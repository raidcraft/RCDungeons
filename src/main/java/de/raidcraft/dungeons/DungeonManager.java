package de.raidcraft.dungeons;

import de.raidcraft.api.Component;
import de.raidcraft.api.player.UnknownPlayerException;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.util.CaseInsensitiveMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.Timestamp;
import java.util.Map;

/**
 * @author Silthus
 */
public class DungeonManager implements Component {

    private final DungeonsPlugin plugin;
    private final Map<String, Dungeon> dungeons = new CaseInsensitiveMap<>();
    private final Map<String, DungeonPlayer> players = new CaseInsensitiveMap<>();

    protected DungeonManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
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
