package de.raidcraft.dungeons.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonException;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.util.UUIDUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Silthus
 */
public class AdminCommands {

    private final DungeonsPlugin plugin;

    public AdminCommands(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"reload"},
            desc = "Reloads the dungeons plugin"
    )
    @CommandPermissions("rcdungeons.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded the dungeons plugin sucessfully!");
    }

    @Command(
            aliases = {"create"},
            desc = "Creates a new dungeon schematic",
            min = 2,
            usage = "<name> <friendlyName>"
    )
    @CommandPermissions("rcdungeons.create")
    public void create(CommandContext args, CommandSender sender) throws CommandException {

        plugin.create((Player) sender, args.getString(0), args.getString(1));
    }

    @Command(
            aliases = {"back"},
            desc = "Port you back to the main world",
            usage = "<world_name>"
    )
    @CommandPermissions("rcdungeons.edit")
    public void back(CommandContext args, CommandSender sender) throws CommandException {

        Player player = (Player) sender;
        Location loc = player.getLocation();
        String worldName = "world";
        if (args.argsLength() >= 1) {
            worldName = args.getJoinedStrings(0);
        }
        World w = Bukkit.getWorld(worldName);
        if (w == null) {
            throw new CommandException("World (" + worldName + ") not exists.");
        }
        loc.setWorld(w);
        player.teleport(loc);
    }

    @Command(
            aliases = {"edit"},
            desc = "Edit a Template World",
            min = 1,
            usage = "<name>"
    )
    @CommandPermissions("rcdungeons.edit")
    public void edit(CommandContext args, CommandSender sender) throws CommandException {

        plugin.edit((Player) sender, args.getString(0));
    }

    @Command(
            aliases = {"start"},
            desc = "Starts the Quest Creation Wizard",
            min = 1,
            usage = "<dungeon_name> <players...>"
    )
    @CommandPermissions("rcdungeons.start")
    public void start(CommandContext args, CommandSender sender) {

        Player player = (args.argsLength() == 2) ? Bukkit.getPlayer(UUIDUtil.convertPlayer(args.getString(1))) : (Player) sender;
        Bukkit.broadcastMessage("start " + args.getString(0) + ": " + args.getString(1));
    }

    @Command(
            aliases = {"save"},
            desc = "Save you current world"
    )
    @CommandPermissions("rcdungeons.save")
    public void save(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Playercommand");
        }
        ((Player) sender).getWorld().save();
    }

    @Command(
            aliases = {"test"},
            desc = "Creates a test instance of the dungeon",
            min = 1,
            usage = "<name>"
    )
    @CommandPermissions("rcdungeons.test")
    public void test(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Dungeon dungeon = plugin.getDungeonManager().getDungeon(args.getString(0));
            DungeonPlayer player = plugin.getDungeonManager().getPlayer((Player) sender);
            DungeonInstance instance = dungeon.createInstance(player.getPlayerId());
            instance.teleport(player);
            sender.sendMessage("Created dungeon test instance with the id " + instance.getId());
        } catch (DungeonException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
