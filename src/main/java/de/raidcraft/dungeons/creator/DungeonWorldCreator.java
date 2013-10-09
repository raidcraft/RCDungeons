package de.raidcraft.dungeons.creator;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.worldedit.CopyManager;
import de.raidcraft.dungeons.worldedit.CuboidCopy;
import de.raidcraft.dungeons.worldedit.CuboidCopyException;
import org.bukkit.World;
import org.bukkit.WorldCreator;

import java.io.IOException;

/**
 * @author Silthus
 */
public class DungeonWorldCreator extends WorldCreator {

    private final DungeonInstance instance;

    /**
     * Creates an empty WorldCreationOptions for the given world name
     *
     * @param name Name of the world that will be created
     */
    public DungeonWorldCreator(String name, DungeonInstance instance) {

        super(name);
        this.instance = instance;
    }

    @Override
    public World createWorld() {

        World world = super.createWorld();
        try {
            // lets copy our schematic
            CopyManager copyManager = RaidCraft.getComponent(DungeonsPlugin.class).getCopyManager();
            CuboidCopy copy = copyManager.load(world, instance.getDungeon());
            copy.paste();
        } catch (IOException | CuboidCopyException e) {
            RaidCraft.LOGGER.warning(e.getMessage());
        }
        return world;
    }
}
