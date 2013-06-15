package de.raidcraft.dungeons;

import de.raidcraft.api.BasePlugin;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonPlayer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class DungeonsPlugin extends BasePlugin {

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

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
