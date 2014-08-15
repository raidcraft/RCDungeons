package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

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
@Getter
@Setter
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
}
