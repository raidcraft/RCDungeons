package de.raidcraft.dungeons.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import de.raidcraft.dungeons.DungeonsPlugin;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Dragonfire
 */
public class PlayerCommands {

    private final DungeonsPlugin plugin;

    public PlayerCommands(DungeonsPlugin plugin) {

        this.plugin = plugin;
    }

    @Command(
            aliases = {"exit", "abort", "back"},
            desc = "Leave the instance"
    )
    @CommandPermissions("rcdungeons.reload")
    public void exit(CommandContext args, CommandSender sender) {

        plugin.exit((Player) sender);
    }
}