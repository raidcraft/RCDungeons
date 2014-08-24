package de.raidcraft.dungeons.listeners;

import de.raidcraft.RaidCraft;
import de.raidcraft.connect.ConnectPlugin;
import de.raidcraft.connect.api.raidcraftevents.RE_PlayerSwitchServer;
import de.raidcraft.connect.commands.DungeonConnect;
import de.raidcraft.connect.tables.TConnectPlayer;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
            String dungeonName = event.getArgs()[0];
            Player player = event.getPlayer();
            try {
                Dungeon dungeon = plugin.getDungeonManager().getDungeon(dungeonName);
                DungeonPlayer dungeonPlayer = plugin.getPlayerManager()
                        .getPlayer(player.getUniqueId());
                DungeonInstance instance = dungeonPlayer.getDungeonInstance(dungeon);
                if (instance == null) {
                    player.sendMessage("Create dungeon " + dungeonName);
                    // grab all players of the party
                    List<UUID> uuids =
                            RaidCraft.getComponent(ConnectPlugin.class).getSimilarPlayerIds(event)
                                    .stream().map(TConnectPlayer::getPlayer).collect(Collectors.toList());
                    instance = dungeon.createInstance(
                            uuids.toArray(new UUID[uuids.size()]));
                }
                player.teleport(instance.getWorld().getSpawnLocation());
                player.sendMessage("Welcome in " + instance.getWorld().getName());
            } catch (DungeonException e) {
                e.printStackTrace();
            }
        }
    }
}
