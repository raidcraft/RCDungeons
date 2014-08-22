package de.raidcraft.dungeons.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonReason;
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
    @CommandPermissions("rcdungeons.admin.reload")
    public void reload(CommandContext args, CommandSender sender) {

        plugin.reload();
        sender.sendMessage(ChatColor.GREEN + "Reloaded the dungeons plugin sucessfully!");
    }

    @Command(
            aliases = {"create"},
            desc = "Creates a new dungeon with selected schematic",
            min = 2,
            usage = "<name> <friendlyName>"
    )
    @CommandPermissions("rcdungeons.admin.create")
    public void create(CommandContext args, CommandSender sender) throws CommandException {

        plugin.create((Player) sender, args.getString(0), args.getString(1));
    }

    @Command(
            aliases = {"back"},
            desc = "Port you back to the overworld",
            usage = "<world_name>"
    )
    @CommandPermissions("rcdungeons.admin.back")
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
            usage = "<dungeon_name>"
    )
    @CommandPermissions("rcdungeons.admin.edit")
    public void edit(CommandContext args, CommandSender sender) throws CommandException {

        plugin.edit((Player) sender, args.getString(0));
    }

    @Command(
            aliases = {"save"},
            desc = "Save you current world"
    )
    @CommandPermissions("rcdungeons.admin.save")
    public void save(CommandContext args, CommandSender sender) throws CommandException {

        if (!(sender instanceof Player)) {
            throw new CommandException("Playercommand");
        }
        ((Player) sender).getWorld().save();
    }

    @Command(
            aliases = {"end"},
            desc = "close the instance"
    )
    @CommandPermissions("rcdungeons.admin.end")
    public void end(CommandContext args, CommandSender sender) throws CommandException {

        DungeonInstance instance = plugin.getInstanceManager().getInstance(((Player) sender).getWorld());
        if (instance == null) {
            throw new CommandException("You are not in a instance");
        }
        plugin.end(instance, DungeonReason.FINISH);
    }
}
