package elevatorsplus.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.enums.Control;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.objects.sessions.SelectionSession;
import elevatorsplus.utils.Logger;
import elevatorsplus.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SelectionSessionListener implements Listener {

	private final SessionManager sessions;
	private final DatabaseManager dbm;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		String name = p.getName();
		if(!sessions.hasSelectionSession(name)) return;
		
		Action action = event.getAction();
		if(!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		EquipmentSlot hand = event.getHand();
		if(!hand.equals(EquipmentSlot.HAND)) return;
		
		SelectionSession session = sessions.getSelectionSession(name);
		String elevatorName = session.getElevator();
		
		Elevator elevator = dbm.getElevator(elevatorName);
		if(elevator == null) {
			sessions.doneSelectionSession(name);
			p.sendMessage(Messages.formatMessage("listener-unknown-elevator", "%elevator%", elevatorName));
			return;
		}
		
		if(elevator.isWorking()) {
			p.sendMessage(Messages.formatMessage("listener-elevator-works", "%elevator%", elevatorName));
			return;
		}
		
		String world = elevator.getWorld();
		if(!p.getWorld().getName().equals(world)) {
			p.sendMessage(Messages.formatMessage("listener-different-worlds", "%world%", world));
			return;
		}
		
		Block b = event.getClickedBlock();
		Control control = session.getControl();
		
		if(control.equals(Control.CALLBUTTONS)) {
			if(action.equals(Action.LEFT_CLICK_BLOCK))
				removeCallbutton(p, b, elevator);
			else addCallbutton(p, b, elevator);
		} else {
			if(action.equals(Action.LEFT_CLICK_BLOCK))
				removePlatformBlock(p, b, elevator);
			else addPlatformBlock(p, b, elevator);
		}
		
		dbm.updateElevator(elevator);
		event.setCancelled(true);
	}
	
	private void addCallbutton(Player p, Block b, Elevator elevator) {
		Material material = b.getType();
		String materialName = StringUtils.capitalizeFirst(material.name());
		
		if(!Config.getCallbuttons().contains(material)) {
			p.sendMessage(Messages.formatMessage("listener-cannot-be-callbutton", "%material%", materialName));
			return;
		}
		
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int count = elevator.getFloorsCount();
		
		if(elevator.isCallButton(strloc)) {
			int current = elevator.getLinkedFloor(strloc);
			sessions.startLinkingSession(name, elevatorName, textloc, current);
			p.sendMessage(Messages.formatMessage("listener-tip-callbutton-relink", "%current%", current, "%max%", count));
		} else {
			sessions.startLinkingSession(name, elevatorName, textloc);
			p.sendMessage(Messages.formatMessage("listener-tip-callbutton-link", "%max%", count));
		}
	}
	
	private void removeCallbutton(Player p, Block b, Elevator elevator) {
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		if(!elevator.isCallButton(strloc)) {
			p.sendMessage(Messages.formatMessage("listener-callbutton-not-linked", "%elevator%", elevatorName,
					"%x%", x, "%y%", y, "%z%", z));
			return;
		}
		
		elevator.unlinkCallButton(strloc);
		p.sendMessage(Messages.formatMessage("listener-callbutton-unlinked", "%elevator%", elevatorName,
				"%x%", x, "%y%", y, "%z%", z));
		
		// Debug message
		Logger.debug("Button at (" + x + " " + y + " " + z + ") unlinked with '" + elevatorName
				+ "' by user " + name);
	}
	
	private void addPlatformBlock(Player p, Block b, Elevator elevator) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		if(elevator.isPlatformBlock(strloc)) {
			p.sendMessage(Messages.getMessage("listener-already-platform"));
			return;
		}
		
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		elevator.setPlatformBlock(strloc, b.getType());
		p.sendMessage(Messages.formatMessage("listener-platform-added", "%elevator%", elevatorName,
				"%x%", x, "%y%", y, "%z%", z));
		
		// Debug message
		Logger.debug("Block at (" + x + " " + y + " " + z + ") marked as platform for '"
				+ elevatorName + "' by user " + name);
	}
	
	private void removePlatformBlock(Player p, Block b, Elevator elevator) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		if(!elevator.isPlatformBlock(strloc)) {
			p.sendMessage(Messages.getMessage("listener-is-not-platform"));
			return;
		}
		
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		elevator.removePlatformBlock(strloc);
		p.sendMessage(Messages.formatMessage("listener-platform-removed", "%elevator%", elevatorName,
				"%x%", x, "%y%", y, "%z%", z));
		
		// Debug message
		Logger.debug("Block at (" + x + " " + y + " " + z + ") no longer marked as platform for '"
				+ elevatorName + "' by user " + name);
	}
	
}
