package de.raidcraft.dungeons.api;

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
     * Checks if all bosses have been killed and the instance has been cleared.
     *
     * @return true if instance is completed
     */
    public boolean isCompleted();
}
