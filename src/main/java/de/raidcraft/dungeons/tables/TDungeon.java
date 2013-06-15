package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.List;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcdungeons_dungeons")
public class TDungeon {

    @Id
    private int id;
    @NotNull
    @Column(unique = true)
    private String name;
    @OneToMany
    @JoinColumn(name = "dungeon_id")
    private List<TDungeonInstance> instances;

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

    public List<TDungeonInstance> getInstances() {

        return instances;
    }

    public void setInstances(List<TDungeonInstance> instances) {

        this.instances = instances;
    }
}
