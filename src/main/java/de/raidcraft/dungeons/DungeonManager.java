package de.raidcraft.dungeons;

import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldedit.data.DataException;
import de.raidcraft.api.Component;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.dungeons.worldedit.MCEditCuboidCopy;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Map;

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
        Plugin wePlugin = Bukkit.getPluginManager().getPlugin("WorldEdit");
        if (wePlugin != null) {
            this.worldEdit = (WorldEditPlugin) wePlugin;
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
            SimpleDungeon simpleDungeon = new SimpleDungeon(dungeon);
            this.dungeons.put(simpleDungeon.getName(), simpleDungeon);
            plugin.getLogger().info("Loaded dungeon template for: " + simpleDungeon.getName() + " - " + simpleDungeon.getFriendlyName());
        }
    }

    public Dungeon getDungeon(String name) {

        return dungeons.get(name);
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

        TDungeonSpawn spawn = new TDungeonSpawn(creator.getLocation());
        plugin.getDatabase().save(spawn);
        ArrayList<TDungeonSpawn> spawns = new ArrayList<>();
        spawns.add(spawn);
        tDungeon.setSpawns(spawns);

        plugin.getDatabase().save(tDungeon);

        SimpleDungeon dungeon = new SimpleDungeon(tDungeon);

        try {
            // then copy the player selection and save it as the dungeon template
            Selection selection = worldEdit.getSelection(creator);
            Vector min = BukkitUtil.toVector(selection.getMinimumPoint());
            Vector max = BukkitUtil.toVector(selection.getMaximumPoint());
            Vector size = max.subtract(min).add(1, 1, 1);
            MCEditCuboidCopy copy = new MCEditCuboidCopy(min, size, creator.getWorld());
            copy.copy();
            plugin.getCopyManager().save(creator.getWorld(), name, copy);
        } catch (IOException | DataException e) {
            creator.sendMessage(ChatColor.RED + "Failed to copy dungeon selection! See console...");
            plugin.getLogger().warning(e.getMessage());
        }
        return dungeon;
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
                tDungeonPlayer.setName(player);
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
