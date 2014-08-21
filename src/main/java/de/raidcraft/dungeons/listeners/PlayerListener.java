package de.raidcraft.dungeons.listeners;

import de.raidcraft.connect.api.raidcraftevents.RE_PlayerSwitchServer;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * @author Dragonfire
 */
public class PlayerListener implements Listener {

    private DungeonsPlugin plugin;

    public PlayerListener(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void join(RE_PlayerSwitchServer event) {

        TDungeonPlayer player = plugin.getDatabase().find(TDungeonPlayer.class)
                .where().eq("player_id", event.getPlayer().getUniqueId().toString()).findUnique();
        if (player == null) {
            plugin.getLogger().warning("No Dungeon Player found for: " + event.getPlayer().getName());
            return;
        }
        World w = null;
        try {
            w = plugin.getDungeonManager().getWorld(player.getLastInstance());
            event.getPlayer().teleport(w.getSpawnLocation());
        } catch (DungeonException e) {
           e.printStackTrace();
        }
    }
}
