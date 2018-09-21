package de.raidcraft.dungeons;

import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.api.WorldNotLoadedExpcetion;
import de.raidcraft.dungeons.api.events.RCDungeonInstanceLoadedEvent;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.types.BukkitDungeonPlayer;
import de.raidcraft.reference.Colors;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class PlayerManager implements Listener {

    private final Map<UUID, DungeonPlayer> players = new HashMap<>();

    public PlayerManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
        plugin.registerEvents(this);
    }

    private DungeonsPlugin plugin;

    public void reload() {

        players.clear();
    }

    public void queuePlayerForInstance(Player player, DungeonInstance instance) {

        if (!WorldManager.isLoaded(instance.getWorldName())) {
            // wait on creation, RE_InstanceLoadedEvent teleport players
            return;
        }
        teleportPlayer(player, instance);
    }

    private void teleportPlayer(Player player, DungeonInstance instance) {

        try {
            player.teleport(instance.getWorld().getSpawnLocation());
            player.sendMessage(Colors.Chat.INFO + "Welcome into: " + Colors.Chat.SPECIAL + instance.getWorldName());

        } catch (WorldNotLoadedExpcetion e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID playerId) {

        return plugin.getDatabase().find(TDungeonPlayer.class)
                .where().eq("player_id", playerId).findOne() != null;
    }

    public DungeonPlayer getPlayer(UUID playerId) {

        if (players.containsKey(playerId)) {
            return players.get(playerId);
        }
        Player bukkitPlayer = Bukkit.getPlayer(playerId);
        TDungeonPlayer tDungeonPlayer = plugin.getDatabase().find(TDungeonPlayer.class)
                .where().eq("player_id", playerId).findOne();
        if (tDungeonPlayer == null) {
            // create new TDungeonPlayer
            tDungeonPlayer = new TDungeonPlayer();
            tDungeonPlayer.setPlayerId(playerId);
            if (bukkitPlayer != null) {
                Location location = bukkitPlayer.getLocation();
                tDungeonPlayer.setLastWorld(location.getWorld().getName());
                tDungeonPlayer.setLastX(location.getX());
                tDungeonPlayer.setLastY(location.getY());
                tDungeonPlayer.setLastZ(location.getZ());
                tDungeonPlayer.setLastPitch(location.getPitch());
                tDungeonPlayer.setLastYaw(location.getYaw());
            }
            plugin.getDatabase().save(tDungeonPlayer);
        }
        BukkitDungeonPlayer dungeonPlayer = new BukkitDungeonPlayer(tDungeonPlayer);
        players.put(dungeonPlayer.getPlayerId(), dungeonPlayer);
        return players.get(playerId);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void instanceLoaded(RCDungeonInstanceLoadedEvent event) {

        event.getInstance().getPlayers().stream().forEach(dungeonPlayer -> {
            Player bukkitPlayer = Bukkit.getPlayer(dungeonPlayer.getPlayerId());
            if (bukkitPlayer != null) {
                teleportPlayer(bukkitPlayer, event.getInstance());
            }
        });
    }
}
