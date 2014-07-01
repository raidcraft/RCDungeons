package de.raidcraft.dungeons;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.api.AbstractDungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import de.raidcraft.dungeons.types.PersistantDungeonInstance;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
public class SimpleDungeon extends AbstractDungeon {

    private final List<DungeonInstance> instances = new ArrayList<>();
    private final World templateWorld;

    public SimpleDungeon(TDungeon dungeon, World templateWorld) {

        super(dungeon.getId(), dungeon.getName());
        this.templateWorld = templateWorld;
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
            // create persistant instances for now, may change later
            instances.addAll(dungeonInstances.stream()
                    .map(instance -> new PersistantDungeonInstance(instance, this))
                    .collect(Collectors.toList()));
        }
    }

    @Override
    public World getTemplateWorld() {

        return templateWorld;
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

        return this.instances.stream()
                .filter(DungeonInstance::isActive)
                .collect(Collectors.toList());
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
