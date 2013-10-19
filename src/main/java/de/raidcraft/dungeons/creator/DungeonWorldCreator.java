package de.raidcraft.dungeons.creator;

import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Random;

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
        generator(new ChunkGenerator() {

            @Override
            public byte[] generate(World world, Random random, int x, int z) {

                int height = world.getMaxHeight();
                return new byte[256 * height];
            }
        });
    }
}
