package com.minecraftserver.warn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.minecraftserver.warn.Warner;

public class Warnversioncmd extends WarnCommandHandler {
	
	public static boolean run(CommandSender sender, Warner warner) {
		
		if (!sender.hasPermission("warner.other.version")) {
			sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
			return false;
		}
		sender.sendMessage(ChatColor.BLUE + "Version: " + ChatColor.GOLD + ChatColor.ITALIC + warner.getVersion() + ChatColor.RESET + "\n" + ChatColor.BLUE + warner.getAuthor());
		return true;
		
	}

}
