package de.raidcraft.dungeons.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * @author Silthus
 */
@Getter
public abstract class AbstractDungeonInstance implements DungeonInstance {

    private final int id;
    private final Dungeon dungeon;
    private final Map<UUID, DungeonPlayer> players = new HashMap<>();
    @Setter
    private boolean active;
    @Setter
    private boolean completed;
    @Setter
    private boolean locked;
    protected Date creationTime;

    public AbstractDungeonInstance(int id, Dungeon dungeon) {

        this.id = id;
        this.dungeon = dungeon;
    }

    @Override
    public void teleport(DungeonPlayer player) {

        // TODO: duplicated, see PlayerManager
        Player bukkitPlayer = Bukkit.getPlayer(player.getPlayerId());
        if (bukkitPlayer != null) {
            player.setLastPosition(bukkitPlayer.getLocation());
            Location spawnLocation = getDungeon().getSpawnLocation();
            try {
                spawnLocation.setWorld(getWorld());
                bukkitPlayer.teleport(spawnLocation);
            } catch (WorldNotLoadedExpcetion e) {
                e.printStackTrace();
            }
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

        return players.remove(player.getPlayerId());
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

    /**
     * World must exist
     *
     * @return the loaded world
     */
    @Override
    public World loadWorld() {

        return RaidCraft.getComponent(DungeonsPlugin.class).getWorldManager()
                .loadWorld(getDungeon().getSpawnLocation(), getWorldName());
    }
}
