package me.ardacraft.updater.launch;

import me.ardacraft.updater.ProgressUI;
import me.dags.ghrelease.Config;
import me.dags.ghrelease.download.DownloadManager;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;
import org.lwjgl.Sys;

import javax.net.ssl.HttpsURLConnection;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class UpdaterTweaker implements ITweaker {

    @Override
    public void acceptOptions(List<String> list, File gameDir, File assetsDir, String s) {
        try {
            URL url = new URL("https://ardacraft.github.io/modpack/update/config.json");
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            try (InputStream inputStream = connection.getInputStream()) {
                Config config = Config.read(inputStream);
                if (config != null) {
                    DownloadManager manager = new DownloadManager(gameDir.toPath(), new ProgressUI());
                    manager.processConfig(config);
                    manager.download();
                }
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("ArdaCraft update checks complete!");
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
