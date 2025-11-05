package net.runelite.client.plugins.prayerpredictor;

import net.runelite.client.config.*;

@ConfigGroup(NpcLabelsConfig.GROUP)
public interface NpcLabelsConfig extends Config
{
    String GROUP = "npclabels";

    @ConfigItem(
            keyName = "idLabelList",
            name = "ID → Label list",
            description = "One per line: npcId=Label (e.g. 2042=Magic)"
    )
    default String idLabelList()
    {
        return "";
    }

    // --- Optional features (OFF by default) ---

    @ConfigItem(
            keyName = "enableNameMapping",
            name = "Enable name mapping",
            description = "If on, also use the Name → Label list below."
    )
    default boolean enableNameMapping()
    {
        return false;
    }

    @ConfigItem(
            keyName = "nameLabelList",
            name = "Name → Label list",
            description = "One per line: npcName=Label (case-insensitive, e.g. zombie=Melee)"
    )
    default String nameLabelList()
    {
        return "";
    }

    @ConfigItem(
            keyName = "enablePerLabelColor",
            name = "Enable per-label colors",
            description = "If on, use the Label → Color list below."
    )
    default boolean enablePerLabelColor()
    {
        return false;
    }

    @ConfigItem(
            keyName = "labelColorList",
            name = "Label → Color list",
            description = "One per line: Label=#RRGGBB (e.g. Melee=#FF4D4D)"
    )
    default String labelColorList()
    {
        return "";
    }

    @ConfigItem(
            keyName = "enableFontSizeOverride",
            name = "Enable font size override",
            description = "If on, use the font size slider."
    )
    default boolean enableFontSizeOverride()
    {
        return false;
    }

    @Range(min = 8, max = 32)
    @ConfigItem(
            keyName = "fontSize",
            name = "Font size",
            description = "Text size in points (only used if override is enabled)."
    )
    default int fontSize()
    {
        return 14;
    }


    @ConfigItem(
            keyName = "verticalOffset",
            name = "Vertical offset",
            description = "Adjust how high above the head the label sits (pixels)."
    )
    @Range(min = -50, max = 50)
    default int verticalOffset()
    {
        return 0;
    }

    @ConfigItem(
            keyName = "hideDefaultName",
            name = "Hide default NPC name (best-effort)",
            description = "Attempts to suppress the game/client nameplate if a label is shown. Not guaranteed."
    )
    default boolean hideDefaultName()
    {
        return false;
    }
}
