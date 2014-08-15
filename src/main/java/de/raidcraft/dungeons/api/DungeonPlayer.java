package de.raidcraft.dungeons.api;

import org.bukkit.Location;

import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface DungeonPlayer {

    public int getId();

    /**
     * Gets the UUID of the player.
     *
     * @return player name
     */
    public UUID getPlayerId();

    public void setLastPosition(Location lastPosition);

    /**
     * Gets the last position of the player before he was teleported to the dungeon.
     *
     * @return last position of the player
     */
    public Location getLastPosition();

    /**
     * Gets a list of dungeons the player is enlisted in.
     *
     * @return active and inactive dungeons the player is in
     */
    public List<DungeonInstance> getDungeonInstances();

    /**
     * Tries to get an active instance of the given dungeon. If none exists
     * a new one will be created for the player.
     *
     * @param dungeon to get instance for
     *
     * @return new or existing instance
     */
    public DungeonInstance getDungeonInstance(Dungeon dungeon);

    public DungeonInstance getActiveInstance();

    public void leaveActiveDungeon(DungeonReason reason);

    public void save();
}
