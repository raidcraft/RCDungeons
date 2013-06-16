package de.raidcraft.dungeons.types;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.AbstractDungeonInstance;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.sql.Timestamp;

/**
 * @author Silthus
 */
public class PersistantDungeonInstance extends AbstractDungeonInstance {

    private final String world;

    public PersistantDungeonInstance(TDungeonInstance instance, Dungeon dungeon, String world) {

        super(instance.getId(), dungeon);
        this.creationTime = instance.getCreationTime();
        this.world = world;
    }

    @Override
    public World getWorld() {

        return Bukkit.getWorld(world);
    }

    @Override
    public World loadWorld() {

        return Bukkit.getServer().createWorld(new DungeonWorldCreator(this.world));
    }

    @Override
    public boolean unloadWorld(boolean force) {

        if (!force && isActive()) {
            return false;
        }
        if (force && isActive()) {
            Player player;
            for (String playerName : getPlayers()) {
                player = Bukkit.getPlayer(playerName);
                if (player == null) {
                    continue;
                }
                // TODO: use multiworld plugin to transfer player to his last position
                player.kickPlayer("Dungeon wurde geschlossen.");
            }
        }
        return Bukkit.unloadWorld(getWorld(), true);
    }

    @Override
    public void save() {

        // save the world first
        getWorld().save();
        // now save stuff to the database
        EbeanServer database = RaidCraft.getDatabase(DungeonsPlugin.class);
        TDungeonInstance instance = database.find(TDungeonInstance.class, getId());
        instance.setActive(isActive());
        instance.setCompleted(isCompleted());
        TDungeonPlayer tPlayer;
        for (String player : getPlayers()) {
            tPlayer = database.find(TDungeonPlayer.class).where().eq("player", player).eq("dungeon_instance", getId()).findUnique();
            if (tPlayer == null) {
                tPlayer = new TDungeonPlayer();
                tPlayer.setPlayer(player);
                tPlayer.setDungeonInstance(instance);
                tPlayer.setJoinTime(new Timestamp(System.currentTimeMillis()));
                database.save(tPlayer);
            }
            tPlayer.setLastJoin(new Timestamp(System.currentTimeMillis()));
            database.update(tPlayer);
            instance.getPlayers().add(tPlayer);
        }
        database.update(instance);
    }
}
