package de.raidcraft.dungeons.api;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
public abstract class AbstractDungeonInstance implements DungeonInstance {

    private final int id;
    private final Dungeon dungeon;
    private final Map<UUID, DungeonPlayer> players = new HashMap<>();
    private boolean active;
    private boolean completed;
    private boolean locked;
    protected Timestamp creationTime;

    public AbstractDungeonInstance(int id, Dungeon dungeon) {

        this.id = id;
        this.dungeon = dungeon;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public Dungeon getDungeon() {

        return dungeon;
    }

    @Override
    public Timestamp getCreationTime() {

        return creationTime;
    }

    @Override
    public void teleport(DungeonPlayer player) {

        Player bukkitPlayer = Bukkit.getPlayer(player.getPlayerId());
        if (bukkitPlayer != null) {
            player.setLastPosition(bukkitPlayer.getLocation());
            Location spawnLocation = getDungeon().getSpawnLocation();
            spawnLocation.setWorld(getWorld());
            bukkitPlayer.teleport(spawnLocation);
        }
    }

    @Override
    public void addPlayer(DungeonPlayer player) {

        players.put(player.getPlayerId(), player);
    }

    @Override
    public void addPlayer(DungeonPlayer player, boolean teleport) {

        addPlayer(player);
        if (teleport) {
            teleport(player);
        }
    }

    @Override
    public DungeonPlayer removePlayer(DungeonPlayer player) {

        return removePlayer(player);
    }

    @Override
    public DungeonPlayer removePlayer(DungeonPlayer player, boolean teleport) {

        DungeonPlayer dungeonPlayer = removePlayer(player);
        if (teleport) {
            Player bPlayer = Bukkit.getPlayer(player.getPlayerId());
            if (bPlayer != null) {
                bPlayer.teleport(player.getLastPosition());
            }
        }
        return dungeonPlayer;
    }

    @Override
    public DungeonPlayer removePlayer(UUID playerId) {

        return players.remove(playerId);
    }

    @Override
    public boolean containsPlayer(DungeonPlayer player) {

        return containsPlayer(player.getPlayerId());
    }

    @Override
    public boolean containsPlayer(UUID playerId) {

        return players.containsKey(playerId);
    }

    @Override
    public Collection<DungeonPlayer> getPlayers() {

        return players.values();
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
    }

    @Override
    public boolean isCompleted() {

        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {

        this.completed = completed;
    }

    @Override
    public boolean isLocked() {

        return locked || getDungeon().isLocked();
    }

    @Override
    public void setLocked(boolean locked) {

        this.locked = locked;
    }
}
