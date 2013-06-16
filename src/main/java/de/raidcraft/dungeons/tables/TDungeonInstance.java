package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.sql.Timestamp;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcdungeons_dungeon_instances")
public class TDungeonInstance {

    @Id
    private int id;
    @ManyToOne
    private TDungeon dungeon;
    @NotNull
    @Column(unique = true)
    private String world;
    @NotNull
    private Timestamp creationTime;
    private boolean active;
    private boolean completed;
    @OneToMany
    @JoinColumn(name = "dungeon_instance_id")
    private List<TDungeonPlayer> players;

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

    public List<TDungeonPlayer> getPlayers() {

        return players;
    }

    public void setPlayers(List<TDungeonPlayer> players) {

        this.players = players;
    }
}
