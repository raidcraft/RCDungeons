package de.raidcraft.dungeons.tables;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Silthus
 */
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

    public int getId() {

        return id;
    }

    public void setId(int id) {

        this.id = id;
    }

    public TDungeon getDungeon() {

        return dungeon;
    }

    public void setDungeon(TDungeon dungeon) {

        this.dungeon = dungeon;
    }

    public double getSpawnX() {

        return spawnX;
    }

    public void setSpawnX(double spawnX) {

        this.spawnX = spawnX;
    }

    public double getSpawnY() {

        return spawnY;
    }

    public void setSpawnY(double spawnY) {

        this.spawnY = spawnY;
    }

    public double getSpawnZ() {

        return spawnZ;
    }

    public void setSpawnZ(double spawnZ) {

        this.spawnZ = spawnZ;
    }

    public float getSpawnYaw() {

        return spawnYaw;
    }

    public void setSpawnYaw(float spawnYaw) {

        this.spawnYaw = spawnYaw;
    }

    public float getSpawnPitch() {

        return spawnPitch;
    }

    public void setSpawnPitch(float spawnPitch) {

        this.spawnPitch = spawnPitch;
    }
}
