package de.raidcraft.dungeons.api;

import java.util.List;

/**
 * @author Silthus
 */
public interface DungeonPlayer {

    /**
     * Gets the name of the player.
     *
     * @return player name
     */
    public String getName();

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
     * @return new or existing instance
     */
    public DungeonInstance getDungeonInstance(Dungeon dungeon);
}
