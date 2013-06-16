package de.raidcraft.dungeons.api;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Silthus
 */
public abstract class AbstractDungeonInstance implements DungeonInstance {

    private final int id;
    private final Dungeon dungeon;
    private final Set<String> players = new HashSet<>();
    private boolean active;
    private boolean completed;
    protected Timestamp creationTime;

    public AbstractDungeonInstance(int id, Dungeon dungeon) {

        this.id = id;
        this.dungeon = dungeon;
    }

    @Override
    public int getId() {

        return id;
    }

    @Override
    public Dungeon getDungeon() {

        return dungeon;
    }

    @Override
    public Timestamp getCreationTime() {

        return creationTime;
    }

    @Override
    public void addPlayer(String player) {

        this.players.add(player);
    }

    @Override
    public boolean removePlayer(String player) {

        return this.players.remove(player);
    }

    @Override
    public List<String> getPlayers() {

        return new ArrayList<>(players);
    }

    @Override
    public boolean isActive() {

        return active;
    }

    @Override
    public void setActive(boolean active) {

        this.active = active;
    }

    @Override
    public boolean isCompleted() {

        return completed;
    }

    @Override
    public void setCompleted(boolean completed) {

        this.completed = completed;
    }
}
