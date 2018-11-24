package de.raidcraft.dungeons.creator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.block.data.BlockData;
import org.bukkit.craftbukkit.v1_13_R2.generator.CraftChunkData;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.material.MaterialData;

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
                    return super.getFixedSpawnLocation(world, random);
                }
                return new Location(world, newSpawn.getX(), newSpawn.getX(), newSpawn.getZ());
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
