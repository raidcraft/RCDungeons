package de.raidcraft.dungeons;

import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.types.BukkitDungeonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class PlayerManager {

    private final Map<UUID, DungeonPlayer> players = new HashMap<>();

    public PlayerManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    private DungeonsPlugin plugin;

    public void reload() {

        players.clear();
    }

    public boolean playerExists(UUID playerId) {

        return plugin.getDatabase().find(TDungeonPlayer.class)
                .where().eq("player_id", playerId).findUnique() != null;
    }

    public DungeonPlayer getPlayer(UUID playerId) {

        Player bukkitPlayer = Bukkit.getPlayer(playerId);
        TDungeonPlayer tDungeonPlayer = plugin.getDatabase().find(TDungeonPlayer.class)
                .where().eq("player_id", playerId).findUnique();
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

}
