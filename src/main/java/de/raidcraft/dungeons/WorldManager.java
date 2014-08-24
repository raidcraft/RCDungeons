package de.raidcraft.dungeons;

import de.raidcraft.RaidCraft;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dungeons.api.raidcraftevents.RE_InstanceCreatedEvent;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import net.minecraft.util.org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Dragonfire
 */
public class WorldManager {

    private DungeonsPlugin plugin;
    private CompletedWorlds completedWorlds;
    // if creation process start a lock is created
    private Hashtable<String, AtomicBoolean> worldLocks = new Hashtable<>();

    public WorldManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
        this.completedWorlds = new CompletedWorlds(plugin);
        completedWorlds.load();
        // delete world from disk
        completedWorlds.completedWorlds.forEach(this::deletWorldData);
        completedWorlds.completedWorlds.clear();
    }

    public synchronized boolean copyProcessActive(String target) {

        return worldLocks.containsKey(target);
    }

    public World loadWorld(Location spawn, String name) {
        // create new chunk generator
        DungeonWorldCreator creator = new DungeonWorldCreator(name, spawn);
        // load map
        return Bukkit.createWorld(creator);
    }

    /**
     * @param spawn null: not loading world; otherwise loads the world
     */
    public synchronized void copyAsyncMapData(String template, String target, Location spawn) {

        if (worldLocks.containsKey(target)) {
            plugin.getLogger().warning("copyAsyncMapData triggered twice template:(" +
                    template + ") target:(" + target + ")");
            return;
        }
        // lock
        worldLocks.put(target, new AtomicBoolean(true));
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                copyMapData(template, target);
                RaidCraft.callEvent(new RE_InstanceCreatedEvent(target));
                if (spawn != null) {
                    Bukkit.getScheduler().runTask(plugin, () -> loadWorld(spawn, target));
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // unlock
                worldLocks.remove(target);
            }
        });
    }

    private void copyMapData(String template, String target) {

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

    public void reload() {
        // TODO: wait on all creations ?
    }

    public void deleteWorld(final World target) {

        target.setKeepSpawnInMemory(false); // to unload the world completly
        Bukkit.unloadWorld(target, false);
        RaidCraft.LOGGER.warning("loaded chunks " + target.getLoadedChunks().length);
        completedWorlds.completedWorlds.add(target.getName());
        completedWorlds.save();
    }

    private void deletWorldData(String target) {

        final File src = new File(Bukkit.getWorldContainer() + File.separator + target);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                plugin.getLogger().info("Delete instance: (" + target + ")");
                FileUtils.deleteDirectory(src);
                plugin.getLogger().info("Instance Deletion completed: (" + target + ")");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public static class CompletedWorlds extends ConfigurationBase<DungeonsPlugin> {


        public CompletedWorlds(DungeonsPlugin plugin) {

            super(plugin, "comletedWorlds.yml");
        }

        @Setting("completed-worlds")
        public List<String> completedWorlds = new ArrayList<>();
    }

    public static boolean isLoaded(String worldName) {
        return Bukkit.getWorld(worldName) != null;
    }
}
