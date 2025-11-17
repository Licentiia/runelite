package net.runelite.client.plugins.friendpraywatcher;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("friendpraywatcher")
public interface FriendPrayWatcherConfig extends Config
{
    @ConfigItem(
            keyName = "friendName",
            name = "Friend Username",
            description = "Exact in-game name of the friend to track"
    )
    default String friendName() { return ""; }
}