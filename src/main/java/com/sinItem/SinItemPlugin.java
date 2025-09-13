package com.sinItem;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class SinItemPlugin extends JavaPlugin {

    private static SinItemPlugin instance;

    @Override
    public void onEnable() {
        instance = this;

        // save default config and messages
        saveDefaultConfig();
        saveResource("messages.yml", false);

        // create items folder if not exists
        File itemsFolder = new File(getDataFolder(), "items");
        if (!itemsFolder.exists()) itemsFolder.mkdirs();

        // register command
        getCommand("sinitem").setExecutor(new com.sinItem.commands.SinItemCommand(this));

        getLogger().info("SinItem enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("SinItem disabled!");
    }

    public static SinItemPlugin getInstance() {
        return instance;
    }

    public void reload() {
        reloadConfig();
        // reload messages.yml
        com.sinItem.util.FileUtils.reloadMessages();
    }
}
