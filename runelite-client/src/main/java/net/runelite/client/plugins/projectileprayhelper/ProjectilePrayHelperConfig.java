package net.runelite.client.plugins.projectileprayhelper;

import net.runelite.client.config.*;

@ConfigGroup(ProjectilePrayHelperConfig.GROUP)
public interface ProjectilePrayHelperConfig extends Config
{
    String GROUP = "projectileprayhelper";

    @ConfigItem(
            keyName = "enabled",
            name = "Enable overlay",
            description = "Master enable/disable"
    )
    default boolean enabled() { return true; }

    @ConfigItem(
            keyName = "enableZulrahPreset",
            name = "Preset: Zulrah (Mage/Range)",
            description = "Adds known Zulrah projectile → prayer rules"
    )
    default boolean enableZulrahPreset() { return true; }

    @ConfigItem(
            keyName = "extraMappings",
            name = "Extra mappings (id=label)",
            description = "One per line: projectileId=Label (e.g. 1044=Mage)"
    )
    default String extraMappings() { return ""; }

    @ConfigItem(
            keyName = "showIcon",
            name = "Show prayer icon",
            description = "Draw Protect-from-Magic/Missiles icon"
    )
    default boolean showIcon() { return true; }

    @ConfigItem(
            keyName = "showLabel",
            name = "Show text label",
            description = "Draw label text box under the icon"
    )
    default boolean showLabel() { return true; }

    @ConfigItem(
            keyName = "onlyIfTargetingLocal",
            name = "Only if targeting me",
            description = "Only show for projectiles targeting you"
    )
    default boolean onlyIfTargetingLocal() { return true; }

    @ConfigItem(
            keyName = "enableFontSizeOverride",
            name = "Enable font size override",
            description = "If on, uses the font size below"
    )
    default boolean enableFontSizeOverride() { return false; }

    @Range(min = 10, max = 32)
    @ConfigItem(
            keyName = "fontSize",
            name = "Font size",
            description = "Text size in points"
    )
    default int fontSize() { return 14; }

    @Range(min = -50, max = 50)
    @ConfigItem(
            keyName = "verticalOffset",
            name = "Vertical offset",
            description = "Adjust vertical position of icon/label (pixels)"
    )
    default int verticalOffset() { return 0; }
}
