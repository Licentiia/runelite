package net.runelite.client.plugins.prayerpredictor;

import java.awt.*;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class NpcLabelsOverlay extends Overlay
{
    private final Client client;
    private final NpcLabelsPlugin plugin;
    private final NpcLabelsConfig config;

    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        // Configure font
        Font base = FontManager.getRunescapeBoldFont();
        if (config.enableFontSizeOverride())
        {
            base = base.deriveFont((float) config.fontSize());
        }
        g.setFont(base);
        FontMetrics fm = g.getFontMetrics();

        for (NPC npc : client.getNpcs())
        {
            if (npc == null || npc.isDead())
                continue;

            final String label = plugin.labelFor(npc);
            if (label == null || label.isEmpty())
                continue;

            LocalPoint lp = npc.getLocalLocation();
            if (lp == null)
                continue;

            int z = npc.getLogicalHeight();
            Point loc = Perspective.localToCanvas(client, lp, client.getPlane(), z + config.verticalOffset());
            if (loc == null)
                continue;

            drawFlatBoxLabel(g, fm, label, loc, plugin.colorForLabel(label));
        }

        return null;
    }

    // === Draws simple clean flat box around text ===
    private void drawFlatBoxLabel(Graphics2D g, FontMetrics fm, String text, Point loc, Color textColor)
    {
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        int padding = 4;
        int boxWidth = textWidth + padding * 2;
        int boxHeight = textHeight;

        int x = loc.getX() - (boxWidth / 2);
        int y = loc.getY() - boxHeight;

        // Background: slight transparency
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(x, y, boxWidth, boxHeight);

        // Border
        g.setColor(Color.WHITE);
        g.drawRect(x, y, boxWidth, boxHeight);

        // Text centered
        g.setColor(textColor);
        g.drawString(text, x + padding, y + fm.getAscent());
    }
}
