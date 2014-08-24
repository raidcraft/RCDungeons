package de.raidcraft.dungeons.tables;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
@Setter
@Getter
@Entity
@Table(name = "dungeons_dungeon_spawns")
public class TDungeonSpawn {

    @Id
    private int id;
    @ManyToOne
    private TDungeon dungeon;
    private double spawnX;
    private double spawnY;
    private double spawnZ;
    private float spawnYaw;
    private float spawnPitch;

    public TDungeonSpawn() {

    }

    public TDungeonSpawn(Location location) {

        this.spawnX = location.getX();
        this.spawnY = location.getY();
        this.spawnZ = location.getZ();
        this.spawnYaw = location.getYaw();
        this.spawnPitch = location.getPitch();
    }

    public Location getLocation() {

        return new Location(Bukkit.getWorld("default"), spawnX, spawnY, spawnZ, spawnYaw, spawnPitch);
    }

    public void setSpawn(Location loc) {

        setSpawnPitch(loc.getPitch());
        setSpawnX(loc.getX());
        setSpawnY(loc.getY());
        setSpawnYaw(loc.getYaw());
        setSpawnZ(loc.getZ());
    }
}
