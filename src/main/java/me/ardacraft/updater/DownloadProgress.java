package me.ardacraft.updater;

import me.ardacraft.updater.callback.DownloadCallback;

import javax.swing.*;
import java.awt.*;

/**
 * @author dags <dags@dags.me>
 */
public class DownloadProgress implements DownloadCallback {

    private final String name;
    private final JProgressBar progressBar = new JProgressBar();

    public DownloadProgress(String name) {
        this.name = name;
        progressBar.setPreferredSize(new Dimension(350, 45));
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setValue(0);

        new Thread() {
            public void run() {
                JFrame frame = new JFrame();
                frame.add(progressBar);
                frame.pack();
                frame.setTitle("RPUpdater");
                frame.setLocationRelativeTo(null);
                frame.setResizable(false);
                frame.setVisible(true);
            }
        }.start();
    }

    @Override
    public void update() {
        progressBar.setValue(progressBar.getValue() + 1);
        progressBar.setStringPainted(true);
        progressBar.repaint();
    }

    @Override
    public void complete() {
        progressBar.setString("Download Complete: " + name);
    }
}
