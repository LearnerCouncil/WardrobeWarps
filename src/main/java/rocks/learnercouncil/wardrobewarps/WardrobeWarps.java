package rocks.learnercouncil.wardrobewarps;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import rocks.learnercouncil.wardrobewarps.commands.closet.ClosetCommand;
import rocks.learnercouncil.wardrobewarps.commands.passage.PassageCommand;

public final class WardrobeWarps extends JavaPlugin {

    private static @Getter
    WardrobeWarps plugin;

    @Override
    public void onEnable() {
        // Plugin startup logic
        plugin = this;
        saveDefaultConfig();
        ClosetRegistry.load(getConfig());
        new ClosetCommand().register(this);
        new PassageCommand().register(this);

        getServer().getPluginManager().registerEvents(new Closet.Events(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        ClosetRegistry.save(getConfig());
        saveConfig();
    }
}
