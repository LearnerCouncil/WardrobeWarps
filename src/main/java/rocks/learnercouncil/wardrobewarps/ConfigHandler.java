package rocks.learnercouncil.wardrobewarps;

import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class ConfigHandler {
    private static final WardrobeWarps plugin = WardrobeWarps.getPlugin();

    public static <T> T validate(Class<T> type, Object object) {
        if(!(type.isInstance(object)))
            throw new ClassCastException("Configuation object '" + object + "' was not the expected type of '" + type + "'.");
        return type.cast(object);
    }

    public static Set<Block> deserializeBlockSet(List<String> serializedBlocks, @NotNull World world) {
        return serializedBlocks.stream().map(s -> {
            String[] parts = s.split(",", 3);
            try {
                int x = Integer.parseInt(parts[0]);
                int y = Integer.parseInt(parts[1]);
                int z = Integer.parseInt(parts[2]);
                return world.getBlockAt(x, y, z);
            } catch (NumberFormatException | NullPointerException e) {
                plugin.getLogger().severe("Serialized block '" + s + "' failed to deserialize.");
                return null;
            }
        }).filter(Objects::nonNull).collect(Collectors.toSet());
    }

}
