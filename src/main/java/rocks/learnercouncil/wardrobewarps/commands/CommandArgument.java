package rocks.learnercouncil.wardrobewarps.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.wardrobewarps.WardrobeWarps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public interface CommandArgument {

    static BaseComponent[] parseCommand(CommandSender sender, Command cmd, String label, String[] args, CommandArgument[] arguments) {
        if(args.length < 1) {
            WardrobeWarps.getPlugin().getServer().dispatchCommand(sender, label + " help");
            return CommandResult.NONE;
        }
        for (CommandArgument a : arguments) {
            BaseComponent[] result = a.execute(sender, cmd, label, args);
            if (result.length != 0) {
                return result;
            }
        }
        return CommandResult.invalidArgs(label);
    }
    static List<String> parseTabCompletion(CommandSender sender, Command cmd, String label, String[] args, CommandArgument[] arguments) {
        List<String> completions = new ArrayList<>();
        List<String> argumentCompletions = Arrays.stream(arguments)
                .flatMap(a -> a.tabComplete(sender, cmd, label, args).stream())
                .collect(Collectors.toList());
        if(args.length > 0) StringUtil.copyPartialMatches(args[args.length - 1], argumentCompletions, completions);
        Collections.sort(completions);
        return completions;
    }

    BaseComponent[] execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

    List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args);

}
