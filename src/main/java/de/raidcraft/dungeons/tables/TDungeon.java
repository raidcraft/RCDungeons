package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "dungeons_dungeons")
public class TDungeon {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String name;
    private String friendlyName;
    private String description;
    private long resetTimeMillis;
    private boolean locked;
    @OneToMany
    @JoinColumn(name = "dungeon_id")
    private List<TDungeonSpawn> spawns = new ArrayList<>();
    @OneToMany
    @JoinColumn(name = "dungeon_id")
    private List<TDungeonInstance> instances = new ArrayList<>();

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getName() {

        return name;
    }

    public void setName(String name) {

        this.name = name;
    }

    public String getFriendlyName() {

        return friendlyName;
    }

    public void setFriendlyName(String friendlyName) {

        this.friendlyName = friendlyName;
    }

    public String getDescription() {

        return description;
    }

    public void setDescription(String description) {

        this.description = description;
    }

    public long getResetTimeMillis() {

        return resetTimeMillis;
    }

    public void setResetTimeMillis(long resetTimeMillis) {

        this.resetTimeMillis = resetTimeMillis;
    }

    public boolean isLocked() {

        return locked;
    }

    public void setLocked(boolean locked) {

        this.locked = locked;
    }

    public List<TDungeonSpawn> getSpawns() {

        return spawns;
    }

    public void setSpawns(List<TDungeonSpawn> spawns) {

        this.spawns = spawns;
    }

    public List<TDungeonInstance> getInstances() {

        return instances;
    }

    public void setInstances(List<TDungeonInstance> instances) {

        this.instances = instances;
    }
}
