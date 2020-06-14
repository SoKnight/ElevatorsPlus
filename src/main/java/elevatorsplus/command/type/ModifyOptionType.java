package elevatorsplus.command.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum ModifyOptionType {

	DEFHEIGHT, LVLSCOUNT, LVLHEIGHT, NAME, SIGN, WORLD;
	
	private static List<String> values;
	
	public static List<String> getValues() {
		if(values != null) return values;
		
		values = new ArrayList<>();
		Arrays.stream(values()).forEach(o -> values.add(o.toString().toLowerCase()));
		return values;
	}
	
}
