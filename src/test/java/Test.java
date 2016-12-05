import me.ardacraft.updater.ProgressUI;
import me.dags.ghrelease.Config;
import me.dags.ghrelease.download.DownloadManager;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author dags <dags@dags.me>
 */
public class Test {

    public static void main(String[] args) {
        final ProgressUI progressUI = new ProgressUI();
        try {
            URL url = new URL("https://ardacraft.github.io/modpack/update/config.json");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                Config config = Config.read(inputStream);
                if (config != null) {
                    Path root = Paths.get("").toAbsolutePath();
                    DownloadManager manager = new DownloadManager(root, progressUI);

                    manager.processConfig(config);
                    manager.download();
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        progressUI.dispose();
        System.out.println("Update checks complete");
    }
}
