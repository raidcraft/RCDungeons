package de.raidcraft.dungeons.api.raidcraftevents;

import de.raidcraft.dungeons.api.DungeonInstance;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
@Getter
public class RE_InstanceLoadedEvent extends Event {

    private World world;
    private DungeonInstance instance;

    public RE_InstanceLoadedEvent(World world, DungeonInstance instance) {

        this.world = world;
        this.instance = instance;
    }

    // Bukkit stuff
    private static final HandlerList handlers = new HandlerList();

    public HandlerList getHandlers() {

        return handlers;
    }

    public static HandlerList getHandlerList() {

        return handlers;
    }
}
