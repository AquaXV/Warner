package com.minecraftserver.warn.commands;

import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.minecraftserver.warn.SLAPI;
import com.minecraftserver.warn.Warner;

public class Warnlookupcmd extends WarnCommandHandler {
	static List<String[]> warnings_player = new Vector<String[]>();

	public static boolean run(CommandSender sender, String[] args, Warner warner) {
		SLAPI slapi=warner.getSLAPI();
		String target;
		
		if (args.length < 2){
			if (!sender.hasPermission("warner.user.lookup.self")){
		    	sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
		    	return false;
		    }
		    if(sender instanceof Player){
		    	target = sender.getName();
		    } else {
		    	return false;
		    }
		} else {
		    if (!sender.hasPermission("warner.user.lookup.other")) {
		    	sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
		    	return false;
		    } else {
		    	Player targetOnline = (Bukkit.getServer().getPlayer(args[1]));
		    	if (targetOnline == null) {
		    	    OfflinePlayer targetOffline = (Bukkit.getServer().getOfflinePlayer(args[1]));
		    	    target = targetOffline.getName();
		    	} else {
		    	    String targetOnlineStr = (Bukkit.getServer().getPlayer(args[1]).getName());
		    	    target = targetOnlineStr;
		    	}
		    }
		}

		// Load player warnings
		warnings_player = slapi.loadPlayerWarnings(target, sender);
		if (warnings_player != null) {
			// player got no warnings
			int counter = 1;
			if (warnings_player.size() == 0) {
				sender.sendMessage(ChatColor.GOLD + target + ChatColor.BLUE + " has no warnings.");
				return false;
			} else
				for (String[] w : warnings_player) {
					sender.sendMessage(ChatColor.GOLD + "[" + counter + "] " + ChatColor.BLUE + "Warned by " + ChatColor.GOLD + w[0] + ChatColor.BLUE + " for " + ChatColor.GOLD + w[1] + ChatColor.BLUE + " on " + ChatColor.GOLD + w[2]);
					counter++;
				}
			return true;
		}
		
	return false;	
	}
}
