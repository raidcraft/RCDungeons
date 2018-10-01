package de.raidcraft.dungeons.tables;

import io.ebean.annotation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
@Getter
@Setter
@Entity
@Table(name = "rc_dungeons_dungeons")
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
