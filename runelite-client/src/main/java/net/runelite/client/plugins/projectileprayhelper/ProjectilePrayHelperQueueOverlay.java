package net.runelite.client.plugins.projectileprayhelper;

import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.SpriteID;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ImageComponent;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ProjectilePrayHelperQueueOverlay extends OverlayPanel
{
    private final Client client;
    private final ProjectilePrayHelperPlugin plugin;
    private final ProjectilePrayHelperConfig config;
    private final SpriteManager spriteManager;

    private BufferedImage iconMage;
    private BufferedImage iconRange;
    private BufferedImage iconMelee;

    @Inject
    public ProjectilePrayHelperQueueOverlay(
            Client client,
            ProjectilePrayHelperPlugin plugin,
            ProjectilePrayHelperConfig config,
            SpriteManager spriteManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;

        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

        panelComponent.setBackgroundColor(new Color(0, 0, 0, 150));
        panelComponent.setPreferredSize(new Dimension(160, 0));
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.enabled() || !config.showQueuePanel())
        {
            return null;
        }

        ensureIcons();
        panelComponent.getChildren().clear();

        // Title
        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text("PRAYER QUEUE")
                        .color(Color.WHITE)
                        .build()
        );

        List<Entry> rows = collectQueue();

        if (rows.isEmpty())
        {
            panelComponent.getChildren().add(
                    LineComponent.builder()
                            .left(config.queueEmptyText())
                            .leftColor(Color.GRAY)
                            .build()
            );
        }
        else
        {
            for (Entry e : rows)
            {
                BufferedImage icon = getIcon(e.type);

                LineComponent.LineComponentBuilder line = LineComponent.builder();

                // icon placeholder on left
                if (icon != null)
                {
                    panelComponent.getChildren().add(new ImageComponent(icon));
                }

                String label = (e.label != null && !e.label.isEmpty())
                        ? e.label.toUpperCase()
                        : e.type.name();

                line.left(label)
                        .leftColor(e.type.color);

                if (config.showTickCountdown())
                {
                    line.right(e.ticks + "t")
                            .rightColor(Color.LIGHT_GRAY);
                }

                panelComponent.getChildren().add(line.build());
            }
        }

        return super.render(graphics);
    }

    private List<Entry> collectQueue()
    {
        List<Entry> entries = new ArrayList<>();

        for (Projectile p : client.getProjectiles())
        {
            if (p == null) continue;
            if (config.onlyIfTargetingLocal() && p.getInteracting() != client.getLocalPlayer()) continue;

            ProjectileRule rule = plugin.ruleFor(p);
            if (rule == null || rule.type == null) continue;

            int cycles = p.getRemainingCycles();
            if (cycles <= 0) continue;

            int ticks = (cycles + 29) / 30;
            entries.add(new Entry(rule.type, rule.label, ticks));
        }

        entries.sort(Comparator.comparingInt(e -> e.ticks));

        List<Entry> collapsed = new ArrayList<>();
        PrayerType last = null;
        for (Entry e : entries)
        {
            if (e.type != last)
            {
                collapsed.add(e);
                last = e.type;
            }
        }

        int max = Math.max(1, Math.min(8, config.queueEntries()));
        if (collapsed.size() > max)
        {
            collapsed = collapsed.subList(0, max);
        }

        return collapsed;
    }

    private void ensureIcons()
    {
        if (iconMage == null)  iconMage  = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
        if (iconRange == null) iconRange = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
        if (iconMelee == null) iconMelee = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
    }

    private BufferedImage getIcon(PrayerType type)
    {
        switch (type)
        {
            case MAGIC:  return iconMage;
            case RANGED: return iconRange;
            case MELEE:  return iconMelee;
            default:     return null;
        }
    }

    private static final class Entry
    {
        final PrayerType type;
        final String label;
        final int ticks;

        Entry(PrayerType type, String label, int ticks)
        {
            this.type = type;
            this.label = label;
            this.ticks = ticks;
        }
    }
}
