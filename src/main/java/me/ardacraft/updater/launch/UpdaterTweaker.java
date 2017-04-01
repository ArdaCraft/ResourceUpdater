package me.ardacraft.updater.launch;

import me.dags.ghrelease.Config;
import me.dags.ghrelease.download.DownloadManager;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author dags <dags@dags.me>
 */
public class UpdaterTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> list, File gameDir, File assetsDir, String s) {
        new Thread(() -> {
            try {
                while (true) {
                    Thread.sleep(5000L);
                    try {
                        URL url = new URL("https://ardacraft.github.io/modpacks/resources.json");
                        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                        try (InputStream inputStream = connection.getInputStream()) {
                            Config config = Config.read(inputStream);
                            if (config != null) {
                                DownloadManager manager = new DownloadManager(gameDir.toPath(), new Updater());
                                manager.processConfig(config);
                                manager.download();
                            }
                        }
                        connection.disconnect();
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                    Thread.sleep(TimeUnit.MINUTES.toMillis(60L));
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader launchClassLoader) {}

    @Override
    public String getLaunchTarget() {
        return "net.minecraft.client.main.Main";
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
