package de.raidcraft.dungeons.creator;

import org.bukkit.WorldCreator;

/**
 * @author Silthus
 */
public class DungeonWorldCreator extends WorldCreator {

    /**
     * Creates an empty WorldCreationOptions for the given world name
     *
     * @param name Name of the world that will be created
     */
    public DungeonWorldCreator(String name) {

        super(name);
    }
}
