package de.raidcraft.dungeons;

import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

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
        load();
    }

    public void load() {

        plugin.getDatabase().find(TDungeonInstance.class)
                .where().eq("completed", 0).eq("locked", 0)
                .findList().stream().forEach(tInstance -> {
            try {
                DungeonInstance instance = new PersistantDungeonInstance(tInstance,
                        plugin.getDungeonManager().getDungeon(tInstance.getDungeon().getName()));
                addInstance(instance);
            } catch (DungeonException e) {
                e.printStackTrace();
            }
        });
    }

    private void addInstance(DungeonInstance instance) {

        instances.put(instance.getId(), instance);
    }

    public DungeonInstance getInstance(int instanceId) {

        return instances.get(instanceId);
    }

    public boolean instanceExists(int instanceId) {

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
            DungeonPlayer dungeonPlayer = plugin.getPlayerManager().getPlayer(playerId);
            instance.addPlayer(dungeonPlayer);
            dungeonPlayer.addDungeonInstance(instance);
            dungeonPlayer.save();
        }
        instance.save();
        addInstance(instance);
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

    public DungeonInstance getInstance(World world) {

        for (DungeonInstance instance : instances.values()) {
            if (world.getName().equalsIgnoreCase(instance.getWorld().getName())) {
                return instance;
            }
        }
        return null;
    }

    public void end(DungeonInstance instance, DungeonReason reason) {

        instance.getPlayers().forEach(p -> {
            Player bukkitPlayer = Bukkit.getPlayer(p.getPlayerId());
            // if on server and in instance
            if (bukkitPlayer != null && instance.getWorld().getName().equalsIgnoreCase(
                    bukkitPlayer.getWorld().getName())) {
                plugin.exit(bukkitPlayer);
            }
        });
        // close Instance
        deleteWorld(instance);
    }

    private void deleteWorld(DungeonInstance instance) {
        // keep data in database
        instance.setActive(false);
        instance.setCompleted(true);
        instance.setLocked(true);
        instance.save();
        // clear cache
        instances.remove(instance.getId());

        instance.getPlayers().stream().forEach(player -> player.removeDungeonInstance(instance));
        // delete world
        WorldManager.deleteWorld(instance.getWorld());
    }

    public void reload() {

        instances.clear();
        load();
    }
}
