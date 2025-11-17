package net.runelite.client.plugins.friendpraywatcher;

import com.google.inject.Provides;
import lombok.Getter;
import net.runelite.api.*;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = PluginDescriptor.Zebe + " Friend Prayer Watcher",
        description = "Shows what overhead prayer your selected friend is using",
        tags = {"zebe", "cb"},
        enabledByDefault = false
)
public class FriendPrayWatcherPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private FriendPrayWatcherOverlay overlay;
    @Inject private OverlayManager overlayManager;
    @Inject private FriendPrayWatcherConfig config;

    // Last detected head icon
    @Getter
    private HeadIcon friendPrayer = null;

    // Cache friend Player
    private Player trackedFriend = null;

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        friendPrayer = null;
        trackedFriend = null;
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        friendPrayer = null;
        trackedFriend = null;
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged event)
    {
        if (event.getGameState() == GameState.LOGIN_SCREEN ||
                event.getGameState() == GameState.HOPPING)
        {
            friendPrayer = null;
            trackedFriend = null;
        }
    }

    @Subscribe
    public void onPlayerSpawned(PlayerSpawned event)
    {
        Player p = event.getPlayer();
        if (p != null && isTargetFriend(p))
        {
            trackedFriend = p;
        }
    }

    @Subscribe
    public void onPlayerDespawned(PlayerDespawned event)
    {
        Player p = event.getPlayer();
        if (p != null && p == trackedFriend)
        {
            trackedFriend = null;
            friendPrayer = null;
        }
    }

    @Subscribe
    public void onGameTick(GameTick tick)
    {
        if (trackedFriend == null)
        {
            findFriendInScene();
        }

        if (trackedFriend != null)
        {
            friendPrayer = trackedFriend.getOverheadIcon();
        }
    }

    private void findFriendInScene()
    {
        String friendName = config.friendName().trim();

        if (friendName.isEmpty() || client.getPlayers() == null)
            return;

        for (Player p : client.getPlayers())
        {
            if (p != null && isTargetFriend(p))
            {
                trackedFriend = p;
                break;
            }
        }
    }

    private boolean isTargetFriend(Player p)
    {
        return p.getName() != null &&
                p.getName().replace('\u00A0', ' ').equalsIgnoreCase(config.friendName());
    }

    @Provides
    FriendPrayWatcherConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(FriendPrayWatcherConfig.class);
    }
}
