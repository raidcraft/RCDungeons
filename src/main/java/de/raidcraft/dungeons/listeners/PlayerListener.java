package de.raidcraft.dungeons.listeners;

import de.raidcraft.connect.api.raidcraftevents.RE_PlayerSwitchServer;
import de.raidcraft.connect.commands.DungeonConnect;
import de.raidcraft.dungeons.DungeonsPlugin;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.UUID;

/**
 * @author Dragonfire
 */
public class PlayerListener implements Listener {

    private DungeonsPlugin plugin;
    private HashMap<String, UUID> waitingQueue = new HashMap<>();

    public PlayerListener(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    @EventHandler
    public void join(RE_PlayerSwitchServer event) {

        if (event.isInvalid()) {
            plugin.getLogger().warning("Invalid server join of ("
                    + event.getPlayer().getName() + ")");
            return;
        }
        if (event.getCause().equals(DungeonConnect.START_INSTACE)) {
            String templateWorld = event.getArgs()[0];
            String instance = event.getArgs()[2];
            event.getPlayer().sendMessage("Start dungeon " + templateWorld);

            if (!plugin.getInstanceManager().instanceExists(instance)) {
                plugin.getInstanceManager().createInstance(templateWorld, instance);
            }
            event.getPlayer().teleport(plugin.getInstanceManager().getInstance(instance).getSpawnLocation());
        }
    }
}
