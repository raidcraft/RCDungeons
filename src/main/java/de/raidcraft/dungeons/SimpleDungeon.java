package de.raidcraft.dungeons;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.api.AbstractDungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class SimpleDungeon extends AbstractDungeon {

    private final List<DungeonInstance> instances = new ArrayList<>();

    public SimpleDungeon(TDungeon dungeon) {

        super(dungeon.getId(), dungeon.getName());
        setFriendlyName(dungeon.getFriendlyName());
        setDescription(dungeon.getDescription());
        setResetTimeMillis(dungeon.getResetTimeMillis());
        setLocked(dungeon.isLocked());
        if (dungeon.getSpawns().size() > 0) {
            setSpawnLocation(dungeon.getSpawns().get(0).getLocation());
        }
        // lets load all instances that are registered with this dungeon
        List<TDungeonInstance> dungeonInstances = dungeon.getInstances();
        if (dungeonInstances != null) {
            for (TDungeonInstance instance : dungeonInstances) {
                // create persistant instances for now, may change later
                instances.add(new PersistantDungeonInstance(instance, this));
            }
        }
    }

    @Override
    public DungeonInstance createInstance(String... players) {

        DungeonInstance instance  = RaidCraft.getComponent(DungeonManager.class).createDungeonInstance(this, players);
        instances.add(instance);
        return instance;
    }

    @Override
    public List<DungeonInstance> getInstances() {

        return instances;
    }

    @Override
    public List<DungeonInstance> getActiveInstances() {

        List<DungeonInstance> instances = new ArrayList<>();
        for (DungeonInstance instance : this.instances) {
            if (instance.isActive()) {
                instances.add(instance);
            }
        }
        return instances;
    }

    @Override
    public DungeonInstance getActiveInstance(String player) {

        for (DungeonInstance instance : getActiveInstances()) {
            if (instance.containsPlayer(player)) {
                return instance;
            }
        }
        return null;
    }

    @Override
    public void save() {

        EbeanServer database = RaidCraft.getDatabase(DungeonsPlugin.class);
        TDungeon dungeon = database.find(TDungeon.class, getId());
        dungeon.setFriendlyName(getFriendlyName());
        dungeon.setDescription(getDescription());
        dungeon.setResetTimeMillis(getResetTimeMillis());
        dungeon.setLocked(isLocked());
        dungeon.getSpawns().add(new TDungeonSpawn(getSpawnLocation()));
        // add all instances
        for (DungeonInstance instance : getActiveInstances()) {
            instance.save();
            dungeon.getInstances().add(database.find(TDungeonInstance.class, instance.getId()));
        }
        database.save(dungeon);
    }
}
