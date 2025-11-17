package net.runelite.client.plugins.leviathanrocks;

import net.runelite.api.Client;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;
import java.util.Map;

public class LeviathanShadowOverlay extends Overlay
{
    private final Client client;
    private final LeviathanShadowPlugin plugin;

    @Inject
    LeviathanShadowOverlay(Client client, LeviathanShadowPlugin plugin)
    {
        this.client = client;
        this.plugin = plugin;
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Map<WorldPoint, Instant> tiles = plugin.getMarkedTiles();

        for (WorldPoint point : tiles.keySet())
        {
            LocalPoint local = LocalPoint.fromWorld(client, point);
            if (local == null)
                continue;

            Polygon tilePoly = Perspective.getCanvasTilePoly(client, local);
            if (tilePoly != null)
            {
                graphics.setColor(new Color(255, 0, 0, 80)); // semi-transparent fill
                graphics.fill(tilePoly);
                graphics.setColor(Color.RED);                // border
                graphics.draw(tilePoly);
            }
        }

        return null;
    }
}
