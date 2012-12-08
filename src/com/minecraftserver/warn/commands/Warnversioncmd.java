package com.minecraftserver.warn.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.minecraftserver.warn.Warner;

public class Warnversioncmd extends WarnCommandHandler {
	
	static Warner warner;
	
	public static boolean run(CommandSender sender) {
		
		if (!sender.hasPermission("warner.other.version")) {
			sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
			return false;
		}
		sender.sendMessage(ChatColor.BLUE + "Version: " + ChatColor.GOLD + ChatColor.UNDERLINE + warner.getVersion() + ChatColor.RESET + "\n" + ChatColor.BLUE + warner.getAuthor());
		return true;
		
	}

}
