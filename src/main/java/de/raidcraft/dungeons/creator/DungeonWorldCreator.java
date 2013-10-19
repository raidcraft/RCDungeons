package de.raidcraft.dungeons.creator;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.worldedit.CopyManager;
import de.raidcraft.dungeons.worldedit.CuboidCopy;
import de.raidcraft.dungeons.worldedit.CuboidCopyException;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
        generator(new ChunkGenerator() {
            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {

                return new ArrayList<>();
            }

            @Override
            public byte[] generate(World world, Random random, int x, int z) {

                int height = world.getMaxHeight();
                return new byte[256 * height];
            }
        });
        generateStructures(false);
        environment(World.Environment.NORMAL);
        type(WorldType.FLAT);
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
