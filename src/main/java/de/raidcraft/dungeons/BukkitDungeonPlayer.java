package de.raidcraft.dungeons;

import de.raidcraft.dungeons.api.AbstractDungeonPlayer;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.tables.TDungeonPlayer;

import java.util.List;

/**
 * @author Silthus
 */
public class BukkitDungeonPlayer extends AbstractDungeonPlayer {

    public BukkitDungeonPlayer(TDungeonPlayer player) {

        super(player.getId(), player.getName());
        setLastPosition(player.getLastPosition());
    }

    @Override
    public List<DungeonInstance> getDungeonInstances() {
        //TODO: implement
    }

    @Override
    public DungeonInstance getDungeonInstance(Dungeon dungeon) {
        //TODO: implement
    }

    @Override
    public DungeonInstance getActiveInstance() {
        //TODO: implement
    }

    @Override
    public void leaveActiveDungeon(DungeonReason reason) {
        //TODO: implement
    }

    @Override
    public void save() {
        //TODO: implement
    }
}
