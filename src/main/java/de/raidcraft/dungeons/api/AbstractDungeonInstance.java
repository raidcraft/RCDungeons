package de.raidcraft.dungeons.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
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
    private Map<UUID, DungeonPlayer> cachedPlayers = null;
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

        getInternalPlayers().put(player.getPlayerId(), player);
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

        return getInternalPlayers().remove(player.getPlayerId());
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

        return getInternalPlayers().remove(playerId);
    }

    @Override
    public boolean containsPlayer(DungeonPlayer player) {

        return containsPlayer(player.getPlayerId());
    }

    @Override
    public boolean containsPlayer(UUID playerId) {

        return getInternalPlayers().containsKey(playerId);
    }

    @Override
    public Collection<DungeonPlayer> getPlayers() {

        return getInternalPlayers().values();
    }

    private Map<UUID, DungeonPlayer> getInternalPlayers() {

        if (cachedPlayers == null) {
            // init cache
            this.cachedPlayers = new HashMap<>();
            DungeonsPlugin plugin = RaidCraft.getComponent(DungeonsPlugin.class);
            plugin.getDatabase().find(TDungeonInstancePlayer.class)
                    .where().eq("instance_id", getId()).findList()
                    .stream().forEach(player -> {
                        TDungeonPlayer tDungeonPlayer = player.getPlayer();
                        UUID uuid = tDungeonPlayer.getPlayerId();
                        cachedPlayers.put(uuid, plugin.getPlayerManager().getPlayer(uuid));
                    }
            );
        }
        return cachedPlayers;
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
