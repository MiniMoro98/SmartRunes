package it.moro.smartRunes;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Objects;

public final class SmartRunes extends JavaPlugin {

    @Getter
    private static SmartRunes instance;

    @Override
    public void onEnable() {
        loadFiles();
        instance = this;
        Events events = new Events(this);
        getServer().getPluginManager().registerEvents(events, this);
        new Runes(this);
        Objects.requireNonNull(getCommand("runes")).setExecutor(new Commands(this));
        getLogger().info("\u001B[32mEnabled!\u001B[0m");
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabled!");
    }

    private void loadFiles() {
        if (!getDataFolder().exists() && !getDataFolder().mkdir()) {
            getLogger().info("\u001B[91mUnable to create plugin data folder! Missing permits?\u001B[0m");
            return;
        }
        File fileConfigs = new File(getDataFolder(), "config.yml");
        if (!fileConfigs.exists()) {
            try {
                fileConfigs.getParentFile().mkdirs();
                saveResource("config.yml", false);
                getLogger().info("\u001B[32mFile config.yml created!\u001B[0m");
            } catch (Exception e) {
                getLogger().severe("§cError creating config.yml");
                e.fillInStackTrace();
            }
        }
        File fileRunes = new File(getDataFolder(), "runes.yml");
        if (!fileRunes.exists()) {
            try {
                fileRunes.getParentFile().mkdirs();
                saveResource("runes.yml", false);
                getLogger().info("\u001B[32mFile runes.yml created!\u001B[0m");
            } catch (Exception e) {
                getLogger().severe("§cError creating runes.yml");
                e.fillInStackTrace();
            }
        }
    }


}

