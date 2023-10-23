package rocks.learnercouncil.wardrobewarps.commands.closet.arguments;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.wardrobewarps.Closet;
import rocks.learnercouncil.wardrobewarps.ClosetRegistry;
import rocks.learnercouncil.wardrobewarps.commands.CommandArgument;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static rocks.learnercouncil.wardrobewarps.commands.CommandResult.*;

public class RemoveArg implements CommandArgument {
    @Override
    public BaseComponent[] execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!args[0].equalsIgnoreCase("remove")) return NONE;
        if(args.length == 1) return MUST_SPECIFY_EXISTING_MAP;
        if(args.length > 3) return TOO_MANY_ARGS;

        String map = args[1];
        if(args.length == 2) return ClosetRegistry.removeAll(map) ? closetsRemoved(map) : noClosetsInMap(map);

        String name = args[2];
        return ClosetRegistry.remove(map, name) ? closetRemoved(map, name) : closetNotExist(map, name);
    }

    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return Collections.singletonList("remove");
        if(args.length == 2 && args[0].equalsIgnoreCase("remove")) return ClosetRegistry.getMaps().stream().toList();
        if(args.length == 3 && args[0].equalsIgnoreCase("remove")) return ClosetRegistry.getClosets(args[1]).stream().map(Closet::getName).collect(Collectors.toList());
        return Collections.emptyList();
    }
}
