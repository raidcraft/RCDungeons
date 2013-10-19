package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
@Table(name = "dungeons_dungeon_players")
public class TDungeonPlayer {

    @Id
    private int id;
    @NotNull
    private String player;
    private String lastWorld;
    private double lastX;
    private double lastY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;
    @OneToMany
    @JoinColumn(name = "player_id")
    private List<TDungeonInstancePlayer> instances = new ArrayList<>();


    public Location getLastPosition() {

        if (lastWorld == null) {
            return null;
        }
        return new Location(Bukkit.getWorld(lastWorld), lastX, lastY, lastZ, lastYaw, lastPitch);
    }

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

    public String getLastWorld() {

        return lastWorld;
    }

    public void setLastWorld(String lastWorld) {

        this.lastWorld = lastWorld;
    }

    public double getLastX() {

        return lastX;
    }

    public void setLastX(double lastX) {

        this.lastX = lastX;
    }

    public double getLastY() {

        return lastY;
    }

    public void setLastY(double lastY) {

        this.lastY = lastY;
    }

    public double getLastZ() {

        return lastZ;
    }

    public void setLastZ(double lastZ) {

        this.lastZ = lastZ;
    }

    public float getLastYaw() {

        return lastYaw;
    }

    public void setLastYaw(float lastYaw) {

        this.lastYaw = lastYaw;
    }

    public float getLastPitch() {

        return lastPitch;
    }

    public void setLastPitch(float lastPitch) {

        this.lastPitch = lastPitch;
    }

    public List<TDungeonInstancePlayer> getInstances() {

        return instances;
    }

    public void setInstances(List<TDungeonInstancePlayer> instances) {

        this.instances = instances;
    }
}
