package rocks.learnercouncil.wardrobewarps.commands.closet.arguments;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import rocks.learnercouncil.wardrobewarps.Closet;
import rocks.learnercouncil.wardrobewarps.ClosetRegistry;
import rocks.learnercouncil.wardrobewarps.commands.CommandArgument;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static rocks.learnercouncil.wardrobewarps.commands.CommandResult.*;

public class AddArg implements CommandArgument {

    @Override
    public BaseComponent[] execute(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!args[0].equalsIgnoreCase("add")) return NONE;
        if(args.length == 1) return MUST_SPECIFY_NEW_CLOSET_NAME;
        if(args.length == 2) return MUST_SPECIFY_NEW_MAP;
        if(args.length > 3) return TOO_MANY_ARGS;

        Player player = (Player) sender;

        String map = args[1].toLowerCase();
        String name = args[2].toLowerCase();


        Location playerLocation = roundLocation(player.getLocation());

        Location doorLocation = player.getLocation().add(player.getFacing().getDirection());
        if(!(doorLocation.getBlock().getBlockData() instanceof Door)) return MUST_BE_FACING_DOOR;

        BoundingBox detectionBox = getClosetInterior(player.getLocation().getBlock().getLocation());
        Set<Block> doors = getDoors(detectionBox, player.getWorld());

        Closet closet = new Closet(name, map, playerLocation, detectionBox, doors);
        closet.debugDisplay();
        return ClosetRegistry.add(closet) ? closetCreated(map, name) : closetAlreadyExists(map, name);
    }

    private Location roundLocation(Location location) {
        double roundedX = Math.round(location.getX() * 2) / 2f;
        double roundedZ = Math.round(location.getZ() * 2) / 2f;
        float roundedYaw = Math.round(location.getYaw() / 90) * 90f;
        float roundedPitch = Math.round(location.getPitch() / 90) * 90f;
        return new Location(location.getWorld(), roundedX, location.getY(), roundedZ, roundedYaw, roundedPitch);
    }
    private BoundingBox getClosetInterior(Location location) {

        final Vector[] AXES = { BlockFace.EAST.getDirection(), BlockFace.UP.getDirection(), BlockFace.SOUTH.getDirection() };
        final int SIZE_THRESHOLD = 10;
        Location min = location.clone();
        for(Vector axis : AXES) {
            for(int i = 0; i <= SIZE_THRESHOLD; i++) {
                if(min.getBlock().getType().isSolid()) break;
                min.subtract(axis);
            }
            min.add(axis);
        }

        Location max = location.clone();
        for(Vector axis : AXES) {
            for(int i = 0; i <= SIZE_THRESHOLD; i++) {
                if(max.getBlock().getType().isSolid()) break;
                max.add(axis);
            }
            max.subtract(axis);
        }

        return BoundingBox.of(min, max.add(1, 1, 1));
    }
    private Set<Block> getDoors(BoundingBox box, World world) {

        Set<Block> doors = new HashSet<>();
        int minY = box.getMin().getBlockY(), maxY = box.getMax().getBlockY() - 1;
        int minX = box.getMin().getBlockX(), maxX = box.getMax().getBlockX() - 1;
        int minZ = box.getMin().getBlockZ(), maxZ = box.getMax().getBlockZ() - 1;

        for(int y = minY; y <= maxY; y++) {

            for(int x = minX; x <= maxX; x++) {
                Block northBlock = world.getBlockAt(x, y, minZ - 1);
                if(northBlock.getBlockData() instanceof Door) doors.add(northBlock);

                Block southBlock = world.getBlockAt(x, y, maxZ + 1);
                if(southBlock.getBlockData() instanceof Door) doors.add(southBlock);
                var northDisplay = (ItemDisplay) world.spawnEntity(northBlock.getLocation().add(.5, .5 ,.5), EntityType.ITEM_DISPLAY);
                northDisplay.setItemStack(new ItemStack(Material.LIME_STAINED_GLASS_PANE));
                northDisplay.setGlowing(true);
                var southDisplay = (ItemDisplay) world.spawnEntity(southBlock.getLocation().add(.5, .5 ,.5), EntityType.ITEM_DISPLAY);
                southDisplay.setItemStack(new ItemStack(Material.LIME_STAINED_GLASS_PANE));
                southDisplay.setGlowing(true);
            }

            for(int z = minZ; z <= maxZ; z++) {
                Block westBlock = world.getBlockAt(minX - 1, y, z);
                if(westBlock.getBlockData() instanceof Door) doors.add(westBlock);

                Block eastBlock = world.getBlockAt(maxX + 1, y, z);
                if(eastBlock.getBlockData() instanceof Door) doors.add(eastBlock);
                var northDisplay = (ItemDisplay) world.spawnEntity(westBlock.getLocation().add(.5, .5 ,.5), EntityType.ITEM_DISPLAY);
                northDisplay.setItemStack(new ItemStack(Material.LIME_STAINED_GLASS_PANE));
                northDisplay.setGlowing(true);
                var southDisplay = (ItemDisplay) world.spawnEntity(eastBlock.getLocation().add(.5, .5 ,.5), EntityType.ITEM_DISPLAY);
                southDisplay.setItemStack(new ItemStack(Material.LIME_STAINED_GLASS_PANE));
                southDisplay.setGlowing(true);
            }
        }
        return doors;
    }



    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(args.length == 1) return Collections.singletonList("add");
        if(args.length == 3 && args[2].equalsIgnoreCase("add")) return ClosetRegistry.getMaps().stream().toList();
        return Collections.emptyList();
    }
}
