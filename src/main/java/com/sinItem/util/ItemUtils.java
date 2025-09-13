package com.sinItem.util;

import com.sinItem.SinItemPlugin;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ItemUtils {

    public static void saveItem(String fileName, String itemName, ItemStack item) {
        if (!fileName.endsWith(".yml")) {
            fileName = fileName + ".yml";
        }

        File file = new File(SinItemPlugin.getInstance().getDataFolder(), "items/" + fileName);
        YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);

        // ✅ apply color to display name and lore before save
        ItemStack clone = item.clone();
        ItemMeta meta = clone.getItemMeta();
        if (meta != null) {
            if (meta.hasDisplayName()) {
                meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
            }
            if (meta.hasLore()) {
                List<String> coloredLore = meta.getLore().stream()
                        .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                        .collect(Collectors.toList());
                meta.setLore(coloredLore);
            }
            clone.setItemMeta(meta);
        }

        yaml.set(itemName, clone);

        try {
            yaml.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean deleteItem(String itemName) {
        File folder = new File(SinItemPlugin.getInstance().getDataFolder(), "items");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return false;

        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            if (yaml.contains(itemName)) {
                yaml.set(itemName, null);
                try {
                    yaml.save(file);
                    return true;
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return false;
    }

    public static List<String> getAllItemNames() {
        List<String> names = new ArrayList<>();
        File folder = new File(SinItemPlugin.getInstance().getDataFolder(), "items");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return names;

        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            names.addAll(yaml.getKeys(false));
        }
        return names;
    }

    public static ItemStack loadItem(String itemName) {
        File folder = new File(SinItemPlugin.getInstance().getDataFolder(), "items");
        File[] files = folder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return null;

        for (File file : files) {
            YamlConfiguration yaml = YamlConfiguration.loadConfiguration(file);
            if (yaml.contains(itemName)) {
                ItemStack item = yaml.getItemStack(itemName);

                if (item != null && item.hasItemMeta()) {
                    ItemMeta meta = item.getItemMeta();
                    if (meta != null) {
                        if (meta.hasDisplayName()) {
                            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', meta.getDisplayName()));
                        }
                        if (meta.hasLore()) {
                            List<String> coloredLore = meta.getLore().stream()
                                    .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                                    .collect(Collectors.toList());
                            meta.setLore(coloredLore);
                        }
                        item.setItemMeta(meta);
                    }
                }
                return item;
            }
        }
        return null;
    }
}
