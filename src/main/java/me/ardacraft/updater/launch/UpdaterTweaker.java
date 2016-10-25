package me.ardacraft.updater.launch;

import me.ardacraft.updater.Updater;
import me.ardacraft.updater.Utils;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author dags <dags@dags.me>
 */
public class UpdaterTweaker implements ITweaker {

    private final Updater downloader = new Updater();

    @Override
    public void acceptOptions(List<String> list, File gameDir, File assetsDir, String s) {
        try {
            downloader.init(gameDir);
            downloader.launch();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
