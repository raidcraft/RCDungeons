package de.raidcraft.dungeons;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

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

    public DungeonInstance createDungeonInstance(Dungeon dungeon, UUID... players) {

        //        prepare an initial table entry to provide a valid id to the new dungeon instance
        TDungeonInstance tableEntry = new TDungeonInstance();
        tableEntry.setDungeon(plugin.getDatabase().find(TDungeon.class, dungeon.getId()));
        tableEntry.setCompleted(false);
        tableEntry.setLocked(false);
        tableEntry.setActive(false);
        tableEntry.setCreationTime(new Date());
        plugin.getDatabase().save(tableEntry);

        // now we have our id we can create the actual dungeon instance
        DungeonInstance instance = new PersistantDungeonInstance(tableEntry, dungeon);
        for (UUID playerId : players) {
            try {
                instance.addPlayer(plugin.getPlayerManager().getPlayer(playerId));
            } catch (UnknownPlayerException e) {
                plugin.getLogger().warning("ERROR adding player to dungeon instance: \"" + e.getMessage() + "\"");
            }
        }
        instance.save();
        //        load the world
        //        TODO: load world
        //        teleport the players
        //        TODO: teleport players
        return instance;
        //        return null;
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

    public void reload() {
        // TODO: implement
    }
}
