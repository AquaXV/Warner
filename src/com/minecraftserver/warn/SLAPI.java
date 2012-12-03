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
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import org.bukkit.plugin.java.JavaPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class SLAPI {

	private File dataFolder;
    YamlConfiguration config = new YamlConfiguration();
    File playerfolder;

    public SLAPI(File dataFolder){
    	this.dataFolder=dataFolder;
    	playerfolder = new File(dataFolder, File.separator + "player_warnings");
    }
    
	public List<String[]> loadPlayerWarnings(Player target) {
		try {
			List<String[]> list;
			return load(playerfolder + File.separator + target.getName() + ".bin");
		} catch (Exception e) {
			List<String[]> list;
			list = new Vector<String[]>();
			try {
				File playerfile=new File(playerfolder, File.separator + target.getName() + ".bin");
				if(!playerfile.exists()) playerfile.createNewFile();
					savePlayerWarnings(list, target);
				return load(playerfolder + File.separator + target.getName() + ".bin");
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
	}

	public boolean savePlayerWarnings(List<String[]> warnings, Player target) {
		try {
			File playerfile=new File(playerfolder, File.separator + target.getName() + ".bin");
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