package com.minecraftserver.warn.commands;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import ru.tehkode.permissions.PermissionUser;
import ru.tehkode.permissions.bukkit.PermissionsEx;

import com.minecraftserver.warn.Punisher;
import com.minecraftserver.warn.SLAPI;
import com.minecraftserver.warn.Warner;

public class Warnsetcmd extends WarnCommandHandler {

    static int            warn_amount;
    static List<String[]> warnings_player = new Vector<String[]>();
    static String         reason          = "You have been warned!";

    private static String getTimeNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - hh:mm a");
        return dateFormat.format(new Date());
    }

    public static boolean run(CommandSender sender, String[] args, Warner warner) {
        YamlConfiguration config = warner.getConfig();
        SLAPI slapi = warner.getSLAPI();
        
        String target;
        PermissionUser user = PermissionsEx.getUser(args[1]);
        Player targetOnline = (Bukkit.getServer().getPlayer(args[1]));
        if (targetOnline == null) {
            OfflinePlayer targetOffline = (Bukkit.getServer().getOfflinePlayer(args[1]));
            target = targetOffline.getName();
        } else {
            String targetOnlineStr = (Bukkit.getServer().getPlayer(args[1]).getName());
            target = targetOnlineStr;
        }

        if (!sender.hasPermission("warner.admin.warn.set")) {
            sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
            return false;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
            sender.sendMessage(ChatColor.RED + "/warn set <user> <amount>");
            return false;
        }

        if (target != null) {
            if (user.has("warner.admin.warn.exempt")) {
                sender.sendMessage(ChatColor.RED + "You can't warn this person!");
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + args[1] + " was not found online!");
            return false;
        }

        try {
            warn_amount = Integer.parseInt(args[2]);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + ("No value is specified!"));
            return false;
        }
        // Load player warnings
        warnings_player = slapi.loadPlayerWarnings(target, sender);
        if (warnings_player != null) {
            // if player got more warnings then arg
            while (warnings_player.size() > warn_amount) {
                // always removes the oldest warning
                warnings_player.remove(0);
            }
            while (warnings_player.size() < warn_amount) {
                // add standard warning
                String[] warning = { sender.getName(), reason, getTimeNow(),
                        System.currentTimeMillis() + "" };
                warnings_player.add(warning);
            }
            // save warnings to file
            if (!slapi.savePlayerWarnings(warnings_player, target)) {
                sender.sendMessage(ChatColor.RED + "Error writing Playerfile!");
                return false;
            }
        } else {
            sender.sendMessage(ChatColor.RED + "Error reading Playerfile!");
            return false;
        }
        if (targetOnline != null){
            targetOnline.sendMessage(ChatColor.GOLD + sender.getName()
                    + ChatColor.BLUE + " has set your warnings to:"
                    + ChatColor.GOLD + " [" + warnings_player.size() + "]");
        }
        sender.sendMessage(ChatColor.BLUE + "Set the warnings of "
                + ChatColor.GOLD + target + ChatColor.BLUE + " to:"
                + ChatColor.GOLD + " [" + warnings_player.size() + "]");
        
        Punisher.punish(target, warn_amount, reason, sender, config);
        return true;

    }

}
