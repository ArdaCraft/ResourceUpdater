package me.ardacraft.updater;

import me.dags.data.NodeAdapter;
import me.dags.data.mapping.ObjectMapper;
import me.dags.data.node.Node;
import me.ardacraft.updater.callback.FileCallback;
import me.ardacraft.updater.settings.Properties;
import me.ardacraft.updater.settings.TimeStamp;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
public class Updater {

    private static final NodeAdapter ADAPTER = NodeAdapter.json();
    private static final ObjectMapper MAPPER = ObjectMapper.builder().adapter(NodeAdapter.json()).build();

    private Properties properties = new Properties();
    private TimeStamp timeStamp = new TimeStamp();
    private File home = new File(new File("").getAbsolutePath(), "");
    private File resourceDir = new File(home, "resourcepacks");
    private File timeStampFile = new File(resourceDir, "timestamp.json");
    private File tempDir = new File(resourceDir, "temp");
    private FileCallback callback = downloaded -> {};
    private boolean initialized = false;

    public void init(File homeDir) throws IOException {
        Utils.log("Initializing...");

        home = homeDir;
        Utils.log("Set home dir to {0}", homeDir);

        resourceDir = new File(home, "resourcepacks");
        Utils.log("Set resourcepack dir to {0}", resourceDir);

        timeStampFile = new File(resourceDir, "timestamp.json");
        Utils.log("Set timestamp file to {0}", timeStampFile);

        tempDir = new File(resourceDir, "temp");
        Utils.log("Set temp dir to {0}", tempDir);

        Utils.log("Reading timestamp file {0}", timeStampFile);
        readTimestamp();

        Utils.log("Reading properties...");
        readProperties();

        initialized = true;
    }

    public void launch() {
        if (!initialized) {
            throw new UnsupportedOperationException("Downloader has not been initialized yet!");
        }
        new Thread() {
            public void run() {
                try {
                    Thread.sleep(TimeUnit.SECONDS.toMillis(properties.startup_delay));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                int error = 3;

                while (true) {
                    try {
                        Updater.this.run();
                        Thread.sleep(TimeUnit.MINUTES.toMillis(properties.interval_mins));
                        error = 3;
                    } catch (Exception e) {
                        if (error-- <= 0) {
                            e.printStackTrace();
                            return;
                        }
                    }
                }
            }
        }.start();
    }

    private void run() throws Exception {
        if (!properties.present()) {
            Utils.log("Properties file not present, unable to run :[");
            return;
        }

        String url = properties.queryUrl();
        Utils.log("Checking for latest release at {0}", url);
        handleResponse(ADAPTER.from(new URL(url)));
    }

    private void readProperties() throws IOException {
        InputStream inputStream = Updater.class.getResourceAsStream("/properties.json");
        Optional<Properties> properties = MAPPER.from(inputStream, Properties.class);
        if (properties.isPresent()) {
            Utils.log("Read properties file {0}", properties.get());
            this.properties = properties.get();
        } else {
            Utils.log("Error reading properties file :[");
        }
    }

    private void readTimestamp() throws IOException {
        Optional<TimeStamp> timeStamp = MAPPER.from(timeStampFile, TimeStamp.class);
        if (timeStamp.isPresent()) {
            this.timeStamp = timeStamp.get();
            Utils.log("Read timestamp {0}", this.timeStamp.time_stamp);
        } else {
            Utils.log("timestamp.json could not be read or does not exist");
        }
    }

    private void handleResponse(Node release) throws Exception {
        if (release.isPresent() && release.isNodeObject() && release.asNodeObject().contains("assets")) {
            // Check response has a published_at entry
            Node published = release.asNodeObject().get("published_at");
            if (!published.isPresent()) {
                Utils.log("Release does not have a publication date :[");
                return;
            }

            // Check if remote has a new release than stored locally
            String publishDate = published.asString();
            if (!timeStamp.olderThan(publishDate)) {
                Utils.log("Nothing new to download");
                return;
            }

            Utils.log("Detected newer release. Attempting download...");
            // Fetch asset download url and commence download
            Node assets = release.asNodeObject().get("assets");
            if (assets.isPresent() && assets.isNodeArray() && !assets.asNodeArray().empty()) {
                Node asset = assets.asNodeArray().get(0);
                if (asset.isPresent() && asset.isNodeObject() && asset.asNodeObject().contains("browser_download_url")) {
                    String downloadUrl = asset.asNodeObject().get("browser_download_url").asString();
                    long size = asset.asNodeObject().get("size").asNumber().longValue();
                    Utils.log("Downloading latest release {0}", downloadUrl);
                    download(downloadUrl, release.asNodeObject().get("name").asString() + ".zip", size, publishDate);
                }
            }
        }
    }

    private void download(String address, String fileName, long size, String timestamp) throws IOException {
        Utils.log("Downloading...");
        boolean complete = false;
        int attempts = 0;

        URL url = new URL(address);
        File tempFile = new File(tempDir, fileName);
        File targetFile = new File(resourceDir, fileName);

        if (targetFile.exists()) {
            Utils.log("File already exists {0}");
            return;
        }

        while (!complete) {
            if (attempts++ > 4) {
                Utils.log("Downloads timed out :[");
                return;
            }
            Utils.log("Download attempt {0}", attempts);
            complete = attemptDownload(url, tempFile, size);
        }

        transfer(tempFile, targetFile, timestamp);
    }
    
    private boolean attemptDownload(URL url, File tempFile, long size) {
        DownloadProgress progress = new DownloadProgress(tempFile.getName());
        try {
            Utils.delete(tempDir);
            Utils.mkdirs(tempDir);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            try (ReadableByteChannel channel = Channels.newChannel(connection.getInputStream())) {
                try (FileOutputStream outputStream = new FileOutputStream(tempFile)) {
                    Utils.log("Downloading to temporary file {0}", tempFile);
                    for (long pos = 0, segment = size / 100L; pos < size; pos += segment) {
                        outputStream.getChannel().transferFrom(channel, pos, segment);
                        progress.update();
                    }
                    progress.complete();
                    Utils.log("Download complete");
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            progress.complete();
        }
    }

    private void transfer(File tempFile, File targetFile, String timestamp) throws IOException {
        Utils.mkdirs(resourceDir);
        Utils.log("Transferring temporary file {0} ==> {1}", tempFile, targetFile);
        try (FileInputStream in = new FileInputStream(tempFile); FileOutputStream out = new FileOutputStream(targetFile)) {
            in.getChannel().transferTo(0L, Long.MAX_VALUE, out.getChannel());
            Utils.log("Transfer complete");
        }

        Utils.log("Cleaning temporary files...");
        Utils.delete(tempDir);
        Utils.log("Transfer complete!");

        Utils.log("Writing timestamp {0}", timestamp);
        timeStamp.time_stamp = timestamp;
        MAPPER.to(timeStamp, timeStampFile);
        Utils.log("Complete!");

        callback.accept(targetFile);
    }
}
