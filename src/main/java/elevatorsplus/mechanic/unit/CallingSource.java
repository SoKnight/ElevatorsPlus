package elevatorsplus.mechanic.unit;

import org.bukkit.entity.HumanEntity;

import elevatorsplus.mechanic.type.CallingSourceType;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class CallingSource {

	private final CallingSourceType type;
	private final HumanEntity caller;
	private final int target;
	private int targetY;
	
}
