package de.raidcraft.dungeons.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import org.bukkit.ChatColor;
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

        try {
            Dungeon dungeon = plugin.getDungeonManager().createDungeon((Player) sender, args.getString(0), args.getJoinedStrings(1));
            sender.sendMessage(ChatColor.GREEN + "Created dungeon " + dungeon.getFriendlyName() + " successfully!");
        } catch (RaidCraftException e) {
            throw new CommandException(e);
        }
    }

    @Command(
            aliases = {"test"},
            desc = "Creates a test instance of the dungeon",
            min = 1,
            usage = "<name>"
    )
    public void test(CommandContext args, CommandSender sender) {

        Dungeon dungeon = plugin.getDungeonManager().getDungeon(args.getString(0));
        DungeonPlayer player = plugin.getDungeonManager().getPlayer((Player) sender);
        DungeonInstance instance = dungeon.createInstance(player.getName());
        instance.teleport(player);
        sender.sendMessage("Created dungeon test instance with the id " + instance.getId());
    }
}
