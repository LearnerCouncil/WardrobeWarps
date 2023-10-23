package rocks.learnercouncil.wardrobewarps;

import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BoundingBox;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Closet {

    private static final WardrobeWarps plugin = WardrobeWarps.getPlugin();

    private static final List<Player> bypassingPlayers = new ArrayList<>();
    public static boolean toggleBypassing(Player player) {
        if(bypassingPlayers.contains(player)) {
            bypassingPlayers.remove(player);
            return true;
        }
        bypassingPlayers.add(player);
        return false;
    }

    private static Stream<Closet> getActive(String map) {
        return ClosetRegistry.get(map).stream()
                .filter(c -> !c.isOnCooldown());
    }

    private static final Duration COOLDOWN_DURATION = Duration.ofSeconds(10);

    private final @Getter String name;
    private final @Getter String map;
    private final Location location;
    private final BoundingBox detectionBox;
    private final Set<Block> doors;

    private Instant cooldownTimestamp = Instant.EPOCH;
    public boolean isOnCooldown() {
        Duration currentCooldown = Duration.between(cooldownTimestamp, Instant.now());
        return currentCooldown.compareTo(COOLDOWN_DURATION) < 0;
    }
    public void setCooldown() {
        cooldownTimestamp = Instant.now();
    }

    public Closet(String name, String map, Location location, BoundingBox detectionBox, Set<Block> doors) {
        this.name = name;
        this.map = map;
        this.location = location;
        this.detectionBox = detectionBox;
        this.doors = doors;
    }

    public void teleportPlayers() {
        Random random = new Random();
        Closet target = getActive(this.map)
                .filter(c -> !c.equals(this))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    System.out.println("Active closets: " + list.stream().map(c -> c.map + ":" + c.name).collect(Collectors.toList()));
                    Closet selected = list.isEmpty() ? null : list.get(random.nextInt(list.size()));
                    System.out.println("Selected: " + (selected == null ? "null" : selected.getName()));
                    return selected;
                }));
        if(target == null) return;

        AtomicBoolean resetCooldowns = new AtomicBoolean(false);
        Optional.ofNullable(location.getWorld())
                .map(world -> {
                    var entities = world.getNearbyEntities(detectionBox, e -> e.getType() == EntityType.PLAYER);
                    System.out.println("[" + name + "] NearbyPlayers: " + entities + ", box: " + detectionBox);
                    return entities;
                })
                .orElse(Collections.emptySet()).stream()
                .filter(p -> !bypassingPlayers.contains((Player) p))
                .forEach(p -> {
                    System.out.println("Teleporting: " + p);
                    p.teleport(target.location);
                    resetCooldowns.set(true);
                });
        if(!resetCooldowns.get()) return;

        this.closeDoors();
        this.setCooldown();
        target.closeDoors();
        target.setCooldown();
    }
    private void closeDoors() {
        doors.removeIf(d -> !(d.getBlockData() instanceof Door));
        doors.forEach(door -> {
            Door doorData = (Door) door.getBlockData();
            doorData.setOpen(false);
            door.setBlockData(doorData);
        });
    }

    public boolean allDoorsAreClosed() {
        doors.removeIf(d -> !(d.getBlockData() instanceof Door));
        return doors.stream().noneMatch(door -> ((Door) door.getBlockData()).isOpen());
    }

    public void debugDisplay() {

        World world = location.getWorld();
        assert world != null;
        ArmorStand corner1 = (ArmorStand) world.spawnEntity(detectionBox.getMin().toLocation(world), EntityType.ARMOR_STAND);
        corner1.setGlowing(true);
        corner1.setCustomNameVisible(true);
        corner1.setCustomName(name + ": c1");
        corner1.setSmall(true);
        ArmorStand corner2 = (ArmorStand)  world.spawnEntity(detectionBox.getMax().toLocation(world), EntityType.ARMOR_STAND);
        corner2.setGlowing(true);
        corner2.setCustomNameVisible(true);
        corner2.setCustomName(name + ": c2");
        corner2.setSmall(true);

        for(Block door : doors) {
            var doorSlime = (Slime) world.spawnEntity(door.getLocation().add(0.5, 0.5, 0.5), EntityType.SLIME);
            doorSlime.setAI(false);
            doorSlime.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, PotionEffect.INFINITE_DURATION,0));
            doorSlime.setGlowing(true);
            doorSlime.setGravity(false);
            doorSlime.setSize(0);
        }

    }

    public void save(FileConfiguration config) {
        config.set("closets." + map + "." + this.name + ".location", this.location);
        config.set("closets." + map + "." + this.name + ".detectionBox", this.detectionBox);
        config.set("closets." + map + "." + this.name + ".doors", this.doors.stream()
                .map(block -> block.getX() + "," + block.getY() + "," + block.getZ())
                .collect(Collectors.toList()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Closet closet = (Closet) o;
        return name.equals(closet.name) && map.equals(closet.map) && location.equals(closet.location) && detectionBox.equals(closet.detectionBox);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, map, location, detectionBox);
    }

    public static class Events implements Listener {

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
            if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
            ClosetRegistry.getAllClosets().stream()
                    .filter(c -> c.doors.contains(event.getClickedBlock()) && !c.isOnCooldown()).findAny()
                    .ifPresent(closet -> new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (closet.allDoorsAreClosed()) {
                                closet.teleportPlayers();
                            }
                        }
                    }.runTaskLater(plugin, 1));
        }
    }
}
