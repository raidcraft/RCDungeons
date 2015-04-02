package de.raidcraft.dungeons.api;

import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Optional;

/**
 * @author Dragonfire
 */
public interface DungeonAPI {

    public void create(Player creator, String dungeonName, String friendlyName);

    public void edit(Player player, String dungeonName);

    public void start(String dungeonName, Player player, int radius);

    public void end(DungeonInstance instance, DungeonReason reason);

    public void exit(Player player);

    public boolean isDungeonTemplate(World world);

    public boolean isDungeonInstance(World world);

    public Optional<DungeonInstance> getDungeonInstance(World world);

    public Optional<Dungeon> getDungeon(World world);
}
