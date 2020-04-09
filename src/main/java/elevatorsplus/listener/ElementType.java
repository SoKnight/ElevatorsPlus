package elevatorsplus.listener;

import java.util.Arrays;
import java.util.List;

public enum ElementType {

	CALLBUTTONS, DOORS, LVLSHEIGHTS, PLATFORM;
	
	public static List<String> getSelectionValues() {
		return Arrays.asList("callbuttons", "platform", "doors");
	}
	
	public static List<String> getAllValues() {
		return Arrays.asList("callbuttons", "lvlsheights", "platform", "doors");
	}
	
}
