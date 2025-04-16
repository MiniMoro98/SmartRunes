package it.moro.smartRunes;

import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static it.moro.smartRunes.Runes.*;

public class Commands implements CommandExecutor, TabCompleter {

    private static SmartRunes plugin;

    public Commands(SmartRunes plugin) {
        Commands.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (command.getName().equalsIgnoreCase("runes")) {
                if (args.length > 0) {
                    if (args[0].equalsIgnoreCase("reload")) {
                        if (player.hasPermission("smartrunes.reload")) {
                            Runes.loadFile();
                            player.sendMessage("§a[SmartRunes] Configuration Reloaded!");
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("info")) {
                        if (player.hasPermission("smartrunes.info")) {
                            ItemStack item = player.getInventory().getItemInMainHand();
                            if (!item.hasItemMeta()) {
                                player.sendMessage("You have no object in your hand!");
                                return true;
                            }
                            ItemMeta meta = item.getItemMeta();
                            Map<String, Integer> enchantValues = readRuneEnchantLevels(meta, runesParameters);

                            player.sendMessage("§e[SmartRunes] §7Rune Values:");
                            enchantValues.forEach((id, value) -> {
                                if (value > 0) {
                                    player.sendMessage("§6" + id + ": §f" + value);
                                }
                            });
                        }
                        return true;
                    } else if (args[0].equalsIgnoreCase("give")) {
                        if (player.hasPermission("smartrunes.give")) {
                            int index = 0;
                            for (String[] enchant : runesParameters) {
                                if (args[1].equalsIgnoreCase(enchant[1])) {
                                    int lvl = Integer.parseInt(args[2]);
                                    if (lvl > getInt("Runes." + args[1] + ".effects.max-level")) {
                                        lvl = getInt("Runes." + args[1] + ".effects.max-level");
                                    }
                                    ItemStack runa = rune(enchant[1], 100.0, index, lvl);
                                    player.getInventory().addItem(runa);
                                    return true;
                                }
                                index++;
                            }
                            player.sendMessage("§cUnrecognized rune!!");
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (command.getName().equalsIgnoreCase("runes")) {
            if (!(sender instanceof Player player)) return Collections.emptyList();
            List<String> suggestions = new ArrayList<>();
            if (args.length == 1) {
                if (player.hasPermission("smartrunes.reload")) suggestions.add("reload");
                if (player.hasPermission("smartrunes.give")) suggestions.add("give");
                if (player.hasPermission("smartrunes.info")) suggestions.add("info");
                return suggestions.stream()
                        .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                        .toList();
            }
            if (args.length == 2 && args[0].equalsIgnoreCase("give")) {
                for (String[] enchant : runesParameters) {
                    suggestions.add(enchant[1]);
                }
                return suggestions.stream()
                        .filter(s -> s.toLowerCase().startsWith(args[1].toLowerCase()))
                        .toList();
            }
            if (args.length == 3 && args[0].equalsIgnoreCase("give")) {
                int max = getInt("Runes." + args[1] + ".effects.max-level");
                for (int i = 0; i < max; i++) {
                    suggestions.add(String.valueOf(i + 1));
                }
                return suggestions.stream()
                        .filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
                        .toList();
            }
        }
        return Collections.emptyList();
    }


    public Map<String, Integer> readRuneEnchantLevels(ItemMeta meta, String[][] runesParameters) {
        Map<String, Integer> values = new HashMap<>();
        PersistentDataContainer data = meta.getPersistentDataContainer();
        for (String[] runesParameter : runesParameters) {
            String enchantId = runesParameter[1];
            NamespacedKey key = new NamespacedKey("smartrunes", enchantId.toLowerCase());
            Integer value = data.getOrDefault(key, PersistentDataType.INTEGER, 0);
            values.put(enchantId, value);
        }
        return values;
    }
}
