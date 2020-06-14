package elevatorsplus.database;

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
	
	public TextLocation(String locationAsString) {
		String[] values = locationAsString.split(" ");
		this.x = Integer.parseInt(values[0]);
		this.y = Integer.parseInt(values[1]);
		this.z = Integer.parseInt(values[2]);
	}
	
	public Location toLocation(World world) {
		if(world == null) return null;
		
		return new Location(world, x, y, z);
	}
	
	public String getAsString() {
		return x + " " + y + " " + z;
	}
	
}
