package rocks.learnercouncil.wardrobewarps.commands.closet.arguments;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.wardrobewarps.Closet;
import rocks.learnercouncil.wardrobewarps.commands.CommandArgument;

import java.util.Collections;
import java.util.List;

import static rocks.learnercouncil.wardrobewarps.commands.CommandResult.*;

public class BypassArg implements CommandArgument {
    @Override
    public BaseComponent[] execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!args[0].equalsIgnoreCase("bypass")) return NONE;
        if(args.length > 1) return TOO_MANY_ARGS;

        return Closet.toggleBypassing((Player) sender) ? NOW_BYPASSING_CLOSETS : NO_LONGER_BYPASSING_CLOSETS;
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return Collections.singletonList("bypass");
        return Collections.emptyList();
    }
}
