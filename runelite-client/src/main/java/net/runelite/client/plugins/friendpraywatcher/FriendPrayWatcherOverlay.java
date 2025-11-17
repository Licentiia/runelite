package net.runelite.client.plugins.friendpraywatcher;

import net.runelite.api.Client;
import net.runelite.api.HeadIcon;
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

import static net.runelite.api.SpriteID.*;

public class FriendPrayWatcherOverlay extends OverlayPanel
{
    private final Client client;
    private final FriendPrayWatcherPlugin plugin;
    private final FriendPrayWatcherConfig config;
    private final SpriteManager spriteManager;

    private BufferedImage iconMage, iconRange, iconMelee;

    @Inject
    public FriendPrayWatcherOverlay(
            Client client,
            FriendPrayWatcherPlugin plugin,
            FriendPrayWatcherConfig config,
            SpriteManager spriteManager
    )
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

    @Override
    public Dimension render(Graphics2D graphics)
    {
        if (config.friendName().isEmpty())
            return null;

        ensureIcons();
        panelComponent.getChildren().clear();

        panelComponent.getChildren().add(
                TitleComponent.builder().text("FRIEND PRAYER").color(Color.WHITE).build()
        );

        HeadIcon icon = plugin.getFriendPrayer();

        if (icon == null)
        {
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("No overhead")
                    .leftColor(Color.GRAY)
                    .build());
            return super.render(graphics);
        }

        BufferedImage prayerIcon = null;
        String label = "";

        switch (icon)
        {
            case MELEE:
                prayerIcon = iconMelee;
                label = "Protect Melee";
                break;

            case RANGED:
                prayerIcon = iconRange;
                label = "Protect Range";
                break;

            case MAGIC:
                prayerIcon = iconMage;
                label = "Protect Magic";
                break;

            default:
                label = "Other/Unknown";
                break;
        }

        if (prayerIcon != null)
            panelComponent.getChildren().add(new ImageComponent(prayerIcon));

        panelComponent.getChildren().add(LineComponent.builder()
                .left(label)
                .leftColor(Color.YELLOW)
                .build());

        return super.render(graphics);
    }

    private void ensureIcons()
    {
        if (iconMage == null)  iconMage = spriteManager.getSprite(PRAYER_PROTECT_FROM_MAGIC, 0);
        if (iconRange == null) iconRange = spriteManager.getSprite(PRAYER_PROTECT_FROM_MISSILES, 0);
        if (iconMelee == null) iconMelee = spriteManager.getSprite(PRAYER_PROTECT_FROM_MELEE, 0);
    }
}
