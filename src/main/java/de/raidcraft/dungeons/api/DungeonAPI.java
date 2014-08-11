package de.raidcraft.dungeons.api;

import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public interface DungeonAPI {

    public void create(Player creator, String dungeonName, String friendlyName);

    public void edit(Player player, String dungeonName);

    public void start(String dungeonName, Player player, int radius);

    public void end(String instanceWorldName);
}
