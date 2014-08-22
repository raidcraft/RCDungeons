package de.raidcraft.dungeons;

import de.raidcraft.RaidCraft;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;

/**
 * @author Dragonfire
 */
public class WorldManager {

    public static void copyMapData(String template, String target) {

        try {
            DungeonsPlugin plugin = RaidCraft.getComponent(DungeonsPlugin.class);
            File src = new File(Bukkit.getWorldContainer() + File.separator + template);
            File dest = new File(Bukkit.getWorldContainer() + File.separator + target);
            if (dest.exists()) {
                RaidCraft.LOGGER.info("copyMapData: target world exists: " + target);
                return;
            }
            FileUtils.copyDirectory(src, dest);
            // delete data files
            FileUtils.forceDelete(new File(dest.getAbsoluteFile() + File.separator + "uid.dat"));
        } catch (IOException e) {
            RaidCraft.LOGGER.info("copyMapData: cannot copy (" + template + ") to: (" + target + ")");
            e.printStackTrace();
        }
    }

    public static void deleteWorld(final World target) {

        target.setKeepSpawnInMemory(false); // to unload the world completly
        Bukkit.unloadWorld(target, false);
        RaidCraft.LOGGER.warning("loaded chunks " + target.getLoadedChunks().length);
        // TODO: queue delete, on shutdown, next start, because directory is locked
        //        final File src = new File(Bukkit.getWorldContainer() + File.separator + target.getName());
        //        Bukkit.getScheduler().runTaskLaterAsynchronously(RaidCraft.getComponent(DungeonsPlugin.class), () -> {
        //            try {
        //                FileUtils.deleteDirectory(src);
        //            } catch (IOException e) {
        //                e.printStackTrace();
        //            }
        //        }, TimeUtil.secondsToTicks(3));
    }
}
