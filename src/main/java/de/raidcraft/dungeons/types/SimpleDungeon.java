package de.raidcraft.dungeons.types;

import com.avaje.ebean.EbeanServer;
import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.AbstractDungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Getter
public class SimpleDungeon extends AbstractDungeon {

    private final List<DungeonInstance> instances = new ArrayList<>();
    private String templateWorldName;
    @Setter
    private World templateWorld;


    public SimpleDungeon(TDungeon dungeon, String templateWorld) {

        super(dungeon.getId(), dungeon.getName());
        this.templateWorldName = templateWorld;
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
            for (TDungeonInstance instance : dungeonInstances) {
                instances.add(new PersistantDungeonInstance(instance, this));
            }
        }
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
