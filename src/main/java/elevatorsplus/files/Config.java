package elevatorsplus.files;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.ui.MenuPattern;
import elevatorsplus.utils.Logger;
import lombok.Getter;

public class Config {

	@Getter private static FileConfiguration config;
	@Getter private static MenuPattern menuPattern;
	
	@Getter private static List<Material> callbuttons, signs, doors;
	@Getter private static List<String> signcontent, materials;
	
	public static void refresh() {
		ElevatorsPlus instance = ElevatorsPlus.getInstance();
		
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		
		File file = new File(instance.getDataFolder(), "config.yml");
		if(!file.exists()) {
			try {
				Files.copy(instance.getResource("config.yml"), file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new config file.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
		
		menuPattern = new MenuPattern();
		refreshLists();
	}
	
	private static void refreshLists() {
		materials = new ArrayList<>();
		Arrays.stream(Material.values()).filter(m -> !m.isLegacy()).forEach(m -> materials.add(m.name()));
		
		callbuttons = new ArrayList<>();
		for(String s : config.getStringList("controls.call-buttons"))
			if(materials.contains(s.toUpperCase())) callbuttons.add(Material.valueOf(s.toUpperCase()));
			else Logger.error("Couldn't get material '" + s + "' from controls.call-buttons list!");
		
		signs = new ArrayList<>();
		for(String s : config.getStringList("controls.signs"))
			if(materials.contains(s.toUpperCase())) signs.add(Material.valueOf(s.toUpperCase()));
			else Logger.error("Couldn't get material '" + s + "' from controls.signs list!");
		
		if(config.getBoolean("open-doors.enabled")) {
			doors = new ArrayList<>();
				for(String s : config.getStringList("open-doors.doors"))
					if(materials.contains(s.toUpperCase())) doors.add(Material.valueOf(s.toUpperCase()));
					else Logger.error("Couldn't get material '" + s + "' from open-doors.doors list!"); }
		
		signcontent = getColoredList("controls.sign-content");
		return;
	}
	
	public static String getColoredString(String section) {
		String message = config.getString(section, "");
		return message.replace("&", "\u00a7");
	}
	
	public static List<String> getColoredList(String section) {
		List<String> list = config.getStringList(section);
		List<String> output = new ArrayList<>();
		if(list == null) return output;
		
		list.forEach(s -> output.add(s.replace("&", "\u00a7")));
		return output;
	}
	
}
