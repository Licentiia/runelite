package net.runelite.client.plugins.pvpprayerpredictor;

import java.awt.*;

public enum AttackStyle
{
    MELEE("MELEE", new Color(220, 80, 80)),
    RANGED("RANGED", new Color(110, 200, 110)),
    MAGIC("MAGIC", new Color(100, 160, 255)),
    UNKNOWN("UNKNOWN", Color.GRAY);

    public final String display;
    public final Color color;

    AttackStyle(String display, Color color)
    {
        this.display = display;
        this.color = color;
    }
}
