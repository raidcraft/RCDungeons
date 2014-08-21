package de.raidcraft.dungeons.api;

import de.raidcraft.RaidCraft;
import de.raidcraft.dungeons.DungeonsPlugin;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Getter
@Setter
public abstract class AbstractDungeon implements Dungeon {

    private final int id;
    private final String name;
    private String friendlyName;
    private String description;
    private long resetTimeMillis;
    private Location spawnLocation;
    private boolean locked;

    public AbstractDungeon(int id, String name) {

        this.id = id;
        this.name = name;
    }

    @Override
    public DungeonInstance createInstance(UUID... players) {

        return RaidCraft.getComponent(DungeonsPlugin.class).getInstanceManager()
                .createDungeonInstance(this, players);
    }

    @Override
    public List<DungeonInstance> getActiveInstances() {

        return getInstances().stream()
                .filter(DungeonInstance::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public DungeonInstance getActiveInstance(UUID player) {

        for (DungeonInstance instance : getActiveInstances()) {
            if (instance.containsPlayer(player)) {
                return instance;
            }
        }
        return null;
    }
}
