package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
@Entity
@Table(name = "dungeons_dungeon_instances")
public class TDungeonInstance {

    @Id
    private int id;
    @ManyToOne
    private TDungeon dungeon;
    @NotNull
    private Timestamp creationTime;
    private boolean active;
    private boolean completed;
    private boolean locked;
    @OneToMany
    @JoinColumn(name = "dungeon_instance_id")
    private Set<TDungeonInstancePlayer> players = new HashSet<>();

    public boolean isLocked() {

        return locked;
    }

    public void setLocked(boolean locked) {

        this.locked = locked;
    }

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TDungeon getDungeon() {

        return dungeon;
    }

    public void setDungeon(TDungeon dungeon) {

        this.dungeon = dungeon;
    }

    public Timestamp getCreationTime() {

        return creationTime;
    }

    public void setCreationTime(Timestamp creationTime) {

        this.creationTime = creationTime;
    }

    public boolean isActive() {

        return active;
    }

    public void setActive(boolean active) {

        this.active = active;
    }

    public boolean isCompleted() {

        return completed;
    }

    public void setCompleted(boolean completed) {

        this.completed = completed;
    }

    public Set<TDungeonInstancePlayer> getPlayers() {

        return players;
    }

    public void setPlayers(Set<TDungeonInstancePlayer> players) {

        this.players = players;
    }
}
