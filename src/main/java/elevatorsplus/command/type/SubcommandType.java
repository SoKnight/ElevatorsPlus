package elevatorsplus.command.type;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public enum SubcommandType {

	HELP, CREATE, DELETE, MODIFY, INFO, LIST, RELOAD, DONE, SELECTION, ELEMENTS;
	
	private static List<String> values;
	
	public static List<String> getValues() {
		if(values != null) return values;
		
		values = new ArrayList<>();
		Arrays.stream(values()).forEach(s -> values.add(s.toString().toLowerCase()));
		return values;
	}
	
}
