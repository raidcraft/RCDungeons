package de.raidcraft.dungeons.api;

/**
 * @author Dragonfire
 */
public class WorldNotLoadedExpcetion extends DungeonException {

    public WorldNotLoadedExpcetion(String worldName) {

        super("World not loaded: (" + worldName + ")");
    }
}
