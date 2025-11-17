/*
 * Copyright (c) 2019 Abex
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.ui;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.ui.laf.RuneLiteLAF;
import net.runelite.client.util.ImageUtil;

import javax.annotation.Nullable;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

/**
 * Splash screen styled to match the RuinElite × Microbot branding.
 * - Auto-sizes from the logo dimensions
 * - Gradient background + subtle inner glow
 * - Gold-accent typography
 * - Smooth rounded progress bar with animated shimmer
 */
@Slf4j
public class SplashScreen extends JFrame implements ActionListener
{
    private static final String VERSION = loadVersion();

    // ---- Brand palette (tuned to the provided logo) ----
    private static final Color BG_TOP = new Color(13, 17, 24);           // #0D1118
    private static final Color BG_BOTTOM = new Color(6, 9, 13);          // #06090D
    private static final Color PANEL_GLOW = new Color(255, 215, 130, 20);// faint inner gold glow
    private static final Color GOLD = new Color(230, 179, 90);           // headline / accents
    private static final Color GOLD_DARK = new Color(168, 121, 49);
    private static final Color TEXT_PRIMARY = new Color(240, 240, 240);
    private static final Color TEXT_SECONDARY = new Color(180, 180, 180);
    private static final Color PROGRESS_TRACK = new Color(28, 32, 40);
    private static final Color PROGRESS_FILL_A = new Color(255, 201, 90);
    private static final Color PROGRESS_FILL_B = new Color(200, 146, 54);
    private static final Color PROGRESS_SHINE = new Color(255, 255, 255, 70);

    private static final int PAD = 14;
    private static final int ARC = 20;

    private static SplashScreen INSTANCE;

    private final JLabel action = new JLabel("Loading");
    private final JProgressBar progress = new JProgressBar();
    private final JLabel subAction = new JLabel();
    private final Timer timer;     // UI refresh + shimmer animation

    // Animation state
    private volatile double overallProgress = 0;
    private volatile String actionText = "Loading";
    private volatile String subActionText = "";
    private volatile String progressText = null;
    private int shimmerX = 0;

    // Layout helpers
    private int frameWidth;
    private int contentY;

    // Custom content pane that paints gradient + inner glow
    private final JPanel backgroundPanel = new JPanel(null)
    {
        @Override
        protected void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth();
            int h = getHeight();

            // Background gradient
            GradientPaint gp = new GradientPaint(0, 0, BG_TOP, 0, h, BG_BOTTOM);
            g2.setPaint(gp);
            g2.fillRoundRect(0, 0, w, h, ARC, ARC);

            // Subtle inner glow
            g2.setColor(PANEL_GLOW);
            g2.setStroke(new BasicStroke(2f));
            g2.drawRoundRect(1, 1, w - 2, h - 2, ARC, ARC);

            g2.dispose();
        }
    };

    private SplashScreen()
    {
        BufferedImage logo = ImageUtil.loadImageResource(
                SplashScreen.class, "ruinelite/ruinelite-logo-2.png");

        final int LOGO_W = logo.getWidth();
        final int LOGO_H = logo.getHeight();
        frameWidth = Math.max(LOGO_W + PAD * 2, 320);

        // ---- Window setup ----
        setTitle("RuinElite");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setUndecorated(true);
        setIconImages(Arrays.asList(ClientUI.ICON_128, ClientUI.ICON_16));

        setContentPane(backgroundPanel);               // custom painter
        backgroundPanel.setOpaque(false);

        // Smooth text rendering globally for labels
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // ---- Typography ----
        Font titleFont = new Font(Font.DIALOG, Font.BOLD, 16);
        Font smallFont = new Font(Font.DIALOG, Font.PLAIN, 11);
        Font bodyFont = new Font(Font.DIALOG, Font.PLAIN, 12);

        // ---- Header ----
        JLabel titleLabel = new JLabel("RuinElite", SwingConstants.CENTER);
        titleLabel.setForeground(GOLD);
        titleLabel.setFont(titleFont);
        backgroundPanel.add(titleLabel);
        titleLabel.setBounds(0, 6, frameWidth, 22);

        JLabel versionLabel = new JLabel("Build: " + VERSION, SwingConstants.CENTER);
        versionLabel.setForeground(TEXT_SECONDARY);
        versionLabel.setFont(smallFont);
        backgroundPanel.add(versionLabel);
        versionLabel.setBounds(0, 26, frameWidth, 14);

        // ---- Logo centered ----
        JLabel logoLabel = new JLabel(new ImageIcon(logo));
        int logoX = (frameWidth - LOGO_W) / 2;
        contentY = 26 + 14 + PAD; // below version + padding
        backgroundPanel.add(logoLabel);
        logoLabel.setBounds(logoX, contentY, LOGO_W, LOGO_H);
        contentY += LOGO_H + PAD;

        // ---- Status text ----
        action.setForeground(TEXT_PRIMARY);
        action.setHorizontalAlignment(SwingConstants.CENTER);
        action.setFont(bodyFont);
        backgroundPanel.add(action);
        action.setBounds(PAD, contentY, frameWidth - PAD * 2, 18);
        contentY += 18 + 8;

        // ---- Progress bar (rounded, animated) ----
        progress.setVisible(true);
        progress.setForeground(GOLD);
        progress.setBackground(PROGRESS_TRACK);
        progress.setBorder(new EmptyBorder(0, 0, 0, 0));
        progress.setBounds(PAD, contentY, frameWidth - PAD * 2, 16);
        progress.setFont(bodyFont);
        progress.setIndeterminate(false);
        progress.setUI(new SmoothGoldProgressUI());
        backgroundPanel.add(progress);
        contentY += 16 + 6;

        // ---- Sub-action text ----
        subAction.setForeground(TEXT_SECONDARY);
        subAction.setHorizontalAlignment(SwingConstants.CENTER);
        subAction.setFont(smallFont);
        backgroundPanel.add(subAction);
        subAction.setBounds(PAD, contentY, frameWidth - PAD * 2, 16);
        contentY += 16 + PAD;

        // ---- Final frame geometry ----
        setSize(frameWidth, contentY);
        setLocationRelativeTo(null);
        setBackground(new Color(0, 0, 0, 0)); // allow shaped window
        setShape(new RoundRectangle2D.Double(0, 0, getWidth(), getHeight(), ARC, ARC));

        // ---- UI timer for updates + shimmer ----
        timer = new Timer(33, this); // ~30fps
        timer.setRepeats(true);
        timer.start();

        setVisible(true);
    }

    // Custom progress bar UI
    private class SmoothGoldProgressUI extends BasicProgressBarUI
    {
        @Override
        protected void paintDeterminate(Graphics g, JComponent c)
        {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int w = progress.getWidth();
            int h = progress.getHeight();
            int x = 0;
            int y = 0;

            // Track
            g2.setColor(PROGRESS_TRACK);
            g2.fillRoundRect(x, y, w, h, h, h);

            // Fill width based on value
            int amountFull = getAmountFull(progress.getInsets(), w, h);

            // Gold gradient fill
            GradientPaint gp = new GradientPaint(0, y, PROGRESS_FILL_A, 0, y + h, PROGRESS_FILL_B);
            g2.setPaint(gp);
            g2.fillRoundRect(x, y, amountFull, h, h, h);

            // Shimmer stripe
            int stripeWidth = Math.max(h, 18);
            int sx = (shimmerX % (w + stripeWidth)) - stripeWidth;
            GradientPaint shine = new GradientPaint(
                    sx, 0, new Color(255, 255, 255, 0),
                    sx + stripeWidth / 2f, 0, PROGRESS_SHINE,
                    true);
            g2.setPaint(shine);
            g2.fillRoundRect(x, y, amountFull, h, h, h);

            // Border (dark gold)
            g2.setColor(GOLD_DARK.darker());
            g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(x, y, w - 1, h - 1, h, h);

            // Text
            if (progress.isStringPainted())
            {
                String str = progress.getString();
                g2.setFont(progress.getFont());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (w - fm.stringWidth(str)) / 2;
                int ty = (h + fm.getAscent() - fm.getDescent()) / 2;
                g2.setColor(Color.BLACK); // subtle outline
                g2.drawString(str, tx + 1, ty + 1);
                g2.setColor(new Color(250, 250, 250));
                g2.drawString(str, tx, ty);
            }

            g2.dispose();
        }

        @Override
        protected void paintIndeterminate(Graphics g, JComponent c)
        {
            paintDeterminate(g, c); // we control animation ourselves
        }
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        action.setText(actionText);
        subAction.setText(subActionText);

        // Progress bar logic + string
        progress.setMaximum(1000);
        progress.setValue((int) (overallProgress * 1000));

        if (progressText == null)
        {
            progress.setStringPainted(false);
            progress.setString(null);
        }
        else
        {
            progress.setStringPainted(true);
            progress.setString(progressText);
        }

        // Animate shimmer
        shimmerX += 6;
        progress.repaint();
    }

    public static boolean isOpen()
    {
        return INSTANCE != null;
    }

    public static void init()
    {
        try
        {
            SwingUtilities.invokeAndWait(() ->
            {
                if (INSTANCE != null)
                {
                    return;
                }

                try
                {
                    boolean hasLAF = UIManager.getLookAndFeel() instanceof RuneLiteLAF;
                    if (!hasLAF)
                    {
                        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                    }
                    INSTANCE = new SplashScreen();
                }
                catch (Exception e)
                {
                    log.warn("Unable to start splash screen", e);
                }
            });
        }
        catch (InterruptedException | InvocationTargetException bs)
        {
            throw new RuntimeException(bs);
        }
    }

    public static void stop()
    {
        SwingUtilities.invokeLater(() ->
        {
            if (INSTANCE == null)
            {
                return;
            }

            INSTANCE.timer.stop();
            // The CLOSE_ALL_WINDOWS quit strategy on MacOS dispatches WINDOW_CLOSING events to each frame
            // from Window.getWindows. However, getWindows uses weak refs and relies on gc to remove windows
            // from its list, causing events to get dispatched to disposed frames. The frames handle the events
            // regardless of being disposed and will run the configured close operation. Set the close operation
            // to DO_NOTHING_ON_CLOSE prior to disposing to prevent this.
            INSTANCE.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            INSTANCE.dispose();
            INSTANCE = null;
        });
    }

    public static void stage(double overallProgress, @Nullable String actionText, String subActionText)
    {
        stage(overallProgress, actionText, subActionText, null);
    }

    public static void stage(double startProgress, double endProgress,
                             @Nullable String actionText, String subActionText,
                             int done, int total, boolean mib)
    {
        String progress;
        if (mib)
        {
            final double MiB = 1024 * 1024;
            final double CEIL = 1.d / 10.d;
            progress = String.format("%.1f / %.1f MiB", done / MiB, (total / MiB) + CEIL);
        }
        else
        {
            progress = done + " / " + total;
        }
        stage(startProgress + ((endProgress - startProgress) * done / total), actionText, subActionText, progress);
    }

    public static void stage(double overallProgress, @Nullable String actionText, String subActionText, @Nullable String progressText)
    {
        if (INSTANCE != null)
        {
            INSTANCE.overallProgress = overallProgress;
            if (actionText != null)
            {
                INSTANCE.actionText = actionText;
            }
            INSTANCE.subActionText = subActionText;
            INSTANCE.progressText = progressText;
        }
    }

    private static String loadVersion()
    {
        try
        {
            var props = new java.util.Properties();
            props.load(SplashScreen.class.getResourceAsStream("/version.properties"));
            return props.getProperty("combined.version", "unknown");
        }
        catch (Exception e)
        {
            return "unknown";
        }
    }
}
