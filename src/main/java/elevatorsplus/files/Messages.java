package elevatorsplus.files;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.utils.Logger;
import elevatorsplus.utils.StringUtils;
import lombok.Getter;

public class Messages {

	@Getter private static FileConfiguration config;
	
	public static void refresh() {
		ElevatorsPlus instance = ElevatorsPlus.getInstance();
		
		File datafolder = instance.getDataFolder();
		if(!datafolder.isDirectory()) datafolder.mkdirs();
		
		String locale = Config.getConfig().getString("messages.locale", "en");
		String path = "locales" + File.separator + "messages_" + locale + ".yml";
		
		File file = new File(instance.getDataFolder(), "messages_" + locale + ".yml");
		if(!file.exists()) {
			try {
				InputStream input = instance.getResource(path);
				if(input == null) {
					Logger.error("Unknown locale " + locale + ", localization for this locale not found.");
					path = "locales" + File.separator + "messages_en.yml";
					input = instance.getResource(path);
					locale = "en";
				}
				
				Files.copy(input, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
				Logger.info("Generated new messages file for locale '" + locale + "'.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}
	
	public static String getMessage(String section) {
		if(!config.isSet(section)) {
			Logger.error("Couldn't load message from messages.yml: " + section);
			return "Whoops! Message not found :(";
		}
		String output = config.getString(section).replace("&", "\u00A7");
		return output;
	}
	
	public static String formatMessage(String section, Object... replacements) {
		if(!config.isSet(section)) {
			Logger.error("Couldn't load message from messages.yml: " + section);
			return "Whoops! Message not found :(";
		}
		
		String output = StringUtils.format(getMessage(section), replacements);
		return output;
	}
	
	public static List<String> getMessagesList(String section) {
		List<String> output = new ArrayList<>();
		if(!config.isSet(section)) {
			Logger.error("Couldn't load messages list from messages.yml: " + section);
			return Arrays.asList("Whoops! Messages list not found :(");
		}
		
		config.getStringList(section).forEach(s -> output.add(s.replace("&", "\u00a7")));
		return output;
	}
	
}
