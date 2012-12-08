package com.minecraftserver.warn.commands;

import java.io.File;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.minecraftserver.warn.SLAPI;
import com.minecraftserver.warn.Warner;

public class Warnreloadcmd {
	

	public static boolean run(CommandSender sender, Warner warner) {
	    
		if (!sender.hasPermission("warner.admin.reload")) {
			sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
			return false;
		}
		
		warner.onDisable();
		warner.onEnable();
		
		sender.sendMessage(ChatColor.BLUE + "Warner has been reloaded!");
		return true;
		
	}

}
