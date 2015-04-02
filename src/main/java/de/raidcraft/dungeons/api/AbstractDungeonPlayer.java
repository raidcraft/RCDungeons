package de.raidcraft.dungeons.api;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;

import java.util.UUID;

/**
 * @author Silthus
 */
@Getter
public abstract class AbstractDungeonPlayer implements DungeonPlayer {

    private final int id;
    private final UUID playerId;
    @Setter
    private Location lastPosition;

    public AbstractDungeonPlayer(int id, UUID playerId) {

        this.id = id;
        this.playerId = playerId;
    }
}
