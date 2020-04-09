package elevatorsplus.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.listener.session.LinkingSession;
import elevatorsplus.listener.session.SessionManager;
import lombok.RequiredArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@RequiredArgsConstructor
public class LinkingSessionListener implements Listener {

	private final Messages messages;
	private final SessionManager sessions;
	private final DatabaseManager dbm;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onChat(AsyncPlayerChatEvent event) {
		Player p = event.getPlayer();
		
		String name = p.getName();
		if(!sessions.hasLinkingSession(name)) return;
		
		event.setCancelled(true);
		
		LinkingSession session = sessions.getLinkingSession(name);
		boolean relinking = session.isRelinking();
		
		ElementType type = session.getType();
		if(type != ElementType.CALLBUTTONS && type != ElementType.DOORS) return;
		
		String section = "listener." + type.toString().toLowerCase();
		
		String message = event.getMessage();
		if(message.toLowerCase().equals("cancel")) {
			sessions.doneLinkingSession(name);
			messages.getAndSend(p, relinking ? section + ".relink.aborted" : section + ".link.aborted");
			return;
		}
		
		String elevatorName = session.getElevator();
		Elevator elevator = dbm.getElevator(elevatorName);
		
		if(elevator == null) {
			sessions.doneLinkingSession(name);
			messages.sendFormatted(p, "listener.unknown-elevator", "%elevator%", elevatorName);
			return;
		}
		
		if(elevator.isWorking()) {
			messages.sendFormatted(p, "listener.elevator-is-busy", "%elevator%", elevatorName);
			return;
		}
		
		int level, levels = elevator.getLevelsCount();
		try {
			level = Integer.parseInt(message);
			if(level < 1 || level > levels) {
				messages.sendFormatted(p, "listener.unknown-level", "%levels%", levels);
				return;
			}
		} catch (NumberFormatException e) {
			messages.sendFormatted(p, "listener.unknown-level", "%levels%", levels);
			return;
		}
		
		TextLocation textloc = session.getLocation();
		String strloc = textloc.getAsString();
		
		int x = textloc.getX();
		int y = textloc.getY();
		int z = textloc.getZ();
		
		sessions.doneLinkingSession(name);
		
		if(type == ElementType.CALLBUTTONS)
			elevator.linkCallButton(strloc, level);
		else elevator.linkDoor(strloc, level);
		
		section += relinking ? ".relink.success" : ".link.success";
		String msg = messages.getFormatted(section, "%elevator%", elevatorName, "%x%", x, "%y%", y, "%z%", z);
		
		if(relinking) {
			int current = elevator.getCallbuttonLevel(strloc);
			if((Integer) current == null) current = 0;
			
			msg = messages.format(msg, "%from%", current, "%to%", level);
		} else msg = messages.format(msg, "%level%", level);
		
		dbm.updateElevator(elevator);
		messages.send(p, msg);
	}
	
}
