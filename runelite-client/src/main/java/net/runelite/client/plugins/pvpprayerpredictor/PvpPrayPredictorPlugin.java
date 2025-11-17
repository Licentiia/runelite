package net.runelite.client.plugins.pvpprayerpredictor;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.kit.KitType;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import net.runelite.client.callback.ClientThread;


import javax.inject.Inject;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@PluginDescriptor(
        name = PluginDescriptor.Zebe + " PvP Prayer Predictor",
        description = "Predicts the correct overhead prayer in PvP based on the attacker's weapon (early weapon read)",
        tags = {"pvp", "prayer", "panel", "predictor", "zebe", "cb"},
        enabledByDefault = false
)
public class PvpPrayPredictorPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;

    @Inject private OverlayManager overlayManager;
    @Inject private PvpPrayPredictorOverlay overlay;
    @Inject private PvpPrayPredictorConfig config;

    // Cache: who is attacking you -> predicted style & label
    @Getter
    private final Map<Integer, Prediction> currentPredictions = new HashMap<>();
    // Small debounce so the panel doesn't flicker on instant swaps
    private final Map<Integer, Integer> staleTimers = new HashMap<>();

    private static final int STALE_TICKS = 3; // keep an entry visible a couple ticks after swap/lost target

    @Override
    protected void startUp()
    {
        overlayManager.add(overlay);
        currentPredictions.clear();
        staleTimers.clear();

        clientThread.invoke(() -> client.playSoundEffect(config.soundId())); // GE ping test
    }

    @Override
    protected void shutDown()
    {
        overlayManager.remove(overlay);
        currentPredictions.clear();
        staleTimers.clear();
    }

    @Subscribe
    public void onGameStateChanged(GameStateChanged e)
    {
        if (e.getGameState() == GameState.HOPPING || e.getGameState() == GameState.LOGIN_SCREEN)
        {
            currentPredictions.clear();
            staleTimers.clear();
        }
    }

    @Subscribe
    public void onClientTick(ClientTick tick)
    {
        if (!config.enabled()) return;
        if (client.getGameState() != GameState.LOGGED_IN) return;

        Player local = client.getLocalPlayer();
        if (local == null) return;

        // Optionally gate to Wilderness / PvP worlds
        if (config.wildernessOnly() && !isInWilderness()) return;

        // Build new predictions this tick
        Map<Integer, Prediction> newPredictions = new HashMap<>();

        final List<Player> players = client.getPlayers();
        if (players == null) return;

        for (Player p : players)
        {
            if (p == null || p == local) continue;

            // Only show players actively targeting you (interact-only)
            if (config.detectMode() == DetectMode.INTERACT_ONLY && p.getInteracting() != local) continue;

            // In scan-nearby mode, we still only care about potential attackers in render range
            if (config.detectMode() == DetectMode.SCAN_NEARBY && p.getInteracting() != local)
            {
                // Optional: you can skip non-targeting players unless skulled / in combat, etc.
                continue;
            }

            int pid = p.getId();
            int weaponId = readWeaponId(p);
            if (weaponId <= 0) continue;

            AttackStyle style = WeaponClassifier.classify(weaponId);
            if (style == AttackStyle.UNKNOWN) continue;

            String weaponName = itemName(weaponId);
            newPredictions.put(pid, new Prediction(p, style, weaponName));
        }

// Merge with previous to reduce flicker; keep stale for a short time
// Remove or decrement stale for missing entries
        Set<Integer> still = newPredictions.keySet();

// ✅ NEW VERSION starts here
        for (Map.Entry<Integer, Prediction> en : newPredictions.entrySet())
        {
            int pid = en.getKey();
            Prediction newPred = en.getValue();
            Prediction oldPred = currentPredictions.get(pid);

            currentPredictions.put(pid, newPred);
            staleTimers.put(pid, STALE_TICKS);

            if (config.playSound()
                    && oldPred != null
                    && oldPred.getStyle() != newPred.getStyle())
            {
                clientThread.invoke(() -> client.playSoundEffect(config.soundId()));
            }
        }
// ✅ NEW VERSION ends here

        // handle stale for ones that disappeared
        List<Integer> toRemove = new ArrayList<>();
        for (Integer key : new ArrayList<>(currentPredictions.keySet()))
        {
            if (!still.contains(key))
            {
                int t = staleTimers.getOrDefault(key, 0) - 1;
                if (t <= 0) toRemove.add(key);
                else staleTimers.put(key, t);
            }
        }
        for (Integer r : toRemove)
        {
            currentPredictions.remove(r);
            staleTimers.remove(r);
        }

        // Optionally cap list length (like your PvM queue)
        int max = Math.max(1, Math.min(8, config.maxEntries()));
        if (currentPredictions.size() > max)
        {
            // keep nearest to you first
            List<Map.Entry<Integer, Prediction>> sorted = currentPredictions.entrySet().stream()
                    .sorted(Comparator.comparingDouble(e -> distTo(local, e.getValue().player)))
                    .limit(max)
                    .collect(Collectors.toList());

            Map<Integer, Prediction> trimmed = new LinkedHashMap<>();
            for (Map.Entry<Integer, Prediction> e : sorted) trimmed.put(e.getKey(), e.getValue());
            currentPredictions.clear();
            currentPredictions.putAll(trimmed);
        }
    }

    private boolean isInWilderness()
    {
        Widget wildy = client.getWidget(WidgetInfo.PVP_WILDERNESS_LEVEL);
        return wildy != null; // very simple heuristic; expand if you want PvP worlds check
    }

    private static double distTo(Player a, Player b)
    {
        if (a == null || b == null) return Double.MAX_VALUE;
        WorldPoint wa = a.getWorldLocation();
        WorldPoint wb = b.getWorldLocation();
        if (wa == null || wb == null) return Double.MAX_VALUE;
        int dx = wa.getX() - wb.getX();
        int dy = wa.getY() - wb.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }

    private int readWeaponId(Player p)
    {
        // Preferred: equipment container (RuneLite exposes via PlayerComposition in most cases)
        PlayerComposition comp = p.getPlayerComposition();
        if (comp != null)
        {
            int id = comp.getEquipmentId(KitType.WEAPON);
            if (id > 0) return id;
        }
        // Fallback: from ItemContainer if available (usually inventory only, so we keep comp path)
        return -1;
    }

    private String itemName(int itemId)
    {
        final ItemComposition ic = client.getItemDefinition(itemId);
        return ic != null ? ic.getName() : ("item:" + itemId);
    }

    @Provides
    PvpPrayPredictorConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PvpPrayPredictorConfig.class);
    }

    // Simple holder
    @Getter
    static class Prediction
    {
        final Player player;
        final AttackStyle style;
        final String weaponName;

        Prediction(Player player, AttackStyle style, String weaponName)
        {
            this.player = player;
            this.style = style;
            this.weaponName = weaponName;
        }
    }
}
