package de.raidcraft.dungeons.api;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface DungeonPlayer {

    int getId();

    /**
     * Gets the UUID of the player.
     *
     * @return player displayName
     */
    UUID getPlayerId();

    void setLastPosition(Location lastPosition);

    /**
     * Gets the last position of the player before he was teleported to the dungeon.
     *
     * @return last position of the player
     */
    Location getLastPosition();

    /**
     * Gets a list of dungeons the player is enlisted in.
     *
     * @return active and inactive dungeons the player is in
     */
    List<DungeonInstance> getDungeonInstances();

    /**
     * Tries to get an active instance of the given dungeon. If none exists
     * a new one will be created for the player.
     *
     * @param dungeon to get instance for
     *
     * @return new or existing instance
     */
    DungeonInstance getDungeonInstance(Dungeon dungeon);

    void addDungeonInstance(DungeonInstance newInstance);

    void removeDungeonInstance(DungeonInstance newInstance);

    DungeonInstance getActiveInstance();

    void setActiveDungeonInstance(DungeonInstance activeInstance);

    void leaveActiveDungeon(DungeonReason reason);

    void save();
}
