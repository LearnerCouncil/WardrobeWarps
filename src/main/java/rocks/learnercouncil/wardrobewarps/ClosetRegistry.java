package rocks.learnercouncil.wardrobewarps;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;

import java.util.*;
import java.util.stream.Collectors;

public class ClosetRegistry {

    private static WardrobeWarps plugin = WardrobeWarps.getPlugin();

    private static final Map<String, Set<Closet>> closets = new HashMap<>();
    public static Set<String> getMaps() {
        return closets.keySet();
    }
    public static Set<Closet> getClosets(String map) {
        return closets.getOrDefault(map, Collections.emptySet());
    }
    public static Set<Closet> getAllClosets() {
        return closets.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

    public static boolean add(Closet closet) {
        if(!closets.containsKey(closet.getMap())) closets.put(closet.getMap(), new HashSet<>());
        return closets.get(closet.getMap()).add(closet);
    }

    public static Set<Closet> get(String map) {
        return closets.get(map);
    }

    public static boolean remove(String map, String name) {
        Optional<Closet> closet = closets.get(map).stream().filter(c -> c.getName().equals(name)).findAny();
        if(closet.isPresent()) {
            closets.get(map).remove(closet.get());
            return true;
        }
        return false;
    }
    public static boolean removeAll(String map) {
        return closets.remove(map) != null;
    }

    public static void save(FileConfiguration config) {
        config.set("closets", null);
        getAllClosets().forEach(c -> c.save(config));
    }

    public static void load(FileConfiguration config) {
        System.out.println(config.getKeys(false));
        ConfigurationSection closetsSection = config.getConfigurationSection("closets");
        if(closetsSection == null) {
            plugin.getLogger().warning("Failed to find section 'closets' in config. Closets will not be loaded.");
            return;
        }
        closetsSection.getKeys(false).forEach(map -> loadMap(map, closetsSection));
    }
    private static void loadMap(String map, ConfigurationSection closetsSection) {
        ConfigurationSection mapSection = closetsSection.getConfigurationSection(map);
        if(mapSection == null) {
            WardrobeWarps.getPlugin().getLogger().warning("Map '" + map + "' has a section but is still null.");
            return;
        }
        mapSection.getKeys(false).forEach(name -> loadCloset(map, name, mapSection));
    }
    private static void loadCloset(String map, String name, ConfigurationSection mapSection) {
        Location location = ConfigHandler.validate(Location.class, mapSection.get(name + ".location"));
        if(location.getWorld() == null) {
            plugin.getLogger().severe("World for closet '" + map + ":" + name + "' is null, cannot deserialize.");
            return;
        }
        BoundingBox detectionBox = ConfigHandler.validate(BoundingBox.class, mapSection.get(name + ".detectionBox"));
        Set<Block> doors = ConfigHandler.deserializeBlockSet(mapSection.getStringList(name + ".doors"), location.getWorld());

        add(new Closet(name, map, location, detectionBox, doors));
    }

}
