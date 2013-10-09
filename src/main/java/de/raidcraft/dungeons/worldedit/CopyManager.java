package de.raidcraft.dungeons.worldedit;

// $Id$
/*
 * CraftBook Copyright (C) 2010 sk89q <http://www.sk89q.com>
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public
 * License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not,
 * see <http://www.gnu.org/licenses/>.
 */

import com.sk89q.worldedit.data.DataException;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.Component;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.util.CaseInsensitiveMap;
import de.raidcraft.util.HistoryHashMap;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Used to load, save, and cache cuboid copies.
 *
 * @author sk89q, Silthus
 */
public class CopyManager implements Component {

    /**
     * Cache.
     */
    private final HashMap<String, HistoryHashMap<String, CuboidCopy>> cache = new CaseInsensitiveMap<>();

    /**
     * Remembers missing copies so as to not look for them on disk.
     */
    private final HashMap<String, HistoryHashMap<String, Long>> missing = new CaseInsensitiveMap<>();

    private final DungeonsPlugin plugin;

    public CopyManager(DungeonsPlugin plugin) {

        this.plugin = plugin;
        RaidCraft.registerComponent(CopyManager.class, this);
    }

    /**
     * Checks if the area and namespace exists.
     *
     * @param dungeon to check
     */
    public boolean isExistingDungeonFile(String dungeon) {

        File file = new File(plugin.getDataFolder(), "dungeons/");
        return new File(file, dungeon).exists();
    }

    /**
     * Load a copy from disk. This may return a cached copy. If the copy is not cached,
     * the file will be loaded from disk if possible. If the copy
     * does not exist, an exception will be raised. An exception may be raised if the file exists but cannot be read
     * for whatever reason.
     *
     * @param dungeon to load
     *
     * @return loaded dungeon copy
     *
     * @throws java.io.IOException
     * @throws MissingCuboidCopyException
     * @throws CuboidCopyException
     */
    public CuboidCopy load(World world, Dungeon dungeon) throws IOException, CuboidCopyException {

        String cacheKey = dungeon.getName();

        HistoryHashMap<String, Long> missing = getMissing(world.getUID().toString());

        if (missing.containsKey(cacheKey)) {
            long lastCheck = missing.get(cacheKey);
            if (lastCheck > System.currentTimeMillis()) throw new MissingCuboidCopyException(dungeon.getName());
        }

        HistoryHashMap<String, CuboidCopy> cache = getCache(world.getUID().toString());

        CuboidCopy copy = cache.get(cacheKey);

        if (copy == null) {
            File folder = new File(plugin.getDataFolder(), "dungeons/");
            copy = CuboidCopy.load(new File(folder, dungeon.getName() + getFileSuffix()), world);
            missing.remove(cacheKey);
            cache.put(cacheKey, copy);
            return copy;
        }

        return copy;
    }

    /**
     * Save a copy to disk. The copy will be cached.
     *
     * @param dungeon
     * @param copyFlat
     *
     * @throws java.io.IOException
     */
    public void save(World world, String dungeon, CuboidCopy copyFlat) throws IOException, DataException {

        HistoryHashMap<String, CuboidCopy> cache = getCache(world.getUID().toString());

        File folder = new File(plugin.getDataFolder(), "dungeons/");

        if (!folder.exists()) {
            folder.mkdirs();
        }

        copyFlat.save(new File(folder, dungeon + getFileSuffix()));
        missing.remove(dungeon);
        cache.put(dungeon, copyFlat);
    }

    private HistoryHashMap<String, CuboidCopy> getCache(String world) {

        HistoryHashMap<String, CuboidCopy> worldCache = cache.get(world);
        if (worldCache != null) {
            return worldCache;
        } else {
            worldCache = new HistoryHashMap<>(10);
            cache.put(world, worldCache);
            return worldCache;
        }
    }

    private HistoryHashMap<String, Long> getMissing(String world) {

        HistoryHashMap<String, Long> worldCache = missing.get(world);
        if (worldCache != null) {
            return worldCache;
        } else {
            worldCache = new HistoryHashMap<>(10);
            missing.put(world, worldCache);
            return worldCache;
        }
    }

    private String getFileSuffix() {

        return ".schematic";
    }
}