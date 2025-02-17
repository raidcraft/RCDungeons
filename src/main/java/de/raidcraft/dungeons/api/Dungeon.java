package de.raidcraft.dungeons.api;

import org.bukkit.Location;
import org.bukkit.World;

import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
public interface Dungeon {

    /**
     * Gets the refrenced database id of the dungeon.
     *
     * @return database id
     */
    int getId();

    /**
     * Gets the unique config displayName of the dungeon.
     *
     * @return unique config displayName
     */
    String getName();

    /**
     * Gets the friendly displayName that can be displayed to players.
     *
     * @return friendly dungeon displayName
     */
    String getFriendlyName();

    void setFriendlyName(String friendlyName);

    /**
     * Gets a description of the dungeon.
     *
     * @return short description
     */
    String getDescription();

    void setDescription(String description);

    /**
     * Gets the time players have to clear the dungeon before it resets.
     *
     * @return time until dungeon is resettet
     */
    long getResetTimeMillis();

    void setResetTimeMillis(long resetTime);

    /**
     * Gets the location where players spawn when they initially enter the dungeon.
     *
     * @return default spawn location
     */
    Location getSpawnLocation();

    void setSpawnLocation(Location location);

    /**
     * Locks the dungeon preventing the creation of new dungeon instances and prevents
     * all created instances from beeing entered.
     *
     * @return true if dungeon is locked
     */
    boolean isLocked();

    /**
     * Locks the dungeon preventing new creation of dungeon instances. Also prevents
     * players in active instances from entering the instance.
     *
     * @param locked state of the dungeon
     */
    void setLocked(boolean locked);

    String getTemplateWorldName();

    World getTemplateWorld();

    void setTemplateWorld(World templateWorld);

    /**
     * Gets a list of active instaces of this dungeon.
     *
     * @return list of active instances
     */
    List<DungeonInstance> getActiveInstances();

    /**
     * Gets all instances that where created for this dungeon.
     *
     * @return list of all instances
     */
    List<DungeonInstance> getInstances();

    /**
     * Creates a new instance of the dungeon for the given party.
     *
     * @param players that are entering the dungeon, most likely the party
     *
     * @return created dungeon instance
     */
    DungeonInstance createInstance(UUID... players);

    /**
     * Tries to get an instance of the dungeon for the given player.
     * If the player has no active instance of the dungeon it will create a new one.
     *
     * @param player to get instance for
     *
     * @return new or existing dungeon instance
     */
    DungeonInstance getActiveInstance(UUID player);

    void save();
}
