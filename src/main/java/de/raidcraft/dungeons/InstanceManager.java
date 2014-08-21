package de.raidcraft.dungeons;

import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Hashtable;

/**
 * @author Dragonfire
 */
public class InstanceManager {

    private DungeonsPlugin plugin;
    private Hashtable<String, World> instances = new Hashtable<>(); // thread safe

    public InstanceManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    public World getInstance(String instanceId) {
        return instances.get(instanceId);
    }

    public boolean instanceExists(String instanceId) {

        try {
            plugin.getCreateWorldLock().acquire();
            return instances.containsKey(instanceId);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            plugin.getCreateWorldLock().release();
        }
        return false;
    }

    public void createInstance(String templateWorld, String instanceId) {

        try {
            plugin.getCreateWorldLock().acquire();
            if (instances.containsKey(instanceId)) {
                plugin.getLogger().warning("double call createInstance on same instance ("
                        + instanceId + ", " + templateWorld + ")");
                return; // finally will be called
            }
            // hotfix for instace
            String instanceName = "instance_" + instanceId;

            // get Template Dungeon
            Dungeon dungeon = plugin.getDungeonManager().getDungeon(templateWorld);
            Location spawn = dungeon.getSpawnLocation();
            // create new chunk generator
            DungeonWorldCreator creator = new DungeonWorldCreator(instanceName, spawn);
            // copy template world
            WorldManager.copyMapData(DungeonUtils.getTemplateWorldName(dungeon.getName()), instanceName);
            // load map
            World world = Bukkit.createWorld(creator);
            instances.put(instanceId, world);
        } catch (Exception | DungeonException e) {
            e.printStackTrace();
        } finally {
            plugin.getCreateWorldLock().release();
        }
    }
}
