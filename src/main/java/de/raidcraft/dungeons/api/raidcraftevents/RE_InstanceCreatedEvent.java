package de.raidcraft.dungeons.api.raidcraftevents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RE_InstanceCreatedEvent extends Event {

    private String worldName;

    public RE_InstanceCreatedEvent(String worldName) {

        this.worldName = worldName;
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
