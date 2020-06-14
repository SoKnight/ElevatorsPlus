package elevatorsplus.ui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elevatorsplus.database.Elevator;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.listener.session.ViewSession;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.type.CallingSourceType;
import elevatorsplus.mechanic.unit.CallingSource;
import lombok.AllArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class MenuListener implements Listener {
	
	private Messages messages;
	
	private final SessionManager sessionManager;
	private final ElevatorMoveOperator moveOperator;
	
	public void update(Messages messages) {
		this.messages = messages;
	}
	
	@EventHandler
	public void onClick(InventoryClickEvent event) {
		if(event.getCurrentItem() == null) return;
		
		HumanEntity p = event.getWhoClicked();
		if(!sessionManager.hasViewSession(p)) return;
		
		ViewSession session = sessionManager.getViewSession(p);
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
		
		CallingSource source = new CallingSource(CallingSourceType.SELF, p, target);
		
		moveOperator.startMove(elevator, source);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity p = event.getPlayer();
		if(sessionManager.hasViewSession(p))
			sessionManager.doneViewSession(p);
	}
	
}
