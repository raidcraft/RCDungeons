package de.raidcraft.dungeons.commands;

import com.sk89q.minecraft.util.commands.Command;
import com.sk89q.minecraft.util.commands.CommandContext;
import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.minecraft.util.commands.CommandPermissions;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitPlayer;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.Extent;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import de.raidcraft.api.RaidCraftException;
import de.raidcraft.dungeons.DungeonException;
import de.raidcraft.dungeons.DungeonsPlugin;
import de.raidcraft.dungeons.api.Dungeon;
import de.raidcraft.dungeons.api.DungeonInstance;
import de.raidcraft.dungeons.api.DungeonPlayer;
import de.raidcraft.dungeons.util.DungeonUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
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

        try {
            Dungeon dungeon = plugin.getDungeonManager().createDungeon((Player) sender, args.getString(0), args.getJoinedStrings(1));
            sender.sendMessage(ChatColor.GREEN + "Created dungeon " + dungeon.getFriendlyName() + " successfully!");
        } catch (RaidCraftException e) {
            throw new CommandException(e);
        }
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

        try {
            Dungeon dungeon = plugin.getDungeonManager().getDungeon(args.getString(0));
            Player player = (Player) sender;
            World w = plugin.getDungeonManager().createDungeonWorld(player, DungeonUtils.getTemplateWorldName(dungeon.getName()));
            Location location = player.getLocation();
            location.setWorld(w);
            player.teleport(location);

        } catch (RaidCraftException e) {
            throw new CommandException(e.getMessage());
        }
    }

    @Command(
            aliases = {"debug2"},
            desc = "debug2"
    )
    @CommandPermissions("rcdungeons.debug")
    public void debug3(CommandContext args, CommandSender sender) throws CommandException {

        Player creator = (Player) sender;
        creator.setGameMode(GameMode.CREATIVE);
        creator.setFlying(true);
        creator.performCommand("/copy");
        edit(args, sender);
        creator.performCommand("/paste");
    }


    @Command(
            aliases = {"debug2"},
            desc = "debug2"
    )
    @CommandPermissions("rcdungeons.debug")
    public void debug2(CommandContext args, CommandSender sender) throws CommandException {

        try {
            Player creator = (Player) sender;
            WorldEditPlugin worldEdit = (WorldEditPlugin) Bukkit.getPluginManager().getPlugin("WorldEdit");
            LocalSession session = worldEdit.getSession(creator);
            Region region = session.getSelection(session.getSelectionWorld());
            BukkitPlayer wgPlayer = new BukkitPlayer(worldEdit, worldEdit.getServerInterface(), creator);
            Extent editSession = wgPlayer.getExtent();

            // copy to clipboard
            BlockArrayClipboard clipboard = new BlockArrayClipboard(region);
            clipboard.setOrigin(session.getPlacementPosition(wgPlayer));

            ForwardExtentCopy copy = new ForwardExtentCopy(editSession, region, clipboard, region.getMinimumPoint());
            Operations.completeLegacy(copy);
            ClipboardHolder holder = new ClipboardHolder(clipboard, wgPlayer.getWorld().getWorldData());
            session.setClipboard(holder);
            creator.sendMessage(region.getArea() + " block(s) were copied.");

            // paste
            //            Vector to = clipboard.getOrigin();
            editSession = worldEdit.createEditSession(creator);
            Vector to = clipboard.getOrigin();
            to = to.add(0, 20, 0);

            Operation operation = holder
                    .createPaste(clipboard, wgPlayer.getWorld().getWorldData())
                    .to(to).ignoreAirBlocks(false)
                    .build();
            Operations.completeLegacy(operation);
            creator.sendMessage("Pasted at: " + to);

        } catch (IncompleteRegionException | MaxChangedBlocksException e) {
            e.printStackTrace();
        }
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
            DungeonInstance instance = dungeon.createInstance(player.getName());
            instance.teleport(player);
            sender.sendMessage("Created dungeon test instance with the id " + instance.getId());
        } catch (DungeonException e) {
            throw new CommandException(e.getMessage());
        }
    }
}
