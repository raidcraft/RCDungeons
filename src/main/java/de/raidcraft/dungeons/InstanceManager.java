package de.raidcraft.dungeons;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.api.WorldNotLoadedExpcetion;
import de.raidcraft.dungeons.api.raidcraftevents.RE_InstanceLoadedEvent;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldLoadEvent;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class InstanceManager implements Listener {

    private DungeonsPlugin plugin;
    private Hashtable<Integer, DungeonInstance> instances = new Hashtable<>(); // thread safe

    public InstanceManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
        load();
        plugin.registerEvents(this);
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

    public DungeonInstance createDungeonInstance(Dungeon dungeon, UUID... players) {

        // prepare an initial table entry to provide a valid id to the new dungeon instance
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
        // create the world async (!)
        createInstanceWorld(instance, instance.getWorldName());
        return instance;
    }

    private void createInstanceWorld(DungeonInstance instance, String worldName) {

        try {
            // get Template Dungeon
            Dungeon dungeon = instance.getDungeon();
            // copy template world  map data async (!)
            plugin.getWorldManager().copyAsyncMapData(
                    DungeonUtils.getTemplateWorldName(dungeon.getName()),
                    worldName,
                    dungeon.getSpawnLocation());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public DungeonInstance getInstance(World world) {

        for (DungeonInstance instance : instances.values()) {
            if (world.getName().equalsIgnoreCase(instance.getWorldName())) {
                return instance;
            }
        }
        return null;
    }

    public void end(DungeonInstance instance, DungeonReason reason) {

        instance.getPlayers().forEach(p -> {
            Player bukkitPlayer = Bukkit.getPlayer(p.getPlayerId());
            // if on server and in instance
            if (bukkitPlayer != null && instance.getWorldName().equalsIgnoreCase(
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
        try {
            plugin.getWorldManager().deleteWorld(instance.getWorld());
        } catch (WorldNotLoadedExpcetion e) {
           e.printStackTrace();
        }
    }

    public void reload() {

        instances.clear();
        load();
    }

    public DungeonInstance findInstance(String worldName) {

        Collection<DungeonInstance> currentInstances = instances.values();
        for (DungeonInstance instance : currentInstances) {
            if (instance.getWorldName().equals(worldName)) {
                return instance;
            }
        }
        return null;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void worldLoad(WorldLoadEvent event) {

        if (!event.getWorld().getName().startsWith(plugin.getConfig().dungeonInstancePrefix)) {
            return;
        }
        World world = event.getWorld();
        plugin.getLogger().info("instance loading detected: (" + world.getName() + ")");
        // find instance
        DungeonInstance instance = findInstance(world.getName());
        if (instance == null) {
            plugin.getLogger().warning("instance not loaded for world: (" + world.getName() + ")");
        }
        RaidCraft.callEvent(new RE_InstanceLoadedEvent(world, instance));
    }
}
