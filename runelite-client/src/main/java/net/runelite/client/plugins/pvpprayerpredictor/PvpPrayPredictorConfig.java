package net.runelite.client.plugins.pvpprayerpredictor;

import net.runelite.client.config.*;

@ConfigGroup("pvppraypredictor")
public interface PvpPrayPredictorConfig extends Config
{
    @ConfigItem(
            keyName = "enabled",
            name = "Enable",
            description = "Enable PvP Prayer Predictor"
    )
    default boolean enabled() { return true; }

    @ConfigItem(
            keyName = "showPanel",
            name = "Show Panel",
            description = "Show the overlay panel"
    )
    default boolean showPanel() { return true; }

    @ConfigItem(
            keyName = "wildernessOnly",
            name = "Wilderness only",
            description = "Only show in the Wilderness/PvP"
    )
    default boolean wildernessOnly() { return true; }

    @ConfigItem(
            keyName = "detectMode",
            name = "Detection Mode",
            description = "Who to scan for weapon predictions"
    )
    default DetectMode detectMode() { return DetectMode.INTERACT_ONLY; }

    @ConfigItem(
            keyName = "maxEntries",
            name = "Max rows",
            description = "Maximum rows to show in the panel"
    )
    @Range(min = 1, max = 8)
    default int maxEntries() { return 5; }

    @ConfigItem(
            keyName = "showWeaponNames",
            name = "Show weapon names",
            description = "Display weapon name next to prayer suggestion"
    )
    default boolean showWeaponNames() { return true; }

    @ConfigItem(
            keyName = "showAttackerName",
            name = "Show attacker name",
            description = "Display attacker name on the right"
    )
    default boolean showAttackerName() { return true; }

    @ConfigItem(
            keyName = "emptyText",
            name = "Empty text",
            description = "Text to show when no one is attacking you"
    )
    default String emptyText() { return "No attackers"; }

    @ConfigItem(
            keyName = "playSound",
            name = "Play switch sound",
            description = "Play a sound when an attacker switches combat style"
    )
    default boolean playSound() { return true; }

    @ConfigItem(
            keyName = "soundId",
            name = "Sound ID",
            description = "Sound effect ID to play (3923 = GE ping)"
    )
    default int soundId() { return 3923; }

}
