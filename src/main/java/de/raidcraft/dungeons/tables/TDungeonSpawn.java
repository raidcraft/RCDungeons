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
@Table(name = "rc_dungeons_dungeon_spawns")
public class TDungeonSpawn {

    @Id
    private int id;
    @ManyToOne
    private TDungeon dungeon;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private float spawnYaw;
    private float spawnPitch;

    public TDungeonSpawn() {

    }

    public TDungeonSpawn(Location location) {

        this.spawnX = location.getBlockX();
        this.spawnY = location.getBlockY();
        this.spawnZ = location.getBlockZ();
        this.spawnYaw = location.getYaw();
        this.spawnPitch = location.getPitch();
    }

    public Location getLocation() {

        return new Location(Bukkit.getWorld("default"), (double) spawnX, (double) spawnY, (double) spawnZ, spawnYaw, spawnPitch);
    }

    public void setSpawn(Location loc) {

        setSpawnPitch(loc.getPitch());
        setSpawnX(loc.getBlockX());
        setSpawnY(loc.getBlockY());
        setSpawnYaw(loc.getYaw());
        setSpawnZ(loc.getBlockZ());
    }
}
