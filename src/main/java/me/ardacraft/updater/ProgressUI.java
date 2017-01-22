package me.ardacraft.updater;

import me.dags.ghrelease.download.ProgressWatcher;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author dags <dags@dags.me>
 */
public class ProgressUI extends JFrame implements ProgressWatcher {

    private final JProgressBar progressBar = new JProgressBar();
    private final AtomicBoolean changesApplied = new AtomicBoolean(false);

    public ProgressUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        final int maxWidth = 400;
        final int maxHeight = 500;

        progressBar.setStringPainted(true);
        progressBar.setMinimum(0);
        progressBar.setMaximum(1);
        progressBar.setValue(0);
        progressBar.setPreferredSize(new Dimension(maxWidth, 30));
        progressBar.setMaximumSize(new Dimension(maxWidth, 30));

        JLayeredPane graphics = new JLayeredPane();
        graphics.setAlignmentX(Component.CENTER_ALIGNMENT);
        graphics.setAlignmentY(Component.CENTER_ALIGNMENT);

        try {
            int availableHeight = maxHeight - (int) progressBar.getPreferredSize().getHeight();
            JLabel background = getImage("/background.jpg", maxWidth, availableHeight);
            JLabel logo = getImage("/logo.png", maxWidth - 50, availableHeight);
            double height = Math.max(background.getPreferredSize().getHeight(), logo.getPreferredSize().getHeight());
            graphics.setPreferredSize(new Dimension(maxWidth, (int) height));

            background.setSize(graphics.getPreferredSize());
            graphics.add(background, JLayeredPane.DEFAULT_LAYER);

            logo.setSize(graphics.getPreferredSize());
            logo.setOpaque(false);
            graphics.add(logo, JLayeredPane.MODAL_LAYER);
        } catch (IOException e) {
            e.printStackTrace();
        }

        double height = progressBar.getPreferredSize().getHeight() + graphics.getPreferredSize().getHeight() + 5;

        Panel root = new Panel();
        root.setPreferredSize(new Dimension(maxWidth, (int) height));
        root.add(graphics);
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

    private static JLabel getImage(String resource, float maxWidth, float maxHeight) throws IOException {
        BufferedImage image = ImageIO.read(ProgressUI.class.getResourceAsStream(resource));
        int max = Math.max(image.getHeight(), image.getWidth());
        float scale = max / maxWidth;
        int width = Math.round(image.getWidth() / scale);
        int height = Math.min(Math.round(image.getHeight() / scale), (int) maxHeight);
        ImageIcon icon = new ImageIcon(image.getScaledInstance(width, height, Image.SCALE_SMOOTH));
        JLabel label = new JLabel(icon);
        label.setPreferredSize(new Dimension(width, height));
        return label;
    }
}
