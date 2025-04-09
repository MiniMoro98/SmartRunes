package it.moro.smartRunes;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Commands implements CommandExecutor {

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
                        player.sendMessage("[SmartRunes] §aConfiguration Reloaded!");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
