package com.minecraftserver.warn;

import org.bukkit.entity.Player;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class Punisher {

	int Warnings;
	static Command kick = Bukkit.getPluginCommand("kick");
	static Command ban = Bukkit.getPluginCommand("ban");
	static Command tempban = Bukkit.getPluginCommand("tempban");
	static Command jail = Bukkit.getPluginCommand("jail");
	static Command mute = Bukkit.getPluginCommand("mute");
	static CommandSender Console = (Bukkit.getServer().getConsoleSender());

	// private static IEssentials essentials;
	// static SLAPI SLAPI = new SLAPI();

	public static boolean punish(String target, int warnings, String reason, CommandSender sender, YamlConfiguration config) {
	    
	    Player targetOnline = (Bukkit.getServer().getPlayer(target));
        if (targetOnline == null) {
            OfflinePlayer targetOffline = (Bukkit.getServer().getOfflinePlayer(target));
            target = targetOffline.getName();
        } else {
            String targetOnlineStr = (Bukkit.getServer().getPlayer(target).getName());
            target = targetOnlineStr;
        }
	    
		if (reason == null) {
			reason = "You have been warned!";
		}
		
		String command = config.getString("warning." + warnings + ".command");
		if (command != null)
//			if (command.equalsIgnoreCase("mute")){
//				if (reason.toLowerCase().contains("spam")){
//					if (!sender.hasPermission("warner.punish.mute")){
//						return false;
//					}
//					String duration = config.getString("warning." + warnings + ".duration");
//					if (duration == null)
//						duration = "30 min";
//					String[] mute_arg = {playername, duration};
//					mute.execute(Console, "mute", mute_arg);
//					sender.sendMessage(target.getName() + " has been muted for" + duration);
//				}
//			}
			if (command.equalsIgnoreCase("jail")){
				if (!sender.hasPermission("warner.punish.jail")){
					return false;
				}
				String duration = config.getString("warning." + warnings + ".duration");
				if (duration == null){
					duration = "30 min";
				}
				String jail_number = config.getString("warning." + warnings + ".jail_number");
				if (jail_number == null){
					sender.sendMessage(ChatColor.RED + "No jail has been specified in config file.");
				}
				String[] jail_arg = {target, jail_number, duration};
				jail.execute(Console, "jail", jail_arg);
				return true;
			} else if (command.equalsIgnoreCase("kick") && (targetOnline != null)) {
				if (!sender.hasPermission("warner.punish.kick")){
					return false;
				}
				String[] kick_arg = { target, "You have been warned by " + sender.getName() + " for: " + reason + ". [" + warnings + "]" };
				kick.execute(Console, "kick", kick_arg);
				return true;
			} else if (command.equalsIgnoreCase("tempban")) {
				if (!sender.hasPermission("warner.punish.tempban")){
					return false;
				}
				String duration = config.getString("warning." + warnings + ".duration");
				if (duration == null)
					duration = "3 Day";
				String[] tempban_arg = { target, duration };
				tempban.execute(Console, "tempban", tempban_arg);
				return true;
			} else if (command.equalsIgnoreCase("ban")) {
				if (!sender.hasPermission("warner.punish.ban")){
					return false;
				}
				String[] ban_arg = { target, "You have been warned by " + sender.getName() + " for: " + reason + ". [" + warnings + "]" };
				ban.execute(Console, "ban", ban_arg);
				return true;
			}
		return false;
	}
}