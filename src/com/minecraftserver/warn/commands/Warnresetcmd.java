package com.minecraftserver.warn.commands;

import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.minecraftserver.warn.SLAPI;
import com.minecraftserver.warn.Warner;

public class Warnresetcmd {

    static List<String[]> warnings_player = new Vector<String[]>();

    public static boolean run(CommandSender sender, String[] args, Warner warner) {
        SLAPI slapi = warner.getSLAPI();

        Player target = (Bukkit.getServer().getPlayer(args[1]));

        if (!sender.hasPermission("warner.admin.warn.reset")) {
            sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
            return false;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
            sender.sendMessage(ChatColor.RED + "/warn reset <user>");
            return false;
        }

        // Load player warnings
        warnings_player = slapi.loadPlayerWarnings(target, sender);
        if (warnings_player != null) {
            if (warnings_player.size() == 0) {
                sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE
                        + " has no warnings\n Can't reset " + ChatColor.GOLD + target.getName()
                        + ChatColor.BLUE + ".");
                return false;
            }
            sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has "
                    + ChatColor.GOLD + warnings_player.size() + ChatColor.BLUE + " warnings.");
            warnings_player = new Vector<String[]>();

            // save warnings to file
            if (!slapi.savePlayerWarnings(warnings_player, target)) {
                sender.sendMessage(ChatColor.RED + "Error writing Playerfile!");
                return false;
            }
            sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE
                    + " has been reset.");
        } else {
            sender.sendMessage(ChatColor.RED + "Error reading Playerfile!");
            return false;
        }

        target.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE
                + " has reset your warnings!");
        return true;
    }

}
