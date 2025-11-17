package net.runelite.client.plugins.vardorvis;

import net.runelite.client.config.*;

@ConfigGroup("vardorvisaxe")
public interface VardorvisAxeConfig extends Config
{
    @ConfigItem(
            keyName = "sky",
            name = "Sky Paths",
            description = "Internal storage",
            secret = true
    )
    default String learned() { return ""; }

    @ConfigItem(
            keyName = "sky",
            name = "Sky Paths",
            description = "Internal storage",
            secret = true
    )
    void learned(String json);
}
