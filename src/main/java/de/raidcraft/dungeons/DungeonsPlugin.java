package de.raidcraft.dungeons;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.worldedit.CopyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class DungeonsPlugin extends BasePlugin {

    private DungeonManager dungeonManager;
    private CopyManager copyManager;

    @Override
    public void enable() {

        this.dungeonManager = new DungeonManager(this);
        this.copyManager = new CopyManager(this);
    }

    @Override
    public void disable() {

    }

    public CopyManager getCopyManager() {

        return copyManager;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TDungeon.class);
        tables.add(TDungeonInstance.class);
        tables.add(TDungeonPlayer.class);
        return tables;
    }
}
