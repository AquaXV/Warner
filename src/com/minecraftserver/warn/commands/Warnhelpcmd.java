package com.minecraftserver.warn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Warnhelpcmd {

    public static boolean run(CommandSender sender) {

        if (!sender.hasPermission("warner.user.help")) {
            return false;
        }

        sender.sendMessage(ChatColor.GOLD + "Available commands (<required> [optional]):");
        if (sender.hasPermission("warner.warn.give")) {
            sender.sendMessage(ChatColor.GREEN + "/warn <user> [reason] - Give <user> a warning.");
        }
        if (sender.hasPermission("warner.user.lookup.self")) {
            sender.sendMessage(ChatColor.GREEN + "/warn lookup - See your warnings.");
        }
        if (sender.hasPermission("warner.user.lookup.other")) {
            sender.sendMessage(ChatColor.GREEN
                    + "/warn lookup <user> - See the warnings <user> has.");
        }
        if (sender.hasPermission("warner.warn.take")) {
            sender.sendMessage(ChatColor.GREEN
                    + "/warn take <user> <warning> - Remove a warning of <user>.");
        }
        if (sender.hasPermission("warner.admin.warn.set")) {
            sender.sendMessage(ChatColor.GREEN
                    + "/warn set <user> <amount> - set warnings of <user> to <amount>.");
        }
        if (sender.hasPermission("warner.admin.warn.reset")) {
            sender.sendMessage(ChatColor.GREEN
                    + "/warn reset <user> - Completely removes warnings from <user>.");
        }
        if (sender.hasPermission("warner.other.version")) {
            sender.sendMessage(ChatColor.GREEN + "/warn version - See the plugin version.");
        }
        if (sender.hasPermission("warner.other.reload")) {
            sender.sendMessage(ChatColor.GREEN + "/warn reload - reload the plugin");
        }

        return true;

    }

}
