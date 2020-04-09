package elevatorsplus.ui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.listener.session.ViewSession;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.type.CallingSourceType;
import elevatorsplus.mechanic.unit.CallingSource;
import lombok.RequiredArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@RequiredArgsConstructor
public class MenuListener implements Listener {
	
	private final ElevatorsPlus plugin;
	private final Messages messages;
	private final SessionManager sessions;
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null) return;
		
		HumanEntity p = event.getWhoClicked();
		if(!sessions.hasViewSession(p)) return;
		
		ViewSession session = sessions.getViewSession(p);
		Elevator elevator = session.getElevator();
		
		InventoryView view = session.getView();
		Inventory inventory = event.getInventory();
		
		if(!view.getTopInventory().equals(inventory)) return;
		
		event.setCancelled(true);
		p.closeInventory();
		
		int target = event.getSlot() + 1;
		if(elevator.getLevelsCount() < target) {
			messages.getAndSend(p, "moving.changed");
			return;
		}
		
		if(target == elevator.getCurrentLevel()) {
			messages.getAndSend(p, "moving.already-there");
			return;
		}
		
		if(!elevator.isConfigured()) {
			messages.sendFormatted(p, "moving.is-unconfigured", "%elevator%", elevator.getName());
			return;
		}
		
		ElevatorMoveOperator moveOperator = plugin.getMoveOperator();
		CallingSource source = new CallingSource(CallingSourceType.SELF, p, target);
		
		moveOperator.startMove(elevator, source);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity p = event.getPlayer();
		if(sessions.hasViewSession(p))
			sessions.doneViewSession(p);
	}
	
}
