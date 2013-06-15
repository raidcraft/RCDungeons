package de.raidcraft.dungeons.api;

/**
 * @author Silthus
 */
public interface Dungeon {

    /**
     * Gets the refrenced database id of the dungeon.
     *
     * @return database id
     */
    public int getId();

    /**
     * Gets the unique config name of the dungeon.
     *
     * @return unique config name
     */
    public String getName();

    /**
     * Gets the friendly name that can be displayed to players.
     *
     * @return friendly dungeon name
     */
    public String getFriendlyName();

    /**
     * Gets a description of the dungeon.
     *
     * @return short description
     */
    public String getDescription();

    /**
     * Gets the time players have to clear the dungeon before it resets.
     *
     * @return time until dungeon is resettet
     */
    public long getResetTimeMillis();

    /**
     * Creates a new instance of the dungeon for the given party.
     *
     * @param players that are entering the dungeon, most likely the party
     * @return created dungeon instance
     */
    public DungeonInstance createInstance(String... players);

    /**
     * Tries to get an instance of the dungeon for the given player.
     * If the player has no active instance of the dungeon it will create a new one.
     *
     * @param player to get instance for
     * @return new or existing dungeon instance
     */
    public DungeonInstance getInstance(String player);
}
