package me.ardacraft.updater.launch;

import me.dags.ghrelease.download.ProgressWatcher;

import javax.swing.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dags <dags@dags.me>
 */
public class Updater implements ProgressWatcher {

    private final AtomicBoolean changesApplied = new AtomicBoolean(false);

    @Override
    public void update(long l, long l1, long l2, long l3) {
        changesApplied.set(true);
    }

    @Override
    public void onComplete() {
        if (changesApplied.get()) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }

            JFrame root = new JFrame();
            root.setTitle("Resource updater");
            root.setLocationRelativeTo(null);
            root.setResizable(false);
            root.setVisible(true);
            JOptionPane.showMessageDialog(root, "Your ResourcePack(s) have been updated!");
            root.dispose();
        }
    }
}
