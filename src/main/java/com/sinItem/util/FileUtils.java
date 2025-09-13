package com.sinItem.util;

import com.sinItem.SinItemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;

public class FileUtils {

    private static File messagesFile;
    private static FileConfiguration messages;

    public static void reloadMessages() {
        messagesFile = new File(SinItemPlugin.getInstance().getDataFolder(), "messages.yml");
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static String getMsg(String path) {
        if (messages == null) reloadMessages();
        String prefix = SinItemPlugin.getInstance().getConfig().getString("prefix", "");
        String raw = messages.getString(path, path);
        return ChatColor.translateAlternateColorCodes('&', prefix + raw);
    }
}
