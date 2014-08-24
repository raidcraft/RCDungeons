package de.raidcraft.dungeons.api;

import org.bukkit.World;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface DungeonInstance {

    /**
     * Gets the refrenced database id of the instance.
     *
     * @return database id
     */
    public int getId();

    /**
     * Gets the dungeon template this instance was created of.
     *
     * @return dungeon the instance is attached to
     */
    public Dungeon getDungeon();

    /**
     * Gets the world the dungeon was created in. Each dungeon instance has
     * its own world that will be destroyed and created on demand.
     *
     * @return unique world for the dungeon instance
     */
    public World getWorld();

    public String getWorldName(); // to get the world without load it

    /**
     * Loads the world that was created for the dungeon, so that players can enter it.
     */
    public World loadWorld();

    /**
     * Unloads the world from memory. Useful to save memory when players are not playing the dungeon.
     *
     * @param force true will kick players out of the dungeon.
     *
     * @return true if world was unloaded, false if world could not be unloaded
     */
    public boolean unload(boolean force);

    /**
     * Unloads the dungeon instance and then deletes the world from the harddisk.
     *
     * @param force false will not delete and unload the world when it is still used.
     *
     * @return true if deletion of the world was successful
     */
    public boolean delete(boolean force);

    /**
     * Gets the time the instance was created
     *
     * @return creation time
     */
    public Date getCreationTime();

    public void teleport(DungeonPlayer player);

    /**
     * Adds the given player to the dungeon instance.
     *
     * @param player to add to the instance
     */
    public void addPlayer(DungeonPlayer player);

    public void addPlayer(DungeonPlayer player, boolean teleport);

    /**
     * Removes the vien player from the dungeon instance if he is in it.
     *
     * @param player to remove from the instance
     *
     * @return true if player was removed, false if he wasn't in the dungeon
     */
    public DungeonPlayer removePlayer(DungeonPlayer player);

    public DungeonPlayer removePlayer(DungeonPlayer player, boolean teleport);

    /**
     * Removes the vien player from the dungeon instance if he is in it.
     *
     * @param playerId to remove from the instance
     *
     * @return true if player was removed, false if he wasn't in the dungeon
     */
    public DungeonPlayer removePlayer(UUID playerId);

    /**
     * Checks if the dungeon instance contains the player. Will also return true if
     * the player is not in the instance and only registered for it.
     *
     * @param player to check for
     *
     * @return true if player is registered in this instance
     */
    public boolean containsPlayer(DungeonPlayer player);

    /**
     * Checks if the dungeon instance contains the player. Will also return true if
     * the player is not in the instance and only registered for it.
     *
     * @param playerId to check for
     *
     * @return true if player is registered in this instance
     */
    public boolean containsPlayer(UUID playerId);

    /**
     * Gets all players that are attached to this instance.
     *
     * @return list of players playing in this instance
     */
    public Collection<DungeonPlayer> getPlayers();

    /**
     * Checks if the instance is currently used by players.
     *
     * @return true if instance is used by players
     */
    public boolean isActive();

    /**
     * Marks the dungeon instance as active or inactive depending if players are playing it.
     *
     * @param active true if players are inside
     */
    public void setActive(boolean active);

    /**
     * Checks if all bosses have been killed and the instance has been cleared.
     *
     * @return true if instance is completed
     */
    public boolean isCompleted();

    /**
     * Marks the dungeon aus completed when the players cleared out all enemies.
     *
     * @param completed true if dungeon is cleared
     */
    public void setCompleted(boolean completed);

    /**
     * Checks if the dungeon is locked by an admin or other reasons. This prevents
     * players from entering the dungeon instance.
     * Will also check if the base dungeon template is locked.
     *
     * @return true if the base dungeon or the instance is locked.
     */
    public boolean isLocked();

    /**
     * Sets the dungeon instance as locked preventing player access.
     *
     * @param locked true if players cannot enter
     */
    public void setLocked(boolean locked);

    /**
     * Saves the world and every data to the database and files.
     */
    public void save();
}
