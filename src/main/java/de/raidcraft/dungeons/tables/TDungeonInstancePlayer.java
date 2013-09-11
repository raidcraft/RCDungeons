package de.raidcraft.dungeons.tables;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Entity
@Table(name = "dungeons_dungeon_instance_players")
public class TDungeonInstancePlayer {

    @Id
    private int id;
    @ManyToOne
    private TDungeonPlayer player;
    @ManyToOne
    private TDungeonInstance instance;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TDungeonPlayer getPlayer() {

        return player;
    }

    public void setPlayer(TDungeonPlayer player) {

        this.player = player;
    }

    public TDungeonInstance getInstance() {

        return instance;
    }

    public void setInstance(TDungeonInstance instance) {

        this.instance = instance;
    }
}
