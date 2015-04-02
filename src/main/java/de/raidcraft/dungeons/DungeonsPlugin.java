package de.raidcraft.dungeons;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.RaidCraft;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.connect.ConnectPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonAPI;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonReason;
import de.raidcraft.dungeons.commands.AdminCommands;
import de.raidcraft.dungeons.commands.PlayerCommands;
import de.raidcraft.dungeons.listeners.PlayerListener;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import de.raidcraft.util.PlayerUtil;
import lombok.Getter;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author Silthus
 */
@Getter
public class DungeonsPlugin extends BasePlugin implements DungeonAPI {

    private PlayerManager playerManager;
    private DungeonManager dungeonManager;
    private WorldManager worldManager;
    private InstanceManager instanceManager;

    private LocalConfiguration config;

    @Override
    public void enable() {

        this.config = configure(new LocalConfiguration(this), true);
        this.playerManager = new PlayerManager(this);
        this.dungeonManager = new DungeonManager(this);
        this.worldManager = new WorldManager(this);
        this.instanceManager = new InstanceManager(this);
        registerCommands(BaseCommands.class);
        registerEvents(new PlayerListener(this));
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

        getPlayerManager().reload();
        getDungeonManager().reload();
        getWorldManager().reload();
        getInstanceManager().reload();
    }

    public DungeonManager getDungeonManager() {

        return dungeonManager;
    }

    public LocalConfiguration getConfig() {

        return config;
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {

        ArrayList<Class<?>> tables = new ArrayList<>();
        tables.add(TDungeon.class);
        tables.add(TDungeonInstance.class);
        tables.add(TDungeonPlayer.class);
        tables.add(TDungeonSpawn.class);
        tables.add(TDungeonInstancePlayer.class);
        return tables;
    }

    public static class LocalConfiguration extends ConfigurationBase<DungeonsPlugin> {


        public LocalConfiguration(DungeonsPlugin plugin) {

            super(plugin, "config.yml");
        }

        @Setting("prefix.dungeon-template")
        public String dungeonTemplatePrefix = "rcdungeon_";
        @Setting("prefix.dungeon-instance")
        public String dungeonInstancePrefix = "instance_";
        @Setting("trim-frequency")
        public double trimFrequency = 1.0;
        @Setting("default-reset-time")
        public double default_reset_time = 86400.0;
    }

    public static class BaseCommands {

        private final DungeonsPlugin plugin;

        public BaseCommands(DungeonsPlugin plugin) {

            this.plugin = plugin;
        }

        @Command(
                aliases = {"rcda", "dungeonadmin"},
                desc = "Gives access to the dungeon admin commands."
        )
        @NestedCommand(AdminCommands.class)
        public void admin(CommandContext args, CommandSender sender) {

        }

        @Command(
                aliases = {"rcd", "dungeon"},
                desc = "Gives access to the dungeon player commands."
        )
        @NestedCommand(PlayerCommands.class)
        public void player(CommandContext args, CommandSender sender) {

        }
    }

    @Override
    public void create(Player creator, String dungeonName, String friendlyName) {

        try {
            dungeonManager.createDungeon(creator, dungeonName, friendlyName);
        } catch (RaidCraftException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void edit(Player player, String dungeonName) {

        try {
            World w = dungeonManager.getWorld(dungeonName);
            player.teleport(w.getSpawnLocation());
        } catch (RaidCraftException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start(String dungeonName, Player player, int radius) {

        try {
            List<UUID> playerIds = PlayerUtil.getPlayerNearby(player, radius).stream()
                    .map(Player::getUniqueId).collect(Collectors.toList());
            Dungeon dungeon = dungeonManager.getDungeon(dungeonName);
            instanceManager.createDungeonInstance(dungeon, playerIds.toArray(new UUID[playerIds.size()]));

        } catch (RaidCraftException e) {
            e.printStackTrace();
        }
    }

    public void start(String dungeonName, Player player) {

        try {
            Dungeon dungeon = dungeonManager.getDungeon(dungeonName);
            instanceManager.createDungeonInstance(dungeon, player.getUniqueId());

        } catch (RaidCraftException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void end(DungeonInstance instance, DungeonReason reason) {

        getInstanceManager().end(instance, reason);
    }

    @Override
    public void exit(Player player) {

        RaidCraft.getComponent(ConnectPlugin.class).teleportBack(player);
    }

    @Override
    public boolean isDungeonTemplate(World world) {

        return getDungeonManager().getDungeon(world).isPresent();
    }

    @Override
    public boolean isDungeonInstance(World world) {

        return getInstanceManager().getInstance(world).isPresent();
    }

    @Override
    public Optional<DungeonInstance> getDungeonInstance(World world) {

        return getInstanceManager().getInstance(world);
    }

    @Override
    public Optional<Dungeon> getDungeon(World world) {

        return getDungeonManager().getDungeon(world);
    }
}
