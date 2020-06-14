package elevatorsplus.listener;

import org.apache.commons.lang.WordUtils;
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

import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.listener.session.SelectionSession;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.mechanic.sound.AmbientSoundPlayer;
import lombok.AllArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class SelectionSessionListener implements Listener {

	private final Config config;
	private final Messages messages;
	
	private final SessionManager sessionManager;
	private final DatabaseManager databaseManager;
	
	private final AmbientSoundPlayer soundPlayer;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		
		String name = p.getName();
		if(!sessionManager.hasSelectionSession(name)) return;
		
		Action action = event.getAction();
		if(!action.equals(Action.LEFT_CLICK_BLOCK) && !action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		EquipmentSlot hand = event.getHand();
		if(!hand.equals(EquipmentSlot.HAND)) return;
		
		SelectionSession session = sessionManager.getSelectionSession(name);
		String elevatorName = session.getElevator();
		
		Elevator elevator = databaseManager.getElevator(elevatorName);
		if(elevator == null) {
			sessionManager.doneSelectionSession(name);
			messages.sendFormatted(p, "listener.unknown-elevator", "%elevator%", elevatorName);
			return;
		}
		
		if(elevator.isWorking()) {
			messages.sendFormatted(p, "listener.elevator-is-busy", "%elevator%", elevatorName);
			return;
		}
		
		String world = elevator.getWorld();
		if(!p.getWorld().getName().equals(world)) {
			messages.sendFormatted(p, "listener.different-worlds", "%world%", world);
			return;
		}
		
		Block b = event.getClickedBlock();
		ElementType control = session.getControl();
		
		event.setCancelled(true);
		
		switch (control) {
		case CALLBUTTONS:
			if(action.equals(Action.LEFT_CLICK_BLOCK))
				unlinkCallbutton(p, b, elevator);
			else linkCallbutton(p, b, elevator);
			break;
		case DOORS:
			if(action.equals(Action.LEFT_CLICK_BLOCK))
				unlinkDoor(p, b, elevator);
			else linkDoor(p, b, elevator);
			break;
		case PLATFORM:
			if(action.equals(Action.LEFT_CLICK_BLOCK))
				removePlatformBlock(p, b, elevator);
			else addPlatformBlock(p, b, elevator);
			databaseManager.updateElevator(elevator);
			break;
		default:
			break;
		}
		
	}
	
	private void linkCallbutton(Player p, Block b, Elevator elevator) {
		Material material = b.getType();
		String materialName = WordUtils.capitalize(material.name().toLowerCase());
		
		if(!config.getCallbuttons().contains(material)) {
			messages.sendFormatted(p, "listener.callbuttons.cannot-be", "%material%", materialName);
			return;
		}
		
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int levels = elevator.getLevelsCount();
		
		if(elevator.isCallButton(strloc)) {
			int current = elevator.getCallbuttonLevel(strloc);
			sessionManager.startLinkingSession(name, elevatorName, ElementType.CALLBUTTONS, textloc, current);
			messages.sendFormatted(p, "listener.callbuttons.relink.tip", "%levels%", levels, "%level%", current);
		} else {
			sessionManager.startLinkingSession(name, elevatorName, ElementType.CALLBUTTONS, textloc);
			messages.sendFormatted(p, "listener.callbuttons.link.tip", "%levels%", levels);
			soundPlayer.onSelect(p);
		}
		
	}
	
	private void unlinkCallbutton(Player p, Block b, Elevator elevator) {
		String name = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		if(!elevator.isCallButton(strloc)) {
			messages.sendFormatted(p, "listener.callbuttons.unlink.is-not", "%elevator%", name,
					"%x%", x, "%y%", y, "%z%", z);
			return;
		}
		
		elevator.unlinkCallButton(strloc);
		messages.sendFormatted(p, "listener.callbuttons.unlink.success", "%elevator%", name,
				"%x%", x, "%y%", y, "%z%", z);
		soundPlayer.onSelect(p);
	}
	
	private void linkDoor(Player p, Block b, Elevator elevator) {
		Material material = b.getType();
		String materialName = WordUtils.capitalize(material.name().toLowerCase());
		
		if(!config.getDoors().contains(material)) {
			messages.sendFormatted(p, "listener.doors.cannot-be", "%material%", materialName);
			return;
		}
		
		String name = p.getName();
		String elevatorName = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int levels = elevator.getLevelsCount();
		
		if(elevator.isDoor(strloc)) {
			int current = elevator.getDoorLevel(strloc);
			sessionManager.startLinkingSession(name, elevatorName, ElementType.DOORS, textloc, current);
			messages.sendFormatted(p, "listener.doors.relink.tip", "%levels%", levels, "%level%", current);
		} else {
			sessionManager.startLinkingSession(name, elevatorName, ElementType.DOORS, textloc);
			messages.sendFormatted(p, "listener.doors.link.tip", "%levels%", levels);
			soundPlayer.onSelect(p);
		}
	}
	
	private void unlinkDoor(Player p, Block b, Elevator elevator) {
		String name = elevator.getName();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		if(!elevator.isDoor(strloc)) {
			messages.sendFormatted(p, "listener.doors.unlink.is-not", "%elevator%", name,
					"%x%", x, "%y%", y, "%z%", z);
			return;
		}
		
		elevator.unlinkDoor(strloc);
		messages.sendFormatted(p, "listener.doors.unlink.success", "%elevator%", name,
				"%x%", x, "%y%", y, "%z%", z);
		soundPlayer.onSelect(p);
	}
	
	private void addPlatformBlock(Player p, Block b, Elevator elevator) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		if(elevator.isPlatformBlock(strloc)) {
			messages.getAndSend(p, "listener.platform.already");
			return;
		}
		
		String name = elevator.getName();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		elevator.setPlatformBlock(strloc, b.getType());
		messages.sendFormatted(p, "listener.platform.added", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		soundPlayer.onSelect(p);
	}
	
	private void removePlatformBlock(Player p, Block b, Elevator elevator) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String strloc = textloc.getAsString();
		
		if(!elevator.isPlatformBlock(strloc)) {
			messages.getAndSend(p, "listener.platform.is-not");
			return;
		}
		
		String name = elevator.getName();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		elevator.removePlatformBlock(strloc);
		messages.sendFormatted(p, "listener.platform.removed", "%elevator%", name, "%x%", x, "%y%", y, "%z%", z);
		soundPlayer.onSelect(p);
	}
	
}
