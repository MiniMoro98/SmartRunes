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

public class Commands implements CommandExecutor, TabCompleter {

    private static SmartRunes plugin;

    public Commands(SmartRunes plugin) {
        Commands.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(sender instanceof Player player){
            if(command.getName().equalsIgnoreCase("runes")){
                if(args.length > 0){
                    if(args[0].equalsIgnoreCase("reload")){
                        Runes.loadFile();
                        player.sendMessage("§a[SmartRunes] Configuration Reloaded!");
                        return true;
                    } else if(args[0].equalsIgnoreCase("info")){
                        ItemStack item = player.getInventory().getItemInMainHand();
                        if (!item.hasItemMeta()) {
                            player.sendMessage("You have no object in your hand!");
                            return true;
                        }
                        ItemMeta meta = item.getItemMeta();
                        Map<String, Integer> enchantValues = readRuneEnchantLevels(meta, Runes.runesParameters);

                        player.sendMessage("§e[SmartRunes] §7Rune Values:");
                        enchantValues.forEach((id, value) -> {
                            if(value > 0){
                                player.sendMessage("§6" + id + ": §f" + value);
                            }
                        });
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
            Player player = (Player) sender;
            List<String> actions = new ArrayList<>();
            if (args.length == 1) {
                if (player.hasPermission("smartrunes.reload")) {
                    actions.add("reload");
                }
                actions.add("info");
            }
            return new ArrayList<>(actions.stream().filter(a -> a.toLowerCase().startsWith(args[0].toLowerCase()))
                    .toList());
        }
        return null;
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
