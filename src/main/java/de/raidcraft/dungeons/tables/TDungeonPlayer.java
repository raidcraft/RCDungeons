package de.raidcraft.dungeons.tables;

import com.avaje.ebean.validation.NotNull;
import org.bukkit.Bukkit;
import org.bukkit.Location;

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
@Table(name = "rcdungeons_dungeon_players")
public class TDungeonPlayer {

    @Id
    private int id;
    @NotNull
    private String name;
    private String lastWorld;
    private double lastX;
    private double lastY;
    private double lastZ;
    private long lastYaw;
    private long lastPitch;
    @OneToMany
    @JoinColumn(name = "player_id")
    private List<TDungeonInstancePlayer> instances;


    public Location getLastPosition() {

        return new Location(Bukkit.getWorld(lastWorld), lastX, lastY, lastZ, lastYaw, lastPitch);
    }

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

    public long getLastYaw() {

        return lastYaw;
    }

    public void setLastYaw(long lastYaw) {

        this.lastYaw = lastYaw;
    }

    public long getLastPitch() {

        return lastPitch;
    }

    public void setLastPitch(long lastPitch) {

        this.lastPitch = lastPitch;
    }

    public List<TDungeonInstancePlayer> getInstances() {

        return instances;
    }

    public void setInstances(List<TDungeonInstancePlayer> instances) {

        this.instances = instances;
    }
}
