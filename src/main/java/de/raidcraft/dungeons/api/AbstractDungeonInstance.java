package de.raidcraft.dungeons.api;

import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.Map;

/**
 * @author Silthus
 */
public abstract class AbstractDungeonInstance implements DungeonInstance {

    private final int id;
    private final Dungeon dungeon;
    private final Map<String, DungeonPlayer> players = new CaseInsensitiveMap<>();
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

        Player bukkitPlayer = Bukkit.getPlayer(player.getName());
        if (bukkitPlayer != null) {
            player.setLastPosition(bukkitPlayer.getLocation());
            Location spawnLocation = getDungeon().getSpawnLocation();
            spawnLocation.setWorld(getWorld());
            bukkitPlayer.teleport(spawnLocation);
        }
    }

    @Override
    public void addPlayer(DungeonPlayer player) {

        players.put(player.getName(), player);
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

        return removePlayer(player.getName());
    }

    @Override
    public DungeonPlayer removePlayer(DungeonPlayer player, boolean teleport) {

        DungeonPlayer dungeonPlayer = removePlayer(player);
        if (teleport) {
            Player bPlayer = Bukkit.getPlayer(player.getName());
            if (bPlayer != null) {
                bPlayer.teleport(player.getLastPosition());
            }
        }
        return dungeonPlayer;
    }

    @Override
    public DungeonPlayer removePlayer(String player) {

        return players.remove(player);
    }

    @Override
    public boolean containsPlayer(DungeonPlayer player) {

        return containsPlayer(player.getName());
    }

    @Override
    public boolean containsPlayer(String player) {

        return players.containsKey(player);
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
