package com.minecraftserver.warn;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Vector;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

public class SLAPI {

	YamlConfiguration config = new YamlConfiguration();
    File playerfolder;

    public SLAPI(File dataFolder){
    	playerfolder = new File(dataFolder, File.separator + "player_warnings");
    }
    
	public List<String[]> loadPlayerWarnings(String target, CommandSender sender) {
		try {
			return load(playerfolder + File.separator + target + ".bin");
		} catch (Exception e) {
			List<String[]> list;
			list = new Vector<String[]>();
			try {
				File playerfile = new File(playerfolder, File.separator + target + ".bin");
				if(!playerfile.exists()) playerfile.createNewFile();
					savePlayerWarnings(list, target);
				return load(playerfolder + File.separator + target + ".bin");
			} catch (Exception e1) {
				e1.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Playerfile not found!");
			}
			return null;
		}
	}

	public boolean savePlayerWarnings(List<String[]> warnings, String target) {
		try {
			File playerfile=new File(playerfolder, File.separator + target + ".bin");
			save(warnings, playerfile);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	public static <T extends Object> void save(T obj, File path) throws Exception {
		ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path));
		oos.writeObject(obj);
		oos.flush();
		oos.close();
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends Object> T load(String path) throws Exception {
		ObjectInputStream ois = new ObjectInputStream(new FileInputStream(path));
		T result = (T) ois.readObject();
		ois.close();
		return result;
	}
	
	public YamlConfiguration loadYamls(File configFile) {
	    try {
	        config.load(configFile);
	        return config;
	    } catch (Exception e) {
	    	if(!configFile.exists())
				try {
					configFile.createNewFile();
				} catch (IOException e1) {
					e1.printStackTrace();
					return null;
				}
	    	return loadYamls(configFile);
	    }
	}

}