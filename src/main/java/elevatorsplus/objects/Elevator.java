
package elevatorsplus.objects;

import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.inventory.Inventory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import elevatorsplus.files.Config;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@DatabaseTable(tableName = "elevators")
public class Elevator {
	
	@DatabaseField(id = true)
	private String name;
	@DatabaseField
	private String world;
	@DatabaseField(columnName = "signlocation")
	private String signLocation;
	@DatabaseField(columnName = "currentfloor")
	private int currentFloor;
	@DatabaseField(columnName = "floorscount")
	private int floorsCount;
	@DatabaseField(columnName = "defaultheight")
	private int defaultFloorHeight;
	@DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "floorheights")
	HashMap<Integer, Integer> floorHeights;
	@DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "callbuttons")
	HashMap<String, Integer> callButtons;
	@DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "platformblocks")
	HashMap<String, Material> platformBlocks;
	
	private boolean working;
	private Inventory gui;
	
	public Elevator(String name) {
		this.name = name;
		this.world = "none";
		this.signLocation = "none";
		this.currentFloor = 1;
		this.floorsCount = 1;
		this.defaultFloorHeight = Config.getConfig().getInt("default-floor-height", 3);
		this.floorHeights = new HashMap<>();
		this.callButtons = new HashMap<>();
		this.platformBlocks = new HashMap<>();
	}
	
	public boolean isConfigured() {
		if(world.equals("none")) return false;
		if(signLocation.equals("none")) return false;
		if(platformBlocks.isEmpty()) return false;
		return true;
	}
	
	/*
	 * FLOOR HEIGHTS
	 */
	
	public int getFloorHeight(int floor) {
		return hasFloorHeight(floor) ? floorHeights.get(floor) : defaultFloorHeight;
	}
	
	public boolean hasFloorHeight(int floor) {
		return floorHeights.containsKey(floor);
	}
	
	public boolean setFloorHeight(int floor, int height) {
		if(getFloorHeight(floor) == height) return false;
		
		floorHeights.put(floor, height);
		return true;
	}
	
	public boolean resetFloorHeight(int floor) {
		floorHeights.remove(floor);
		return true;
	}
	
	/*
	 * CALLBUTTONS
	 */
	
	public int getLinkedFloor(String location) {
		return callButtons.get(location);
	}
	
	public boolean isCallButton(String location) {
		return callButtons.containsKey(location);
	}
	
	public boolean linkCallButton(String location, int floor) {
		if(isCallButton(location) && getLinkedFloor(location) == floor) return false;
		
		callButtons.put(location, floor);
		return true;
	}
	
	public boolean unlinkCallButton(String location) {
		if(!isCallButton(location)) return false;
		
		callButtons.remove(location);
		return true;
	}
	
	/*
	 * PLATFORM BLOCKS
	 */
	
	public Material getPlatformBlock(String location) {
		return platformBlocks.get(location);
	}
	
	public boolean isPlatformBlock(String location) {
		return platformBlocks.containsKey(location);
	}
	
	public boolean setPlatformBlock(String location, Material material) {
		if(isPlatformBlock(location)) return false;
		
		platformBlocks.put(location, material);
		return true;
	}
	
	public boolean removePlatformBlock(String location) {
		if(!isPlatformBlock(location)) return false;
		
		platformBlocks.remove(location);
		return true;
	}
	
}
