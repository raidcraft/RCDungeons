package de.raidcraft.dungeons.tables;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author Silthus
 */
@Getter
@Setter
@Entity
@Table(name = "rc_dungeons_dungeon_players")
public class TDungeonPlayer {

    @Id
    private int id;
    @Deprecated
    private String player;
    private UUID playerId;
    private String lastWorld;
    private double lastX;
    private double lastY;
    private double lastZ;
    private float lastYaw;
    private float lastPitch;
    @OneToMany
    @JoinColumn(name = "player_id")
    private List<TDungeonInstancePlayer> instances = new ArrayList<>();
    @Transient
    private Location cacheLastWorld;

    public Location getLastPosition() {

        if (lastWorld == null) {
            return null;
        }
        if (cacheLastWorld == null) {
            cacheLastWorld = new Location(Bukkit.getWorld(lastWorld),
                    lastX, lastY, lastZ, lastYaw, lastPitch);
        }
        return cacheLastWorld;
    }

    public void init(Player bukkitPlayer) {

        Location loc = bukkitPlayer.getLocation();
        setLastPitch(loc.getPitch());
        setLastYaw(loc.getYaw());
        setLastX(loc.getX());
        setLastY(loc.getY());
        setLastZ(loc.getZ());
        setLastWorld(loc.getWorld().getName());
        setPlayerId(bukkitPlayer.getUniqueId());
    }
}
