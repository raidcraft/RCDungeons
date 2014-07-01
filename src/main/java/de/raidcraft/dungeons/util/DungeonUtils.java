package de.raidcraft.dungeons.util;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import org.bukkit.World;

import java.io.File;

/**
 * @author Silthus
 */
public class DungeonUtils {

    public static boolean deleteWorld(World world) {

        return deleteWorld(world.getWorldFolder());
    }

    public static boolean deleteWorld(File path) {

        if (path.exists()) {
            for (File file : path.listFiles()) {
                if (file.isDirectory()) {
                    deleteWorld(file);
                } else {
                    file.delete();
                }
            }
        }
        return (path.delete());
    }

    public static String getTemplateWorldName(String name) {

        return RaidCraft.getComponent(DungeonsPlugin.class).getConfig().dungeonTemplatePrefix + name;
    }
}
