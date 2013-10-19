package de.raidcraft.dungeons;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.api.AbstractDungeonPlayer;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Silthus
 */
public class BukkitDungeonPlayer extends AbstractDungeonPlayer {

    private final Map<Dungeon, DungeonInstance> instances = new HashMap<>();
    private DungeonInstance activeInstance;

    public BukkitDungeonPlayer(TDungeonPlayer player) {

        super(player.getId(), player.getPlayer());
        setLastPosition(player.getLastPosition());
        DungeonManager dungeonManager = RaidCraft.getComponent(DungeonManager.class);
        for (TDungeonInstancePlayer instance : player.getInstances()) {
            try {
                PersistantDungeonInstance dungeonInstance =
                        new PersistantDungeonInstance(instance.getInstance(), dungeonManager.getDungeon(instance.getInstance().getDungeon().getName()));
                instances.put(dungeonInstance.getDungeon(), dungeonInstance);
                if (dungeonInstance.isActive()) {
                    activeInstance = dungeonInstance;
                }
            } catch (DungeonException e) {
                RaidCraft.LOGGER.warning(e.getMessage());
            }
        }
    }

    @Override
    public List<DungeonInstance> getDungeonInstances() {

        return new ArrayList<>(this.instances.values());
    }

    @Override
    public DungeonInstance getDungeonInstance(Dungeon dungeon) {

        return this.instances.get(dungeon);
    }

    @Override
    public DungeonInstance getActiveInstance() {

        return activeInstance;
    }

    @Override
    public void leaveActiveDungeon(DungeonReason reason) {

        if (getActiveInstance() != null) {
            Player player = Bukkit.getPlayer(getName());
            if (player != null) {
                player.teleport(getLastPosition());
            }
            getActiveInstance().removePlayer(this);
            activeInstance = null;
        }
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(DungeonsPlugin.class);
        TDungeonPlayer player = database.find(TDungeonPlayer.class, getId());
        Location position = getLastPosition();
        player.setLastWorld(position.getWorld().getName());
        player.setLastX(position.getX());
        player.setLastY(position.getY());
        player.setLastZ(position.getZ());
        player.setLastYaw((long) position.getYaw());
        player.setLastPitch((long) position.getPitch());
        for (DungeonInstance instance : getDungeonInstances()) {
            instance.save();
        }
    }
}
