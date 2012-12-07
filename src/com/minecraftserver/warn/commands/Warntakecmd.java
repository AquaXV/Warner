package com.minecraftserver.warn.commands;

import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecraftserver.warn.SLAPI;

public class Warntakecmd extends WarnCommandHandler {
	
	static List<String[]> warnings_player = new Vector<String[]>();
	static int warn_amount;
    static SLAPI slapi;
	
	public static boolean run(CommandSender sender, String[] args) {
		
		Player target = (Bukkit.getServer().getPlayer(args[1]));
		
		if (!sender.hasPermission("warner.warn.take")) {
			sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
			return false;
		}
		
		if (args.length < 3) {
			sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
			sender.sendMessage(ChatColor.RED + "/warn take <user> <warning>");
			return false;
		}
		
		if (target == null){
			sender.sendMessage(ChatColor.GOLD + args[1] + ChatColor.RED +" was not found online.");
		}
		
		// Load player warnings
		warnings_player = slapi.loadPlayerWarnings(target);
		if (warnings_player != null) {
			warn_amount = warnings_player.size();
			if (warn_amount == 0) {
				sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has no warnings, can't take them.");
				return false;
			}
			
			// removes the oldest warning
			if (args[2] != null) {
				try {
					int number=Integer.parseInt(args[2]);
				    warnings_player.remove(number - 1);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.BLUE + "There is no warning number " + ChatColor.GOLD + args[2] + ChatColor.BLUE + ".");
					return false;
				}
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
		
		target.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + " has removed one of your warnings! " + ChatColor.GOLD + "[" + warnings_player.size() + "]");
		Player[] playerList = Bukkit.getServer().getOnlinePlayers();
		for (Player player : playerList) {
			if (player.hasPermission("warner.other.notify")) {
				player.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has had a warning removed by " + ChatColor.GOLD + sender.getName() + ChatColor.BLUE + "." + ChatColor.GOLD + " [" + warnings_player.size() + "]");
			}
		}
		
		return true;
	}

}
