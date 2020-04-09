
package elevatorsplus.database;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.Inventory;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

import elevatorsplus.configuration.Config;
import elevatorsplus.mechanic.unit.PlatformBlock;
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
	@DatabaseField(columnName = "currentlevel")
	private int currentLevel;
	@DatabaseField(columnName = "lvlscount")
	private int levelsCount;
	@DatabaseField(columnName = "defheight")
	private int defaultLevelHeight;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	LinkedHashMap<String, Integer> callbuttons;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	LinkedHashMap<String, Integer> doors;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	LinkedHashMap<Integer, Integer> lvlsheights;
	@DatabaseField(dataType = DataType.SERIALIZABLE, columnName = "platformblocks")
	LinkedHashMap<String, Material> platform;
	
	
	private boolean working;
	private Inventory gui;
	
	private Set<Entity> passengers;
	private List<PlatformBlock> platformBlocks;
	
	public Elevator(Config config, String name) {
		this.name = name;
		this.currentLevel = 1;
		this.levelsCount = 1;
		this.defaultLevelHeight = config.getInt("default-floor-height", 3);
		this.callbuttons = new LinkedHashMap<>();
		this.doors = new LinkedHashMap<>();
		this.lvlsheights = new LinkedHashMap<>();
		this.platform = new LinkedHashMap<>();
	}
	
	public boolean isConfigured() {
		if(world == null) return false;
		if(signLocation == null) return false;
		if(platform.isEmpty()) return false;
		return true;
	}
	
	public World getBukkitWorld() {
		return world == null ? null : Bukkit.getWorld(this.world);
	}
	
	/*
	 * CALLBUTTONS
	 */
	
	public int getCallbuttonLevel(String location) {
		return callbuttons.get(location);
	}
	
	public boolean isCallButton(String location) {
		return callbuttons.containsKey(location);
	}
	
	public boolean linkCallButton(String location, int floor) {
		if(isCallButton(location) && getCallbuttonLevel(location) == floor) return false;
		
		callbuttons.put(location, floor);
		return true;
	}
	
	public boolean unlinkCallButton(String location) {
		if(!isCallButton(location)) return false;
		
		callbuttons.remove(location);
		return true;
	}
	
	/*
	 * DOORS
	 */
	
	public int getDoorLevel(String location) {
		return doors.get(location);
	}
	
	public boolean isDoor(String location) {
		return doors.containsKey(location);
	}
	
	public boolean linkDoor(String location, int floor) {
		if(isDoor(location) && getDoorLevel(location) == floor) return false;
		
		doors.put(location, floor);
		return true;
	}
	
	public boolean unlinkDoor(String location) {
		if(!isDoor(location)) return false;
		
		doors.remove(location);
		return true;
	}
	
	/*
	 * LEVELS HEIGHTS
	 */
	
	public int getLevelHeight(int floor) {
		return hasLevelHeight(floor) ? lvlsheights.get(floor) : defaultLevelHeight;
	}
	
	public boolean hasLevelHeight(int floor) {
		return lvlsheights.containsKey(floor);
	}
	
	public boolean setLevelHeight(int floor, int height) {
		if(getLevelHeight(floor) == height) return false;
		
		lvlsheights.put(floor, height);
		return true;
	}
	
	public boolean resetLevelHeight(int floor) {
		lvlsheights.remove(floor);
		return true;
	}
	
	/*
	 * PLATFORM BLOCKS
	 */
	
	public Material getPlatformBlock(String location) {
		return platform.get(location);
	}
	
	public boolean isPlatformBlock(String location) {
		return platform.containsKey(location);
	}
	
	public boolean setPlatformBlock(String location, Material material) {
		if(isPlatformBlock(location)) return false;
		
		platform.put(location, material);
		return true;
	}
	
	public boolean removePlatformBlock(String location) {
		if(!isPlatformBlock(location)) return false;
		
		platform.remove(location);
		return true;
	}
	
}
