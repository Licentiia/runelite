package net.runelite.client.plugins.vardorvis;

import com.google.inject.Provides;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.config.ConfigManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(
        name = PluginDescriptor.Zebe + " Vardorvis Axe",
        description = "Marks axe paths toward a fixed center tile.",
        tags = {"vardorvis", "axe", "path", "zebe", "cb"}
)
public class VardorvisAxePlugin extends Plugin
{
    private static final int AXE_STATIC = 12225;

    // adjust to match your arena’s center in your world instance
    private static final WorldPoint CENTER_TILE = new WorldPoint(2454, 9822, 0);

    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private VardorvisAxeOverlay overlay;
    @Inject private ConfigManager configManager;

    private final Map<Integer, List<WorldPoint>> predictedByNpc = new HashMap<>();

    @Provides
    VardorvisAxeConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(VardorvisAxeConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        predictedByNpc.clear();
    }

    @Subscribe
    public void onNpcSpawned(NpcSpawned e)
    {
        NPC npc = e.getNpc();
        if (npc.getId() != AXE_STATIC)
            return;

        WorldPoint spawn = npc.getWorldLocation();
        if (spawn == null) return;

        // draw line from spawn to center
        List<WorldPoint> path = computeLine(spawn, CENTER_TILE);
        predictedByNpc.put(npc.getIndex(), path);
    }

    @Subscribe
    public void onNpcDespawned(NpcDespawned e)
    {
        NPC npc = e.getNpc();
        if (npc.getId() == AXE_STATIC)
            predictedByNpc.remove(npc.getIndex());
    }

    public Collection<List<WorldPoint>> getAllPredictedPaths()
    {
        return predictedByNpc.values();
    }

    // Bresenham line algorithm
    private List<WorldPoint> computeLine(WorldPoint start, WorldPoint end)
    {
        List<WorldPoint> line = new ArrayList<>();
        int x0 = start.getX(), y0 = start.getY();
        int x1 = end.getX(), y1 = end.getY();
        int dx = Math.abs(x1 - x0);
        int dy = Math.abs(y1 - y0);
        int sx = x0 < x1 ? 1 : -1;
        int sy = y0 < y1 ? 1 : -1;
        int err = dx - dy;

        while (true)
        {
            line.add(new WorldPoint(x0, y0, start.getPlane()));
            if (x0 == x1 && y0 == y1)
                break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; x0 += sx; }
            if (e2 < dx)  { err += dx; y0 += sy; }
        }
        return line;
    }
}
