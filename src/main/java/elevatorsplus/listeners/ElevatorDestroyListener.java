package elevatorsplus.listeners;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.utils.Logger;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ElevatorDestroyListener implements Listener {
	
	private final DatabaseManager dbm;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onDestroy(BlockBreakEvent event) {
		// If destroying is allowed, return.
		if(Config.getConfig().getBoolean("allow-controls-destroy")) return;
		
		Player p = event.getPlayer();
		Block b = event.getBlock();
		
		String world = b.getWorld().getName();
		List<Elevator> elevators = dbm.getElevatorsInWorld(world);
		
		// Checking elevators in current world
		if(elevators == null || elevators.isEmpty()) return;
		
		boolean callbutton = false, platform = false, sign = false;
		Elevator target = null;
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		for(Elevator e : elevators) {
			callbutton = e.isCallButton(strloc);
			if(callbutton) { target = e; break; }
			
			platform = e.isPlatformBlock(strloc);
			if(platform) { target = e; break; }
			
			sign = e.getSignLocation().equals(strloc);
			if(sign) { target = e; break; }
		}
		
		if(target == null) return;
		
		if(!p.hasPermission("eplus.controls.destroy")) {
			p.sendMessage(Messages.getMessage("destroying-no-permissions"));
			return;
		}
		
		String elevatorName = target.getName();
		String name = p.getName();
		
		int x = location.getBlockX();
		int y = location.getBlockY();
		int z = location.getBlockZ();
		
		if(callbutton) {
			target.unlinkCallButton(strloc);
			p.sendMessage(Messages.formatMessage("destroying-callbutton", "%elevator%", elevatorName,
					"%x%", x, "%y%", y, "%z%", z));
			
			// Debug message
			Logger.debug("Callbutton at (" + x + " " + y + " " + z + ") of elevator '"
					+ elevatorName + "' destroyed by user " + name);
		}
		
		if(platform) {
			target.removePlatformBlock(strloc);
			p.sendMessage(Messages.formatMessage("destroying-platform", "%elevator%", elevatorName,
					"%x%", x, "%y%", y, "%z%", z));
			
			// Debug message
			Logger.debug("Platform block at (" + x + " " + y + " " + z + ") of elevator '"
					+ elevatorName + "' destroyed by user " + name);
		}
		
		if(sign) {
			target.setSignLocation("none");
			p.sendMessage(Messages.formatMessage("destroying-sign", "%elevator%", elevatorName,
					"%x%", x, "%y%", y, "%z%", z));
			
			// Debug message
			Logger.debug("Sign at (" + x + " " + y + " " + z + ") of elevator '"
					+ elevatorName + "' destroyed by user " + name);
		}
		
		dbm.updateElevator(target);
	}
	
}
