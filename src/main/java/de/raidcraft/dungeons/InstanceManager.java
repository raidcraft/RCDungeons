package de.raidcraft.dungeons;

import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
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
    private Hashtable<Integer, DungeonInstance> instances = new Hashtable<>(); // thread safe

    public InstanceManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    public DungeonInstance getInstance(String instanceId) {

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
        PersistantDungeonInstance instance = new PersistantDungeonInstance(tableEntry, dungeon);
        for (UUID playerId : players) {
            try {
                DungeonPlayer dungeonPlayer = plugin.getPlayerManager().getPlayer(playerId);
                instance.addPlayer(dungeonPlayer);
                dungeonPlayer.save();
            } catch (UnknownPlayerException e) {
                plugin.getLogger().warning("ERROR adding player to dungeon instance: \"" + e.getMessage() + "\"");
            }
        }
        instance.save();
        // load the world
        createInstanceWorld(instance, instance.getWorldName());
        // TODO: teleport the players ?
        // Arrays.stream(players).forEach();
        return instance;
    }

    private World createInstanceWorld(DungeonInstance instance, String worldName) {

        try {
            plugin.getCreateWorldLock().acquire();
            // get Template Dungeon
            Dungeon dungeon = instance.getDungeon();
            Location spawn = dungeon.getSpawnLocation();
            // create new chunk generator
            DungeonWorldCreator creator = new DungeonWorldCreator(worldName, spawn);
            // copy template world
            WorldManager.copyMapData(dungeon.getTemplateWorldName(), worldName);
            // load map
            return Bukkit.createWorld(creator);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            plugin.getCreateWorldLock().release();
        }
        return null;
    }

    public void reload() {
        // TODO: implement
    }
}
