package net.runelite.client.plugins.leviathanrocks;

import net.runelite.api.Client;
import net.runelite.api.GraphicsObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GraphicsObjectCreated;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@PluginDescriptor(
        name = PluginDescriptor.Zebe + " Leviathan Shadows",
        description = "",
        tags = {"leviathan", "zebe", "cb"}
)
public class LeviathanShadowPlugin extends Plugin
{
    @Inject
    private Client client;

    @Inject
    private OverlayManager overlayManager;

    @Inject
    private LeviathanShadowOverlay overlay;

    // Store marked tiles with their creation time
    private final Map<WorldPoint, Instant> markedTiles = new ConcurrentHashMap<>();

    // ✅ Correct Leviathan shadow GFX IDs
    private static final int[] SHADOW_IDS = {2475, 2476, 2477, 2478, 2479, 2480};
    private static final int MARK_DURATION_MS = 5000; // 5 seconds before fading out

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        markedTiles.clear();
    }

    @Subscribe
    public void onGraphicsObjectCreated(GraphicsObjectCreated event)
    {
        GraphicsObject gfx = event.getGraphicsObject();

        for (int id : SHADOW_IDS)
        {
            if (gfx.getId() == id)
            {
                WorldPoint point = WorldPoint.fromLocal(client, gfx.getLocation());
                if (point != null)
                {
                    markedTiles.put(point, Instant.now());
                }
                break;
            }
        }
    }

    // Clean up old tiles
    public Map<WorldPoint, Instant> getMarkedTiles()
    {
        Iterator<Map.Entry<WorldPoint, Instant>> it = markedTiles.entrySet().iterator();
        while (it.hasNext())
        {
            Map.Entry<WorldPoint, Instant> entry = it.next();
            if (Instant.now().toEpochMilli() - entry.getValue().toEpochMilli() > MARK_DURATION_MS)
            {
                it.remove();
            }
        }
        return markedTiles;
    }
}
