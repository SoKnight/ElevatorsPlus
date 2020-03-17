package elevatorsplus.utils;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.files.Config;

public class Logger {

	private static ElevatorsPlus instance = ElevatorsPlus.getInstance();
	
	public static void info(String info) {
		instance.getLogger().info(info);
	}
	
	public static void warning(String warning) {
		instance.getLogger().warning(warning);
	}
	
	public static void error(String error) {
		instance.getLogger().severe(error);
	}
	
	public static void debug(String debug) {
		if(!Config.getConfig().getBoolean("messages.debug")) return;
		instance.getLogger().info("[Debug] " + debug);
	}
	
	public static void debug(String debug, int changed) {
		if(!Config.getConfig().getBoolean("messages.debug")) return;
		instance.getLogger().info("[Debug] " + debug + ", " + changed + " entries changed in the database.");
	}
	
}
