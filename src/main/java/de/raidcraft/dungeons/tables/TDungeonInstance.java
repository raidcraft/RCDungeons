package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Silthus
 */
@Getter
@Setter
@Entity
@Table(name = "dungeons_dungeon_instances")
public class TDungeonInstance {

    @Id
    private int id;
    @ManyToOne
    private TDungeon dungeon;
    @NotNull
    private Date creationTime;
    private boolean active;
    private boolean completed;
    private boolean locked;
    @OneToMany
    @JoinColumn(name = "dungeon_instance_id")
    private Set<TDungeonInstancePlayer> players = new HashSet<>();
}
