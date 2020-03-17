package elevatorsplus.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.objects.sessions.LinkingSession;
import elevatorsplus.utils.Logger;
import elevatorsplus.utils.StringUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LinkingSessionListener implements Listener {

	private final SessionManager sessions;
	private final DatabaseManager dbm;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		String name = p.getName();
		if(!sessions.hasLinkingSession(name)) return;
		
		event.setCancelled(true);
		
		LinkingSession session = sessions.getLinkingSession(name);
		boolean relinking = session.isRelinking();
		
		String message = event.getMessage();
		if(message.toLowerCase().equals("none")) {
			sessions.doneLinkingSession(name);
			p.sendMessage(Messages.getMessage(relinking ? "listener-relinking-abort" : "listener-linking-abort"));
			return;
		}
		
		String elevatorName = session.getElevator();
		Elevator elevator = dbm.getElevator(elevatorName);
		
		if(elevator == null) {
			sessions.doneLinkingSession(name);
			p.sendMessage(Messages.formatMessage("listener-unknown-elevator", "%elevator%", elevatorName));
			return;
		}
		
		if(elevator.isWorking()) {
			p.sendMessage(Messages.formatMessage("listener-elevator-works", "%elevator%", elevatorName));
			return;
		}
		
		int floor, max = elevator.getFloorsCount();
		try {
			floor = Integer.parseInt(message);
			if(floor < 1 || floor > max) {
				p.sendMessage(Messages.formatMessage("listener-invalid-floor", "%max%", max));
				return;
			}
		} catch (NumberFormatException e) {
			p.sendMessage(Messages.formatMessage("listener-invalid-floor", "%max%", max));
			return;
		}
		
		TextLocation textloc = session.getLocation();
		String strloc = textloc.getAsString();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		sessions.doneLinkingSession(name);
		elevator.linkCallButton(strloc, floor);
		dbm.updateElevator(elevator);
		
		String section = relinking ? "listener-callbutton-relinked" : "listener-callbutton-linked";
		String msg = Messages.formatMessage(section, "%elevator%", elevatorName, "%x%", x, "%y%", y, "%z%", z);
		
		if(relinking) {
			int current = elevator.getLinkedFloor(strloc);
			if((Integer) current == null) current = 0;
			
			msg = StringUtils.format(msg, "%from%", current, "%to%", floor);
		} else msg = StringUtils.format(msg, "%level%", floor);
		
		p.sendMessage(msg);
		
		// Debug message
		Logger.debug("Button at (" + x + " " + y + " " + z + ") linked with floor #" + floor + " for elevator '"
				+ elevatorName + "' by user " + name);
	}
	
}
