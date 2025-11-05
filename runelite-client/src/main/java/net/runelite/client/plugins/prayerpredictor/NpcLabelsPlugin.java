package net.runelite.client.plugins.prayerpredictor;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.NPC;
import net.runelite.api.Client;
import net.runelite.client.events.ConfigChanged;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = "NPC  Prayer Indicator",
        description = "Draws a custom label above NPCs based on ID (or name, if enabled).",
        tags = {"npc","label","overhead","pvm","pvp"}
)
public class NpcLabelsPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private OverlayManager overlayManager;
    @Inject private NpcLabelsOverlay overlay;
    @Inject private NpcLabelsConfig config;

    @Getter
    private final Map<Integer, String> idToLabel = new HashMap<>();

    @Getter
    private final Map<String, String> nameToLabel = new HashMap<>(); // lowercase npc name -> label

    @Getter
    private final Map<String, Color> labelToColor = new HashMap<>(); // label -> color

    @Override
    protected void startUp()
    {
        reloadAllMaps();
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        idToLabel.clear();
        nameToLabel.clear();
        labelToColor.clear();
    }

    @Provides
    NpcLabelsConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NpcLabelsConfig.class);
    }

    private void reloadAllMaps()
    {
        parseIdLabelList(config.idLabelList());
        parseNameLabelList(config.nameLabelList());
        parseLabelColorList(config.labelColorList());
    }

    private void parseIdLabelList(String list)
    {
        idToLabel.clear();
        for (ParsedEntry e : Parser.parseLines(list))
        {
            try
            {
                int id = Integer.parseInt(e.key);
                idToLabel.put(id, e.value);
            }
            catch (NumberFormatException ignored)
            {
                log.debug("NPC Labels: Skipped non-integer ID '{}'", e.key);
            }
        }
    }

    private void parseNameLabelList(String list)
    {
        nameToLabel.clear();
        for (ParsedEntry e : Parser.parseLines(list))
        {
            nameToLabel.put(e.key.toLowerCase(), e.value);
        }
    }

    private void parseLabelColorList(String list)
    {
        labelToColor.clear();
        for (ParsedEntry e : Parser.parseLines(list))
        {
            Color c = Parser.parseColor(e.value);
            if (c != null)
            {
                labelToColor.put(e.key, c);
            }
        }
    }

    @net.runelite.client.eventbus.Subscribe
    public void onConfigChanged(ConfigChanged e)
    {
        if (!e.getGroup().equals(NpcLabelsConfig.GROUP)) return;
        reloadAllMaps();
    }

    @net.runelite.client.eventbus.Subscribe
    public void onNpcSpawned(NpcSpawned e)
    {
        // no-op; overlay reads directly from client each frame
    }

    @net.runelite.client.eventbus.Subscribe
    public void onNpcDespawned(NpcDespawned e)
    {
        // no-op
    }

    /* === Helpers used by overlay === */

    public String labelFor(NPC npc)
    {
        // Priority: ID map → (if enabled) name map → null
        String byId = idToLabel.get(npc.getId());
        if (byId != null)
        {
            return byId;
        }
        if (config.enableNameMapping())
        {
            String name = npc.getName();
            if (name != null)
            {
                return nameToLabel.get(name.toLowerCase());
            }
        }
        return null;
    }

    public Color colorForLabel(String label)
    {
        if (config.enablePerLabelColor())
        {
            Color c = labelToColor.get(label);
            if (c != null) return c;
        }
        return Color.WHITE;
    }
}
