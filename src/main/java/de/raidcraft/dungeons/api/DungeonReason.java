package de.raidcraft.dungeons.api;

/**
 * @author Silthus
 */
public enum DungeonReason {

    UNLOAD("Dungeon wurde geschlossen."),
    EXPIRE("Die ID des Dungeons ist ausgelaufen."),
    KICK("Du wurdest aus dem Dungeon geworfen."),
    FINISH("Der Dungeon wurde geschafft und kann nicht mehr betreten werden."),
    LOCKDOWN("Der Dungeon wurde von einem Admin gesperrt.");

    private final String message;

    private DungeonReason(String message) {

        this.message = message;
    }
}
