package rocks.learnercouncil.wardrobewarps.commands.closet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import rocks.learnercouncil.wardrobewarps.WardrobeWarps;
import rocks.learnercouncil.wardrobewarps.commands.CommandArgument;
import rocks.learnercouncil.wardrobewarps.commands.CommandResult;
import rocks.learnercouncil.wardrobewarps.commands.closet.arguments.AddArg;
import rocks.learnercouncil.wardrobewarps.commands.closet.arguments.BypassArg;
import rocks.learnercouncil.wardrobewarps.commands.closet.arguments.HelpArg;
import rocks.learnercouncil.wardrobewarps.commands.closet.arguments.RemoveArg;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ClosetCommand implements TabExecutor {

    public static CommandArgument[] ARGUMENTS = {
            new AddArg(),
            new BypassArg(),
            new RemoveArg(),
            new HelpArg()
    };

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!command.getName().equalsIgnoreCase("closet")) return false;
        if(!(sender instanceof Player)) {
            sender.spigot().sendMessage(CommandResult.needsPlayer(label));
            return true;
        }
        if(!sender.hasPermission("wardrobewarps.commands.closet")) {
            sender.spigot().sendMessage(CommandResult.NO_PERMISSION);
            return true;
        }

        sender.spigot().sendMessage(CommandArgument.parseCommand(sender, command, label, args, ARGUMENTS));
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("wardrobewarps.commands.closet")) return Collections.emptyList();
        return CommandArgument.parseTabCompletion(sender, command, label, args, ARGUMENTS);
    }

    public void register(WardrobeWarps plugin) {
        PluginCommand command = Objects.requireNonNull(plugin.getCommand("closet"));
        command.setExecutor(this);
        command.setTabCompleter(this);
    }
}
