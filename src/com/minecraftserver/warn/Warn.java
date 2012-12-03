package com.minecraftserver.warn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Ambient;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import com.minecraftserver.warn.Punisher;

public class Warn extends JavaPlugin {

	int warn_amount;
	int arguments = 0;
	public static String reason = "You have been warned";
	private String Players;
	private FileConfiguration customConfig = null;
	private File customConfigFile = null;
	String[] args;
	private File configFile;
	YamlConfiguration config = new YamlConfiguration();
	static List<String[]> warnings_player = new Vector<String[]>();
	SLAPI slapi;
	private Player target;

	// Start args[] to String
	String createString(String[] args, int start) {
		return createString(args, start, " ");
	}

	String createString(String[] args, int start, String glue) {
		StringBuilder string = new StringBuilder();

		for (int x = start; x < args.length; x++) {
			string.append(args[x]);
			if (x != args.length - 1) {
				string.append(glue);
			}
		}

		return string.toString();
	}

	// End args[] to String

	public String Version() {
		return "2.5.7";
	}

	public String Author() {
		return "Made by AquaXV.\nMany thanks to M0P and TechTeller96 for helping me create it.\nThanks to DarkMagician6 and ProForYou for helping testing.\nAnd not to forget Bukkit ofcourse.";
	}

	public boolean onCommand(CommandSender cmdsender, Command cmd, String label, String[] args) {
		if ((cmd.getName().equalsIgnoreCase("warn")) && (args.length == 0)) {
			CommandSender sender;
			sender = getSender(cmdsender);
			sender.sendMessage(ChatColor.WHITE + "/warn help");
			return false;
		} else {
			if ((cmd.getName().equalsIgnoreCase("warn")) && (Bukkit.getServer().getPlayer(args[0]) != null)) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.warn.give")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				reason = "You have been warned";
				target = (Bukkit.getServer().getPlayer(args[0]));
				if (target != null) {
					if (target.hasPermission("warner.admin.warn.exempt")) {
						sender.sendMessage(ChatColor.RED + "You can't warn this person!");
						return false;
					}
					if (args.length > 1) {
						reason = createString(args, 1);
					}
				} else {
					sender.sendMessage(ChatColor.RED + args[0] + " was not found online!");
					return false;
				}
				// Load existing warnings of player
				warnings_player = slapi.loadPlayerWarnings(target);
				if (warnings_player != null) {
					Long time_difference = 20L;
					if (warnings_player.size() > 0) {
						time_difference = (System.currentTimeMillis() - Long.parseLong(warnings_player .get(warnings_player.size()-1)[3]) )/ 1000;
					}
					if (time_difference >= 20) {
						String[] warning = { sender.getName(), reason, getTimeNow(), System.currentTimeMillis() + "" };
						warnings_player.add(warning);
					} else {
						sender.sendMessage(ChatColor.RED + "You have to wait " + ChatColor.GOLD + (20 - time_difference) + ChatColor.RED + " seconds until you can warn " + ChatColor.GOLD + target.getName() + ChatColor.RED + " again.");
						return false;
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
				target.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + " has warned you for:\n" + ChatColor.GOLD + reason + ChatColor.BLUE + "." + ChatColor.GOLD + " [" + warnings_player.size() + "]");
				Player[] playerList = Bukkit.getServer().getOnlinePlayers();
				for (Player player : playerList) {
					if (player.hasPermission("warner.other.notify")) {
						player.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has been warned by " + ChatColor.GOLD + sender.getName() + ChatColor.BLUE + " for:\n" + ChatColor.GOLD + reason + ChatColor.BLUE + "." + ChatColor.GOLD + " [" + warnings_player.size() + "]");
					}
				}
				Punisher.punish(target, warnings_player.size(), reason, sender, config);
				return false;
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("help"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.user.help")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				sender.sendMessage(ChatColor.GOLD + "Available commands (<required> [optional]):");
				if (sender.hasPermission("warner.warn.give")) {
					sender.sendMessage(ChatColor.GREEN + "/warn <user> [reason] - Give <user> a warning.");	
				}
				if (sender.hasPermission("warner.user.lookup.self")){
					sender.sendMessage(ChatColor.GREEN + "/warn lookup - See your warnings.");
				}
				if (sender.hasPermission("warner.user.lookup.other")) {
					sender.sendMessage(ChatColor.GREEN + "/warn lookup <user> - See the warnings <user> has.");
				}
				if (sender.hasPermission("warner.warn.take")) {
					sender.sendMessage(ChatColor.GREEN + "/warn take <user> <warning> - Remove a warning of <user>.");
				}
				if (sender.hasPermission("warner.admin.warn.set")) {
					sender.sendMessage(ChatColor.GREEN + "/warn set <user> <amount> - set warnings of <user> to <amount>.");
				}
				if (sender.hasPermission("warner.admin.warn.reset")) {
					sender.sendMessage(ChatColor.GREEN + "/warn reset <user> - Completely removes warnings from <user>.");
				}
				if (sender.hasPermission("warner.other.version")) {
					sender.sendMessage(ChatColor.GREEN + "/warn version - See the plugin version.");
				}
				if (sender.hasPermission("warner.other.reload")) {
					sender.sendMessage(ChatColor.GREEN + "/warn reload - reload the plugin");
				}
				return false;
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("take"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.warn.take")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
					sender.sendMessage(ChatColor.RED + "/warn take <user> <warning>");
					return false;
				}
				target = (Bukkit.getServer().getPlayer(args[1]));
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
							sender.sendMessage(ChatColor.BLUE + "There is no warning " + ChatColor.GOLD + args[2] + ChatColor.BLUE + ".");
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
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("set"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.admin.warn.set")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				if (args.length < 3) {
					sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
					sender.sendMessage(ChatColor.RED + "/warn set <user> <amount>");
					return false;
				}
				Player target = (Bukkit.getServer().getPlayer(args[1]));
				if (target != null) {
					if (target.hasPermission("warner.admin.warn.exempt")) {
						sender.sendMessage(ChatColor.RED + "You can't warn this person!");
						return false;
					}
				} else {
					sender.sendMessage(ChatColor.RED + args[0] + " was not found online!");
					return false;
				}
				try {
					warn_amount = Integer.parseInt(args[2]);
					// Warnings = Integer.parseInt(args[2]);
				} catch (Exception e) {
					sender.sendMessage(ChatColor.RED + ("No value is specified!"));
					return false;
				}
				// Load player warnings
				warnings_player = slapi.loadPlayerWarnings(target);
				if (warnings_player != null) {
					// if player got more warnings then arg
					while (warnings_player.size() > warn_amount) {
						// always removes the oldest warning
						warnings_player.remove(0);
					}
					while (warnings_player.size() < warn_amount) {
						// add standard warning
						String[] warning = { sender.getName(), reason, getTimeNow(), System.currentTimeMillis() + "" };
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
				target.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + " has set your warnings to:" + ChatColor.GOLD + " [" + warnings_player.size() + "]");
				Punisher.punish(target, warn_amount, reason, sender, config);
				return true;
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("lookup"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (args.length < 2){
					if (!sender.hasPermission("warner.user.lookup.self")){
				    	sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
				    	return false;
				    }
				    if(sender instanceof Player){
				    	target = (Player) sender;
				    } else {
				    	return false;
				    }
				}			
				else {
				    if (!sender.hasPermission("warner.user.lookup.other")) {
				    	sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
				    	return false;
				    } else {
				    	target = (Bukkit.getServer().getPlayer(args[1]));
				    }
				}

				// Load player warnings
				warnings_player = slapi.loadPlayerWarnings(target);
				if (warnings_player != null) {
					// player got no warnings
					int counter = 1;
					if (warnings_player.size() == 0) {
						sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has no warnings.");
						return false;
					} else
						for (String[] w : warnings_player) {
							sender.sendMessage(ChatColor.GOLD + "[" + counter + "] " + ChatColor.BLUE + "Warned by " + ChatColor.GOLD + w[0] + ChatColor.BLUE + " for " + ChatColor.GOLD + w[1] + ChatColor.BLUE + " on " + ChatColor.GOLD + w[2]);
							counter++;
						}
					return true;
				}
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("reset"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.admin.warn.reset")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				if (args.length < 2) {
					sender.sendMessage(ChatColor.RED + "Too few arguments, usage:");
					sender.sendMessage(ChatColor.RED + "/warn reset <user>");
					return false;
				}
				Player target = (Bukkit.getServer().getPlayer(args[1]));

				// Load player warnings
				warnings_player = slapi.loadPlayerWarnings(target);
				if (warnings_player != null) {
					if (warnings_player.size() == 0) {
						sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has no warnings\n Can't reset " + ChatColor.GOLD + target.getName() + ChatColor.BLUE + ".");
						return false;
					}
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has " + ChatColor.GOLD + warnings_player.size() + ChatColor.BLUE + " warnings.");
					warnings_player = new Vector<String[]>();
					// save warnings to file
					if (!slapi.savePlayerWarnings(warnings_player, target)) {
						sender.sendMessage(ChatColor.RED + "Error writing Playerfile!");
						return false;
					}
					sender.sendMessage(ChatColor.GOLD + target.getName() + ChatColor.BLUE + " has been reset.");
				} else {
					sender.sendMessage(ChatColor.RED + "Error reading Playerfile!");
					return false;
				}
				target.sendMessage(ChatColor.GOLD + sender.getName() + ChatColor.BLUE + " has reset your warnings!");
				return true;
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("version"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.other.version")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				sender.sendMessage("Version " + Version() + "\n" + Author());
				return true;
			} else if ((cmd.getName().equalsIgnoreCase("warn")) && (args[0].equalsIgnoreCase("reload"))) {
				CommandSender sender;
				sender = getSender(cmdsender);
				if (!sender.hasPermission("warner.admin.reload")) {
					sender.sendMessage(ChatColor.DARK_RED + "You have insufficient permissions to do this.");
					return false;
				}
				onDisable();
				onEnable();
				sender.sendMessage(ChatColor.BLUE + "Warner has been reloaded!");
				return true;
			}
		}
		return false;
	}

	private CommandSender getSender(CommandSender cmdsender) {
		CommandSender sender;
		if (!cmdsender.equals(Bukkit.getConsoleSender())) {
			sender = (Player) cmdsender;
		} else {
			sender = cmdsender;
		}
		return sender;
	}

	private String getTimeNow() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy - hh:mm a");
		return dateFormat.format(new Date());
	}

	@Override
	public void onEnable() {
		getLogger().info("Warner has been enabled!");
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		File playerfolder = new File(getDataFolder(), File.separator + "player_warnings");
		if (!playerfolder.exists()) playerfolder.mkdir();
		slapi = new SLAPI(getDataFolder());
		configFile = new File(getDataFolder(), "config.yml");
		config = slapi.loadYamls(configFile);

	}

	@Override
	public void onDisable() {
		getLogger().info("Warner has been disabled!");
	}
}