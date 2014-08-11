package de.raidcraft.dungeons;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.dungeons.util.DungeonUtils;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.TimeUtil;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class DungeonManager implements Component {

    private final DungeonsPlugin plugin;
    private final Map<String, Dungeon> dungeons = new CaseInsensitiveMap<>();
    private final Map<String, DungeonPlayer> players = new CaseInsensitiveMap<>();
    private WorldEditPlugin worldEdit;

    protected DungeonManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(DungeonManager.class, this);
        Plugin wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (wePlugin != null) {
            this.worldEdit = (WorldEditPlugin) wePlugin;
            load();
        } else {
            plugin.getLogger().warning("Unable to hook worldedit!");
            plugin.disable();
        }
    }

    public void reload() {

        dungeons.clear();
        players.clear();
        load();
    }

    private void load() {

        for (TDungeon dungeon : plugin.getDatabase().find(TDungeon.class).findList()) {
            SimpleDungeon simpleDungeon = new SimpleDungeon(dungeon, Bukkit.getWorld(DungeonUtils.getTemplateWorldName(dungeon.getName())));
            this.dungeons.put(simpleDungeon.getName(), simpleDungeon);
            plugin.getLogger().info("Loaded dungeon template for: " + simpleDungeon.getName() + " - " + simpleDungeon.getFriendlyName());
        }
    }

    public Dungeon getDungeon(String name) throws DungeonException {

        if (dungeons.containsKey(name)) {
            return dungeons.get(name);
        }
        List<Dungeon> foundDungeons = dungeons.values().stream()
                .filter(dungeon -> dungeon.getName().startsWith(name)
                        || dungeon.getFriendlyName().toLowerCase().startsWith(name.toLowerCase()))
                .collect(Collectors.toList());
        if (foundDungeons.isEmpty()) {
            throw new DungeonException("Did not find a dungeon with the name: " + name);
        }
        if (foundDungeons.size() > 1) {
            throw new DungeonException("Found multiple dungeons with the name " + name + ":" + StringUtils.join(foundDungeons, ", "));
        }
        return foundDungeons.get(0);
    }

    public Dungeon createDungeon(Player creator, String name, String friendlyName) throws RaidCraftException {

        if (dungeons.containsKey(name)) {
            throw new RaidCraftException("Duplicate dungeon " + name + "! Aborted dungeon creation...");
        }
        // first lets create our dungeon object
        TDungeon tDungeon = new TDungeon();
        tDungeon.setName(name);
        tDungeon.setLocked(true);
        tDungeon.setFriendlyName(friendlyName);
        tDungeon.setResetTimeMillis(TimeUtil.secondsToMillis(plugin.getConfig().default_reset_time));
        plugin.getDatabase().save(tDungeon);

        Location origin = creator.getLocation();
        TDungeonSpawn spawn = new TDungeonSpawn(origin);
        spawn.setDungeon(tDungeon);
        plugin.getDatabase().save(spawn);

        SimpleDungeon dungeon = new SimpleDungeon(tDungeon, createDungeonWorld(creator, DungeonUtils.getTemplateWorldName(tDungeon.getName())));
        this.dungeons.put(dungeon.getName(), dungeon);
        return dungeon;
    }

    public World createDungeonWorld(Player creator, String worldName) throws RaidCraftException {
        // create empty world
        World world = Bukkit.createWorld(new DungeonWorldCreator(worldName, creator.getLocation()));

        // switch creator to flying mode that he don't die
        creator.sendMessage("Create dungeon ... don't move !!!");
        creator.setGameMode(GameMode.CREATIVE);
        creator.setFlying(true);

        Location loc = creator.getLocation();
        loc.setWorld(world);
        creator.performCommand("/copy");
        creator.teleport(loc);
        creator.performCommand("/paste");
        return world;
    }

    public DungeonInstance createDungeonInstance(Dungeon dungeon, String... players) {

        // prepare an initial table entry to provide a valid id to the new dungeon instance
        TDungeonInstance tableEntry = new TDungeonInstance();
        tableEntry.setDungeon(plugin.getDatabase().find(TDungeon.class, dungeon.getId()));
        tableEntry.setCompleted(false);
        tableEntry.setLocked(false);
        tableEntry.setActive(false);
        tableEntry.setCreationTime(new Timestamp(System.currentTimeMillis()));
        plugin.getDatabase().save(tableEntry);
        // now we have our id we can create the actual dungeon instance
        DungeonInstance instance = new PersistantDungeonInstance(tableEntry, dungeon);
        for (String player : players) {
            try {
                instance.addPlayer(getPlayer(player));
            } catch (UnknownPlayerException e) {
                plugin.getLogger().warning("ERROR adding player to dungeon instance: \"" + e.getMessage() + "\"");
            }
        }
        instance.save();
        return instance;
    }

    public DungeonPlayer getPlayer(String player) throws UnknownPlayerException {

        Player bukkitPlayer = Bukkit.getPlayer(player);
        if (bukkitPlayer != null) {
            player = bukkitPlayer.getName();
        }

        if (!players.containsKey(player)) {
            TDungeonPlayer tDungeonPlayer = plugin.getDatabase().find(TDungeonPlayer.class).where().eq("player", player).findUnique();
            if (bukkitPlayer == null && tDungeonPlayer == null) {
                throw new UnknownPlayerException("The player " + player + " is not online and does not exist in the database!");
            } else if (bukkitPlayer != null && tDungeonPlayer == null) {
                tDungeonPlayer = new TDungeonPlayer();
                tDungeonPlayer.setPlayer(player);
                Location location = bukkitPlayer.getLocation();
                tDungeonPlayer.setLastWorld(location.getWorld().getName());
                tDungeonPlayer.setLastX(location.getX());
                tDungeonPlayer.setLastY(location.getY());
                tDungeonPlayer.setLastZ(location.getZ());
                tDungeonPlayer.setLastPitch(location.getPitch());
                tDungeonPlayer.setLastYaw(location.getYaw());
                plugin.getDatabase().save(tDungeonPlayer);
            }
            BukkitDungeonPlayer dungeonPlayer = new BukkitDungeonPlayer(tDungeonPlayer);
            players.put(dungeonPlayer.getName(), dungeonPlayer);
        }
        return players.get(player);
    }

    public DungeonPlayer getPlayer(Player player) {

        try {
            return getPlayer(player.getName());
        } catch (UnknownPlayerException ignored) {
            // will never occur
        }
        return null;
    }
}
