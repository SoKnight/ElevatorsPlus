package elevatorsplus.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import lombok.AllArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class ElementsDestroyingListener implements Listener {
	
	private final Config config;
	private final Messages messages;
	
	private final DatabaseManager databaseManager;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDestroy(BlockBreakEvent event) {
		// If destroying is allowed, just finish execution
		if(config.getBoolean("allow-controls-destroy")) return;
		
		Player p = event.getPlayer();
		Block b = event.getBlock();
		
		String world = b.getWorld().getName();
		List<Elevator> elevators = databaseManager.getElevatorsInWorld(world);
		
		// Checking elevators in current world
		if(elevators == null || elevators.isEmpty()) return;
		
		boolean callbutton = false, door = false, platform = false, sign = false;
		Elevator target = null;
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		for(Elevator e : elevators) {
			String signloc = e.getSignLocation();
			
			sign = signloc != null ? signloc.equals(strloc) : false;
			if(sign) { target = e; break; }
			
			door = e.isDoor(strloc);
			if(door) { target = e; break; }
			
			platform = e.isPlatformBlock(strloc);
			if(platform) { target = e; break; }
			
			callbutton = e.isCallButton(strloc);
			if(callbutton) { target = e; break; }
		}
		
		if(target == null) return;
		
		if(!p.hasPermission("eplus.controls.destroy")) {
			messages.getAndSend(p, "listener.destroying.no-permissions");
			return;
		}
		
		String name = target.getName();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		if(callbutton) {
			target.unlinkCallButton(strloc);
			messages.sendFormatted(p, "listener.destroying.callbutton", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		}
		
		if(door) {
			target.unlinkDoor(strloc);
			messages.sendFormatted(p, "listener.destroying.door", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		}
		
		if(platform) {
			target.removePlatformBlock(strloc);
			messages.sendFormatted(p, "listener.destroying.platform", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		}
		
		if(sign) {
			target.setSignLocation("none");
			messages.sendFormatted(p, "listener.destroying.sign", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		}
		
		databaseManager.updateElevator(target);
	}
	
}
