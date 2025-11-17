package net.runelite.client.plugins.vardorvis;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.util.Collection;
import java.util.List;

public class VardorvisAxeOverlay extends Overlay
{
    private final Client client;
    private final VardorvisAxePlugin plugin;

    @Inject
    VardorvisAxeOverlay(Client client, VardorvisAxePlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        // Draw predicted tiles – red for each active axe path
        for (List<WorldPoint> path : plugin.getAllPredictedPaths())
        {
            drawTiles(g, path, new Color(255, 0, 0, 80), Color.RED);
        }

        return null;
    }


    private void drawTiles(Graphics2D g, Collection<WorldPoint> tiles, Color fill, Color stroke)
    {
        for (WorldPoint wp : tiles)
        {
            LocalPoint lp = LocalPoint.fromWorld(client, wp);
            if (lp == null) continue;

            Polygon poly = Perspective.getCanvasTilePoly(client, lp);
            if (poly == null) continue;

            g.setColor(fill);
            g.fill(poly);
            g.setColor(stroke);
            g.draw(poly);
        }
    }
}
