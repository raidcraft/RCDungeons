package de.raidcraft.dungeons.api;

import org.bukkit.Location;

/**
 * @author Silthus
 */
public abstract class AbstractDungeonPlayer implements DungeonPlayer {

    private final int id;
    private final String name;
    private Location lastPosition;

    public AbstractDungeonPlayer(int id, String name) {

        this.id = id;
        this.name = name;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public String getName() {

        return name;
    }

    @Override
    public Location getLastPosition() {

        return lastPosition;
    }

    @Override
    public void setLastPosition(Location lastPosition) {

        this.lastPosition = lastPosition;
    }
}
