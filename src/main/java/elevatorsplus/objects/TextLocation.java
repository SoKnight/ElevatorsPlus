package elevatorsplus.objects;

import org.bukkit.Location;
import org.bukkit.World;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TextLocation {

	private int x;
	private int y;
	private int z;
	
	public TextLocation(Location location) {
		this.x = location.getBlockX();
		this.y = location.getBlockY();
		this.z = location.getBlockZ();
	}
	
	public Location toLocation(World world) {
		if(world == null) return null;
		
		return new Location(world, x, y, z);
	}
	
	public String getAsString() {
		return x + " " + y + " " + z;
	}
	
}
