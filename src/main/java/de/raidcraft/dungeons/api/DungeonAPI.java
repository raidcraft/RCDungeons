package de.raidcraft.dungeons.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Dragonfire
 */
public interface DungeonAPI {

    void create(Player creator, String dungeonName, String friendlyName);

    void edit(Player player, String dungeonName);

    void start(String dungeonName, Player player, int radius);

    void end(DungeonInstance instance, DungeonReason reason);

    void exit(Player player);

    boolean isDungeonTemplate(World world);

    boolean isDungeonInstance(World world);

    Optional<DungeonInstance> getDungeonInstance(World world);

    Optional<Dungeon> getDungeon(World world);
}
