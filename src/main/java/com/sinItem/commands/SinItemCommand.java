package com.sinItem.commands;

import com.sinItem.SinItemPlugin;
import com.sinItem.util.FileUtils;
import com.sinItem.util.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SinItemCommand implements CommandExecutor, TabCompleter {

    private final SinItemPlugin plugin;

    public SinItemCommand(SinItemPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(FileUtils.getMsg("usage-main"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "reload":
                plugin.reload();
                sender.sendMessage(FileUtils.getMsg("reloaded"));
                break;

            case "get":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(FileUtils.getMsg("player-only"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(FileUtils.getMsg("usage-get"));
                    return true;
                }
                String itemName = args[1];
                int amount = Integer.parseInt(args[2]);
                ItemStack item = ItemUtils.loadItem(itemName);
                if (item == null) {
                    sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", itemName));
                    return true;
                }
                item.setAmount(amount);
                ((Player) sender).getInventory().addItem(item);
                sender.sendMessage(FileUtils.getMsg("got-item")
                        .replace("%item%", itemName).replace("%amount%", "" + amount));
                break;

            case "give":
                if (args.length < 4) {
                    sender.sendMessage(FileUtils.getMsg("usage-give"));
                    return true;
                }
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(FileUtils.getMsg("player-not-found"));
                    return true;
                }
                itemName = args[2];
                amount = Integer.parseInt(args[3]);
                item = ItemUtils.loadItem(itemName);
                if (item == null) {
                    sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", itemName));
                    return true;
                }
                item.setAmount(amount);
                target.getInventory().addItem(item);
                sender.sendMessage(FileUtils.getMsg("gave-item")
                        .replace("%player%", target.getName())
                        .replace("%item%", itemName)
                        .replace("%amount%", "" + amount));
                break;

            case "drop":
                if (args.length < 3) {
                    sender.sendMessage(FileUtils.getMsg("usage-drop-p"));
                    sender.sendMessage(FileUtils.getMsg("usage-drop-w"));
                    return true;
                }

                String option = args[1];
                String itemNameDrop;
                int amountDrop;
                ItemStack dropItem;

                if (option.equalsIgnoreCase("-p")) {
                    if (args.length < 5) {
                        sender.sendMessage(FileUtils.getMsg("usage-drop-p"));
                        return true;
                    }
                    Player targetDrop = Bukkit.getPlayer(args[2]);
                    if (targetDrop == null) {
                        sender.sendMessage(FileUtils.getMsg("player-not-found"));
                        return true;
                    }
                    itemNameDrop = args[3];
                    amountDrop = Integer.parseInt(args[4]);
                    dropItem = ItemUtils.loadItem(itemNameDrop);
                    if (dropItem == null) {
                        sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", itemNameDrop));
                        return true;
                    }
                    dropItem.setAmount(amountDrop);
                    targetDrop.getWorld().dropItemNaturally(targetDrop.getLocation(), dropItem);
                    sender.sendMessage(FileUtils.getMsg("dropped-item")
                            .replace("%item%", itemNameDrop)
                            .replace("%amount%", "" + amountDrop));

                } else if (option.equalsIgnoreCase("-w")) {
                    if (args.length < 8) {
                        sender.sendMessage(FileUtils.getMsg("usage-drop-w"));
                        return true;
                    }
                    String worldName = args[2];
                    double x = Double.parseDouble(args[3]);
                    double y = Double.parseDouble(args[4]);
                    double z = Double.parseDouble(args[5]);
                    itemNameDrop = args[6];
                    amountDrop = Integer.parseInt(args[7]);

                    dropItem = ItemUtils.loadItem(itemNameDrop);
                    if (dropItem == null) {
                        sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", itemNameDrop));
                        return true;
                    }
                    dropItem.setAmount(amountDrop);

                    if (Bukkit.getWorld(worldName) == null) {
                        sender.sendMessage(FileUtils.getMsg("world-not-found"));
                        return true;
                    }
                    Location loc = new Location(Bukkit.getWorld(worldName), x, y, z);
                    Bukkit.getWorld(worldName).dropItemNaturally(loc, dropItem);
                    sender.sendMessage(FileUtils.getMsg("dropped-item")
                            .replace("%item%", itemNameDrop)
                            .replace("%amount%", "" + amountDrop));
                } else {
                    sender.sendMessage(FileUtils.getMsg("unknown-subcommand"));
                }
                break;

            case "save":
                if (!(sender instanceof Player)) {
                    sender.sendMessage(FileUtils.getMsg("player-only"));
                    return true;
                }
                if (args.length < 3) {
                    sender.sendMessage(FileUtils.getMsg("usage-save"));
                    return true;
                }
                String fileName = args[1];
                String saveItemName = args[2];
                ItemStack inHand = ((Player) sender).getInventory().getItemInMainHand();
                if (inHand == null || inHand.getType().isAir()) {
                    sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", saveItemName));
                    return true;
                }
                ItemUtils.saveItem(fileName, saveItemName, inHand);
                sender.sendMessage(FileUtils.getMsg("item-saved")
                        .replace("%item%", saveItemName)
                        .replace("%file%", fileName.endsWith(".yml") ? fileName : fileName + ".yml"));
                break;

            case "delete":
                if (args.length < 2) {
                    sender.sendMessage(FileUtils.getMsg("usage-delete"));
                    return true;
                }
                String deleteItemName = args[1];
                boolean deleted = ItemUtils.deleteItem(deleteItemName);
                if (deleted) {
                    sender.sendMessage(FileUtils.getMsg("deleted-item")
                            .replace("%item%", deleteItemName));
                } else {
                    sender.sendMessage(FileUtils.getMsg("item-not-found").replace("%item%", deleteItemName));
                }
                break;

            default:
                sender.sendMessage(FileUtils.getMsg("unknown-subcommand"));
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("get", "give", "drop", "save", "delete", "reload"));
        } else {
            switch (args[0].toLowerCase()) {

                // -------------------- GET --------------------
                case "get":
                    if (args.length == 2) {
                        completions.addAll(ItemUtils.getAllItemNames()); // <item>
                    } else if (args.length == 3) {
                        completions.add("<amount>");
                    }
                    break;

                // -------------------- GIVE --------------------
                case "give":
                    if (args.length == 2) {
                        for (Player online : Bukkit.getOnlinePlayers()) {
                            completions.add(online.getName()); // <player>
                        }
                    } else if (args.length == 3) {
                        completions.addAll(ItemUtils.getAllItemNames()); // <item>
                    } else if (args.length == 4) {
                        completions.add("<amount>");
                    }
                    break;

                // -------------------- DROP --------------------
                case "drop":
                    if (args.length == 2) {
                        completions.addAll(Arrays.asList("-p", "-w")); // เลือก mode
                    } else if (args[1].equalsIgnoreCase("-p")) {
                        if (args.length == 3) {
                            for (Player online : Bukkit.getOnlinePlayers()) {
                                completions.add(online.getName()); // <player>
                            }
                        } else if (args.length == 4) {
                            completions.addAll(ItemUtils.getAllItemNames()); // <item>
                        } else if (args.length == 5) {
                            completions.add("<amount>");
                        }
                    } else if (args[1].equalsIgnoreCase("-w")) {
                        if (args.length == 3) {
                            Bukkit.getWorlds().forEach(world -> completions.add(world.getName())); // <world>
                        } else if (args.length == 4) {
                            completions.add("<x>");
                        } else if (args.length == 5) {
                            completions.add("<y>");
                        } else if (args.length == 6) {
                            completions.add("<z>");
                        } else if (args.length == 7) {
                            completions.addAll(ItemUtils.getAllItemNames()); // <item>
                        } else if (args.length == 8) {
                            completions.add("<amount>");
                        }
                    }
                    break;

                // -------------------- SAVE --------------------
                case "save":
                    if (args.length == 2) {
                        completions.add("<fileName>");
                    } else if (args.length == 3) {
                        completions.add("<itemName>");
                    }
                    break;

                // -------------------- DELETE --------------------
                case "delete":
                    if (args.length == 2) {
                        completions.addAll(ItemUtils.getAllItemNames()); // <itemName>
                    }
                    break;

                // -------------------- RELOAD --------------------
                case "reload":
                    // ไม่มี arg เพิ่มเติม
                    break;
            }
        }

        return completions;
    }
}
