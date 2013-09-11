package de.raidcraft.dungeons.api;

import org.bukkit.Location;

/**
 * @author Silthus
 */
public abstract class AbstractDungeon implements Dungeon {

    private final int id;
    private final String name;
    private String friendlyName;
    private String description;
    private long resetTime;
    private Location spawnLocation;
    private boolean locked;

    public AbstractDungeon(int id, String name) {

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
    public String getFriendlyName() {

        return friendlyName;
    }

    @Override
    public void setFriendlyName(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    @Override
    public String getDescription() {

        return description;
    }

    @Override
    public void setDescription(String description) {

        this.description = description;
    }

    @Override
    public long getResetTimeMillis() {

        return resetTime;
    }

    @Override
    public void setResetTimeMillis(long resetTime) {

        this.resetTime = resetTime;
    }

    @Override
    public Location getSpawnLocation() {

        return spawnLocation;
    }

    @Override
    public void setSpawnLocation(Location location) {

        this.spawnLocation = location;
    }

    @Override
    public boolean isLocked() {

        return locked;
    }

    @Override
    public void setLocked(boolean locked) {

        this.locked = locked;
    }
}
