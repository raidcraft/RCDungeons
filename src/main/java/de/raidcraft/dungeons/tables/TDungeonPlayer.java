package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.sql.Timestamp;

/**
 * @author Silthus
 */
@Entity
@Table(name = "rcdungeons_dungeon_players")
public class TDungeonPlayer {

    @Id
    private int id;
    @NotNull
    private String player;
    @ManyToOne
    private TDungeonInstance dungeonInstance;
    private Timestamp joinTime;
    private Timestamp lastJoin;

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public TDungeonInstance getDungeonInstance() {

        return dungeonInstance;
    }

    public void setDungeonInstance(TDungeonInstance dungeonInstance) {

        this.dungeonInstance = dungeonInstance;
    }

    public Timestamp getJoinTime() {

        return joinTime;
    }

    public void setJoinTime(Timestamp joinTime) {

        this.joinTime = joinTime;
    }

    public Timestamp getLastJoin() {

        return lastJoin;
    }

    public void setLastJoin(Timestamp lastJoin) {

        this.lastJoin = lastJoin;
    }
}
