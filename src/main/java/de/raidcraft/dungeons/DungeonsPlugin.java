package de.raidcraft.dungeons;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.NestedCommand;
import de.raidcraft.api.BasePlugin;
import de.raidcraft.api.config.ConfigurationBase;
import de.raidcraft.api.config.Setting;
import de.raidcraft.dungeons.commands.AdminCommands;
import de.raidcraft.dungeons.tables.TDungeon;
import de.raidcraft.dungeons.tables.TDungeonInstance;
import de.raidcraft.dungeons.tables.TDungeonInstancePlayer;
import de.raidcraft.dungeons.tables.TDungeonPlayer;
import de.raidcraft.dungeons.tables.TDungeonSpawn;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Silthus
 */
public class DungeonsPlugin extends BasePlugin {

    private DungeonManager dungeonManager;
    private LocalConfiguration config;

    @Override
    public void enable() {

        this.config = configure(new LocalConfiguration(this), true);
        this.dungeonManager = new DungeonManager(this);
        registerCommands(BaseCommands.class);
    }

    @Override
    public void disable() {

    }

    @Override
    public void reload() {

        getDungeonManager().reload();
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
    }
}
