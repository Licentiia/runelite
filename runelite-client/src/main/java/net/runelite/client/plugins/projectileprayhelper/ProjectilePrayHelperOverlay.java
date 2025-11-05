package net.runelite.client.plugins.projectileprayhelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.inject.Inject;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Client;
import net.runelite.api.Projectile;
import net.runelite.api.Point;
import net.runelite.api.SpriteID;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.Perspective;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

@RequiredArgsConstructor(onConstructor_ = @Inject)
public class ProjectilePrayHelperOverlay extends Overlay
{
    private final Client client;
    private final ProjectilePrayHelperPlugin plugin;
    private final ProjectilePrayHelperConfig config;
    private final SpriteManager spriteManager;

    private BufferedImage iconMage;
    private BufferedImage iconRange;
    private BufferedImage iconMelee;

    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
    }

    @Override
    public Dimension render(Graphics2D g)
    {
        if (!config.enabled())
        {
            return null;
        }

        // Font setup
        Font base = FontManager.getRunescapeBoldFont();
        if (config.enableFontSizeOverride())
        {
            base = base.deriveFont((float) config.fontSize());
        }
        g.setFont(base);
        FontMetrics fm = g.getFontMetrics();

        ensureIconsLoaded();

        for (Projectile proj : client.getProjectiles())
        {
            if (proj == null)
            {
                continue;
            }

            // Optional filter: only if targeting me
            if (config.onlyIfTargetingLocal() && proj.getInteracting() != client.getLocalPlayer())
            {
                continue;
            }

            ProjectileRule rule = plugin.ruleFor(proj);
            if (rule == null)
            {
                continue; // no mapping for this projectile id
            }

            // ✅ FIX: projectile X/Y are LOCAL coords in your RL build — do NOT use fromWorld()
            int projX = (int) proj.getX();
            int projY = (int) proj.getY();
            LocalPoint lp = new LocalPoint(projX, projY);

            int z = (int) proj.getZ(); // projectile height above ground (still fine to cast)
            Point canvas = Perspective.localToCanvas(client, lp, client.getPlane(), z + config.verticalOffset());
            if (canvas == null)
            {
                continue;
            }

            int x = canvas.getX();
            int y = canvas.getY();

            // Draw icon (if enabled)
            if (config.showIcon())
            {
                BufferedImage icon =
                        rule.type == PrayerType.MAGIC ? iconMage :
                                rule.type == PrayerType.RANGED ? iconRange :
                                        rule.type == PrayerType.MELEE ? iconMelee :
                                                null;


                if (icon != null)
                {
                    int iw = icon.getWidth();
                    int ih = icon.getHeight();
                    g.drawImage(icon, x - iw / 2, y - ih - 2, null);
                    y -= ih + 4; // move label down slightly under the icon
                }
            }

            // Draw label (if enabled)
            if (config.showLabel() && rule.label != null && !rule.label.isEmpty())
            {
                drawFlatBoxLabel(g, fm, rule.label, x, y, rule.type.color);
            }
        }

        return null;
    }

    private void ensureIconsLoaded()
    {
        if (iconMage == null)
        {
            iconMage = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MAGIC, 0);
        }
        if (iconRange == null)
        {
            iconRange = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MISSILES, 0);
        }
        if (iconMelee == null) // ✅ NEW
        {
            iconMelee = spriteManager.getSprite(SpriteID.PRAYER_PROTECT_FROM_MELEE, 0);
        }
    }

    /** Simple, flat, clean box centered at (centerX, baselineY). */
    private void drawFlatBoxLabel(Graphics2D g, FontMetrics fm, String text, int centerX, int baselineY, Color textColor)
    {
        int padding = 4;
        int textW = fm.stringWidth(text);
        int textH = fm.getHeight();

        int boxW = textW + padding * 2;
        int boxH = textH;

        int x = centerX - (boxW / 2);
        int y = baselineY - boxH;

        // Background
        g.setColor(new Color(0, 0, 0, 160));
        g.fillRect(x, y, boxW, boxH);

        // Border
        g.setColor(Color.WHITE);
        g.drawRect(x, y, boxW, boxH);

        // Text
        g.setColor(textColor);
        g.drawString(text, x + padding, y + fm.getAscent());
    }
}
