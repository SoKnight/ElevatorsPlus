package elevatorsplus.mechanic.unit;

import java.util.UUID;

import org.bukkit.entity.FallingBlock;

import elevatorsplus.database.TextLocation;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PlatformBlock {

	private FallingBlock entity;
	private TextLocation textLocation;
	
	public UUID getUniqueID() {
		return entity.getUniqueId();
	}
	
}
