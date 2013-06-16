package de.raidcraft.dungeons.api;

import org.bukkit.World;

import java.sql.Timestamp;
import java.util.List;

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
    public boolean unloadWorld(boolean force);

    /**
     * Gets the time the instance was created
     *
     * @return creation time
     */
    public Timestamp getCreationTime();

    /**
     * Adds the given player to the dungeon instance.
     *
     * @param player to add to the instance
     */
    public void addPlayer(String player);

    /**
     * Removes the vien player from the dungeon instance if he is in it.
     *
     * @param player to remove from the instance
     * @return true if player was removed, false if he wasn't in the dungeon
     */
    public boolean removePlayer(String player);

    /**
     * Gets all players that are attached to this instance.
     *
     * @return list of players playing in this instance
     */
    public List<String> getPlayers();

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
     * Saves the world and every data to the database and files.
     */
    public void save();
}
