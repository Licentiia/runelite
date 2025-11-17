package net.runelite.client.plugins.projectileprayhelper;

import com.google.inject.Provides;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
        name = PluginDescriptor.Zebe + " Projectile Highlight",
        description = "Shows prayer icon + label for dangerous projectiles (Zulrah preset included).",
        tags = {"projectile","prayer","zulrah","icon","label", "zebe", "cb"}
)
public class ProjectilePrayHelperPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private SpriteManager spriteManager;
    @Inject private ProjectilePrayHelperOverlay overlay;
    @Inject private ProjectilePrayHelperConfig config;
    @Inject private OverlayManager overlayManager; // <-- IMPORTANT
    @Inject private ProjectilePrayHelperQueueOverlay queueOverlay;


    @Getter
    private final Map<Integer, ProjectileRule> rules = new HashMap<>();

    @Provides
    ProjectilePrayHelperConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(ProjectilePrayHelperConfig.class);
    }

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay); // <-- REGISTER OVERLAY
        overlayManager.add(queueOverlay);    // NEW queue panel

        rebuildRules();
        log.debug("Projectile Pray Helper started");
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay); // <-- UNREGISTER
        overlayManager.remove(queueOverlay); // NEW

        rules.clear();
        log.debug("Projectile Pray Helper stopped");
    }

    @Subscribe
    public void onProjectileMoved(ProjectileMoved event)
    {
        // No-op; ensures we're “active” while projectiles are moving.
    }

    @Subscribe
    public void onConfigChanged(net.runelite.client.events.ConfigChanged e)
    {
        if (!ProjectilePrayHelperConfig.GROUP.equals(e.getGroup()))
        {
            return;
        }
        rebuildRules();
    }

    private void rebuildRules()
    {
        rules.clear();

        // ---- Preset: Zulrah (can turn off in config) ----
        if (config.enableZulrahPreset())
        {
            // 1044 => Zulrah magic; 1045 => Zulrah range
            rules.put(1044, new ProjectileRule(PrayerType.MAGIC, "Mage"));
            rules.put(1045, new ProjectileRule(PrayerType.RANGED, "Range"));
        }

        // ---- User overrides: id=label per line ----
        for (ParsedEntry pe : RuleParser.parseLines(config.extraMappings()))
        {
            try
            {
                int id = Integer.parseInt(pe.key);
                PrayerType p = PrayerType.fromLabel(pe.value); // infer icon color/type
                rules.put(id, new ProjectileRule(p, pe.value));
            }
            catch (NumberFormatException ex)
            {
                log.debug("ProjectilePrayHelper: skipped non-int projectile id '{}'", pe.key);
            }
        }

        log.debug("Loaded {} projectile rules", rules.size());
    }

    /* === Helper used by overlay === */
    public ProjectileRule ruleFor(Projectile proj)
    {
        return rules.get(proj.getId());
    }
}
