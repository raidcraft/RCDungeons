package de.raidcraft.dungeons.creator;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Silthus
 */
public class DungeonWorldCreator extends WorldCreator {

    public DungeonWorldCreator(String name, Location spawn) {

        super(name);
        generator(new ChunkGenerator() {
            private Location newSpawn;

            @Override
            public Location getFixedSpawnLocation(World world, Random random) {

                if (newSpawn == null) {
                    newSpawn = new Location(world, spawn.getX(), spawn.getY(), spawn.getZ());
                }
                return newSpawn;
            }

            @Override
            public boolean canSpawn(World world, int x, int z) {

                return true;
            }

            @Override
            public List<BlockPopulator> getDefaultPopulators(World world) {

                return new ArrayList<>();
            }
        });
    }
}
