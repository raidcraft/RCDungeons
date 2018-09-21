package de.raidcraft.dungeons.types;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.*;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.util.DungeonUtils;
import io.ebean.EbeanServer;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Silthus
 */
@Getter
public class PersistantDungeonInstance extends AbstractDungeonInstance {

    private final String worldName;

    public PersistantDungeonInstance(TDungeonInstance instance, Dungeon dungeon) {

        super(instance.getId(), dungeon);
        this.creationTime = instance.getCreationTime();
        this.worldName = RaidCraft.getComponent(DungeonsPlugin.class).getConfig().dungeonInstancePrefix + dungeon.getName() + "_" + instance.getId();
        setLocked(instance.isLocked());
        setCompleted(instance.isCompleted());
        setActive(instance.isActive());
        load(instance);
    }

    private void load(TDungeonInstance tInstance) {

        DungeonsPlugin plugin = RaidCraft.getComponent(DungeonsPlugin.class);
        // lazy load player, otherwise player load dungeons that don't exists atm
        //        plugin.getDatabase().find(TDungeonInstancePlayer.class)
        //                .where().eq("instance_id", tInstance.getId()).findList()
        //                .stream().forEach(player -> {
        //                    TDungeonPlayer tDungeonPlayer = player.getPlayer();
        //                    UUID uuid = tDungeonPlayer.getPlayerId();
        //                    addPlayer(plugin.getPlayerManager().getPlayer(uuid));
        //                }
        //        );
    }

    @Override
    public World getWorld() throws WorldNotLoadedExpcetion {

        World world = Bukkit.getWorld(this.worldName);
        if (world == null) {
            throw new WorldNotLoadedExpcetion(this.worldName);
        }
        return world;
    }

    @Override
    public boolean unload(boolean force) {

        if (!force && isActive()) {
            return false;
        }
        if (force && isActive()) {
            for (DungeonPlayer player : getPlayers()) {
                player.leaveActiveDungeon(DungeonReason.UNLOAD);
            }
        }
        try {
            return Bukkit.unloadWorld(getWorld(), true);
        } catch (WorldNotLoadedExpcetion e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean delete(boolean force) {

        try {
            return unload(force) && DungeonUtils.deleteWorld(getWorld());
        } catch (WorldNotLoadedExpcetion e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void save() {

        // save the world first, if loaded
        try {
            getWorld().save();
        } catch (WorldNotLoadedExpcetion e) {
            // nothing, happend if in world creation process
        }
        // now save stuff to the database
        EbeanServer database = RaidCraft.getDatabase(DungeonsPlugin.class);
        TDungeonInstance instance = database.find(TDungeonInstance.class, getId());
        instance.setActive(isActive());
        instance.setCompleted(isCompleted());
        instance.setLocked(isLocked());
        database.save(instance);
        for (DungeonPlayer player : getPlayers()) {
            TDungeonInstancePlayer tDungeonPlayer = database.find(TDungeonInstancePlayer.class)
                    .where().eq("instance_id", getId()).eq("player_id", player.getId()).findOne();
            if (tDungeonPlayer == null) {
                tDungeonPlayer = new TDungeonInstancePlayer();
                tDungeonPlayer.setInstance(instance);
                tDungeonPlayer.setPlayer(database.find(TDungeonPlayer.class, player.getId()));
                database.save(tDungeonPlayer);
            }
        }
    }
}
