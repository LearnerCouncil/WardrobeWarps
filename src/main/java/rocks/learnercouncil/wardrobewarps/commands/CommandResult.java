package rocks.learnercouncil.wardrobewarps.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.hover.content.Text;

import java.util.Arrays;
import java.util.stream.Collectors;

public class CommandResult {

    private static final ChatColor
            PREFIX = ChatColor.DARK_PURPLE,
            ERROR = ChatColor.DARK_RED,
            RESULT = ChatColor.LIGHT_PURPLE,
            SPECIAL = ChatColor.BLUE;

    private static ComponentBuilder prefix() {
        return new ComponentBuilder("[Closets] ").color(PREFIX);
    }
    private static BaseComponent[] error(String message) {
        return prefix().append(message).color(ERROR).create();
    }
    private static BaseComponent[] result(String message) {
        return prefix().append(message).color(RESULT).create();
    }

    public static final BaseComponent[]
            NONE = {},
            NOW_BYPASSING_CLOSETS = result("You are now bypassing closet teleports."),
            NO_LONGER_BYPASSING_CLOSETS = result("You are no longer bypassing closet teleports.");

    public static BaseComponent[] closetCreated(String map, String name) {
        return prefix().append("Closet '").color(RESULT)
                .append(name).color(SPECIAL)
                .append("' successfully created in map '").color(RESULT)
                .append(map).color(SPECIAL)
                .append("'.").color(RESULT)
                .create();
    }
    public static BaseComponent[] closetRemoved(String map, String name) {
        return prefix().append("Closet '").color(RESULT)
                .append(name).color(SPECIAL)
                .append("' successfully removed from map '").color(RESULT)
                .append(map).color(SPECIAL)
                .append("'.").color(RESULT)
                .create();
    }
    public static BaseComponent[] closetsRemoved(String map) {
        return prefix().append("All closets successfully removed from map '").color(RESULT)
                .append(map).color(SPECIAL)
                .append("'.").color(RESULT)
                .create();
    }

    public static final BaseComponent[]
            NO_PERMISSION = error("You don't have permission to execute this command."),
            TOO_MANY_ARGS = error("Too many arguments."),
            MUST_SPECIFY_NEW_MAP = error("You must specify a map for this closet to belong to."),
            MUST_SPECIFY_EXISTING_MAP = error("You must specify a map that a closet belongs to."),
            MUST_SPECIFY_NEW_CLOSET_NAME = error("You must specify a name for this closet."),
            MUST_BE_FACING_DOOR = error("You must be facing a door.");

    public static BaseComponent[] invalidArgs(String label) {
        return error("Invalid arguments. Try '/" + label + " help' for help.");
    }
    public static BaseComponent[] needsPlayer(String label) {
        return error("'" + label + "' must be executed by a player.");
    }
    public static BaseComponent[] closetAlreadyExists(String map, String name) {
        return prefix().append("A closet already exists in map '").color(ERROR)
                .append(map).color(SPECIAL)
                .append("' called '").color(ERROR)
                .append(name).color(SPECIAL)
                .append("'.").color(ERROR)
                .create();
    }
    public static BaseComponent[] closetNotExist(String map, String name) {
        return prefix().append("No closets exist in map '").color(ERROR)
                .append(map).color(SPECIAL)
                .append("' called '").color(ERROR)
                .append(name).color(SPECIAL)
                .append("'.").color(ERROR)
                .create();
    }
    public static BaseComponent[] noClosetsInMap(String map) {
        return prefix().append("No closets exist in map '").color(ERROR)
                .append(map).color(SPECIAL)
                .append("'.").color(ERROR)
                .create();
    }

    public static BaseComponent[] closetHelp(String label) {
        return new HelpMenuBuilder(label)
                .add("add <map> <name>", "Adds a closet to the specified map by the specified name. You must be standing inside a valid closet facing the door.", "add mansion basement")
                .add("remove <map> <name>", "Removes the closet of the specified name from the speicified map.", "remove mansion basement")
                .add("remove <map>", "Removes all closets from the specified map.", "remove mansion")
                .add("bypass", "Toggles whether you bypass the closet teleportation.", "bypass")
                .create();
    }

    private static class HelpMenuBuilder {
        private final ComponentBuilder helpMenu;
        private final String label;

        private HelpMenuBuilder(String label) {
            this.label = label;
            helpMenu = prefix().append("====================\n").color(RESULT);
        }

        public HelpMenuBuilder add(String argument, String description, String... examples) {
            if(examples.length == 0) examples = new String[]{argument};
            BaseComponent[] argumentExample = new ComponentBuilder(Arrays.stream(examples).map(e -> "/" + label + " " + e).collect(Collectors.joining("\n"))).color(SPECIAL).create();

            helpMenu.append("/" + label + " " + argument).color(PREFIX)
                    .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(argumentExample)))
                    .append(" - " + description + "\n").color(RESULT);

            return this;
        }
        public BaseComponent[] create() {
            add("help", "Shows this menu.");
            return helpMenu.append("====================").reset().color(RESULT).create();
        }

    }
}
