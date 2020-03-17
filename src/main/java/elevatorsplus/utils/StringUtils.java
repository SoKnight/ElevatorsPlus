package elevatorsplus.utils;

import org.bukkit.Location;

public class StringUtils {

	public static String format(String source, Object... replacements) {
		for(int i = 0; i < replacements.length; i++) {
			String replacement = replacements[i].toString();
			if(replacement.startsWith("%") && replacement.endsWith("%")) continue;
			
			String node = replacements[i - 1].toString();
			source = source.replace(node, replacement);
		}
		return source;
	}
	
	public static String capitalizeFirst(String source) {
		return Character.toUpperCase(source.charAt(0)) + source.substring(1).toLowerCase();
	}
	
	public static String getStringFromLocation(Location location) {
		String output = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
		return output;
	}
	
}
