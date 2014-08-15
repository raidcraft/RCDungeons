package de.raidcraft.dungeons.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

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
}
