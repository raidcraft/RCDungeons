package de.raidcraft.dungeons.types;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.AbstractDungeonInstance;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.creator.DungeonWorldCreator;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;

/**
 * @author Silthus
 */
public class PersistantDungeonInstance extends AbstractDungeonInstance {

    private final String world;

    public PersistantDungeonInstance(TDungeonInstance instance, Dungeon dungeon) {

        super(instance.getId(), dungeon);
        this.creationTime = instance.getCreationTime();
        this.world = RaidCraft.getComponent(DungeonsPlugin.class).getConfig().dungeonInstancePrefix + dungeon.getName() + "_" + instance.getId();
        setLocked(instance.isLocked());
        setCompleted(instance.isCompleted());
        setActive(instance.isActive());
    }

    @Override
    public World getWorld() {

        World world = Bukkit.getWorld(this.world);
        if (world == null) {
            world = loadWorld();
        }
        return world;
    }

    @Override
    public World loadWorld() {

        return Bukkit.getServer().createWorld(new DungeonWorldCreator(this.world).copy(getDungeon().getTemplateWorld()));
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
        return Bukkit.unloadWorld(getWorld(), true);
    }

    @Override
    public boolean delete(boolean force) {

        return unload(force) && DungeonUtils.deleteWorld(getWorld());
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
        instance.setLocked(isLocked());
        database.save(instance);
        for (DungeonPlayer player : getPlayers()) {
            player.save();
            TDungeonInstancePlayer tDungeonPlayer = database.find(TDungeonInstancePlayer.class)
                    .where().eq("instance_id", getId()).eq("player_id", player.getId()).findUnique();
            if (tDungeonPlayer == null) {
                tDungeonPlayer = new TDungeonInstancePlayer();
                tDungeonPlayer.setInstance(instance);
                tDungeonPlayer.setPlayer(database.find(TDungeonPlayer.class, player.getId()));
                database.save(tDungeonPlayer);
            }
        }
    }
}
