package me.ardacraft.updater;

import javafx.application.Platform;
import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import javafx.scene.web.WebView;
import me.dags.ghrelease.download.ProgressWatcher;

import javax.swing.*;
import java.awt.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dags <dags@dags.me>
 */
public class ProgressUI extends JFrame implements ProgressWatcher {

    private final JProgressBar progressBar = new JProgressBar();
    private final AtomicBoolean changesApplied = new AtomicBoolean(false);

    public ProgressUI() {
        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(500, 30));

        JFXPanel webPanel = new JFXPanel();
        webPanel.setPreferredSize(new Dimension(500, 265));

        Platform.runLater(() -> {
            WebView webView = new WebView();
            webView.getEngine().load("https://ardacraft.github.io/modpack/update");
            webPanel.setScene(new Scene(webView));
        });

        Panel root = new Panel();
        root.setPreferredSize(new Dimension(500, 300));
        root.add(webPanel);
        root.add(progressBar);

        this.getInsets().set(0, 0, 0, 0);
        this.add(root);
        this.pack();
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        this.setTitle("Updating ArdaCraft Resources...");
    }

    @Override
    public void update(long taskProgress, long taskCount, long dlProgress, long dlLength) {
        if (!this.isVisible()) {
            this.setVisible(true);
        }
        changesApplied.set(true);
        progressBar.setMaximum((int) dlLength);
        progressBar.setValue((int) dlProgress);
        progressBar.repaint();
    }

    @Override
    public void onComplete() {
        if (changesApplied.get()) {
            changesApplied.set(false);
            JOptionPane.showMessageDialog(this, "ArdaCraft updates complete!");
        }
        this.dispose();
    }
}
