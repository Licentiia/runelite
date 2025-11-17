package net.runelite.client.plugins.pvpprayerpredictor;

import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
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
import java.util.Comparator;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static net.runelite.api.SpriteID.*;

public class PvpPrayPredictorOverlay extends OverlayPanel
{
    private final Client client;
    private final PvpPrayPredictorPlugin plugin;
    private final PvpPrayPredictorConfig config;
    private final SpriteManager spriteManager;

    private BufferedImage iconMage;
    private BufferedImage iconRange;
    private BufferedImage iconMelee;

    @Inject
    public PvpPrayPredictorOverlay(
            Client client,
            PvpPrayPredictorPlugin plugin,
            PvpPrayPredictorConfig config,
            SpriteManager spriteManager)
    {
        this.client = client;
        this.plugin = plugin;
        this.config = config;
        this.spriteManager = spriteManager;

        setPosition(OverlayPosition.TOP_LEFT);
        setLayer(OverlayLayer.ABOVE_WIDGETS);

        panelComponent.setBackgroundColor(new Color(0, 0, 0, 150));
        panelComponent.setPreferredSize(new Dimension(200, 0));
    }

    private double distTo(Player a, Player b)
    {
        if (a == null || b == null) return Double.MAX_VALUE;
        WorldPoint wa = a.getWorldLocation();
        WorldPoint wb = b.getWorldLocation();
        if (wa == null || wb == null) return Double.MAX_VALUE;
        int dx = wa.getX() - wb.getX();
        int dy = wa.getY() - wb.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }


    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (!config.enabled() || !config.showPanel())
        {
            return null;
        }

        ensureIcons();
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(
                TitleComponent.builder()
                        .text("PVP PRAYER")
                        .color(Color.WHITE)
                        .build()
        );

        // Build rows from plugin predictions
        List<Row> rows = plugin.getCurrentPredictions().values().stream()
                // Nearest first
                .sorted(Comparator.comparingDouble(pred ->
                        distTo(client.getLocalPlayer(), pred.getPlayer())))
                .map(p -> new Row(p.getStyle(), p.getWeaponName(), p.getPlayer().getName()))
                .collect(Collectors.toList());

        if (rows.isEmpty())
        {
            panelComponent.getChildren().add(
                    LineComponent.builder()
                            .left(config.emptyText())
                            .leftColor(Color.GRAY)
                            .build()
            );
        }
        else
        {
            int max = Math.max(1, Math.min(8, config.maxEntries()));
            for (int i = 0; i < Math.min(max, rows.size()); i++)
            {
                Row e = rows.get(i);

                BufferedImage icon = getIcon(e.style);
                if (icon != null) panelComponent.getChildren().add(new ImageComponent(icon));

                String label = e.style.display;
                if (config.showWeaponNames() && e.weaponName != null)
                {
                    label += " (" + e.weaponName + ")";
                }

                LineComponent.LineComponentBuilder line = LineComponent.builder()
                        .left(label)
                        .leftColor(e.style.color);

                if (config.showAttackerName() && e.attackerName != null)
                {
                    line.right(e.attackerName);
                    line.rightColor(Color.LIGHT_GRAY);
                }

                panelComponent.getChildren().add(line.build());
            }
        }

        return super.render(graphics);
    }

    private void ensureIcons()
    {
        if (iconMage == null)  iconMage  = spriteManager.getSprite(PRAYER_PROTECT_FROM_MAGIC, 0);
        if (iconRange == null) iconRange = spriteManager.getSprite(PRAYER_PROTECT_FROM_MISSILES, 0);
        if (iconMelee == null) iconMelee = spriteManager.getSprite(PRAYER_PROTECT_FROM_MELEE, 0);
    }

    private BufferedImage getIcon(AttackStyle style)
    {
        switch (style)
        {
            case MAGIC:  return iconMage;
            case RANGED: return iconRange;
            case MELEE:  return iconMelee;
            default:     return null;
        }
    }

    private static final class Row
    {
        final AttackStyle style;
        final String weaponName;
        final String attackerName;
        Row(AttackStyle style, String weaponName, String attackerName)
        {
            this.style = style;
            this.weaponName = weaponName;
            this.attackerName = attackerName;
        }
    }
}
