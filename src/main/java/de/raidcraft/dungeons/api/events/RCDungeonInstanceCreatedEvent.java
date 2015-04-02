package de.raidcraft.dungeons.api.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 * @author Dragonfire
 */
public class RCDungeonInstanceCreatedEvent extends Event {

    private String worldName;

    public RCDungeonInstanceCreatedEvent(String worldName) {

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
