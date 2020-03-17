package elevatorsplus.ui;

import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elevatorsplus.files.Messages;
import elevatorsplus.listeners.SessionManager;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.sessions.ViewSession;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MenuListener implements Listener {
	
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
		if(elevator.getFloorsCount() < target) {
			p.sendMessage(Messages.getMessage("mech-changed"));
			return;
		}
		
		if(target == elevator.getCurrentFloor()) {
			p.sendMessage(Messages.getMessage("mech-already-there"));
			return;
		}
		
		// TODO: checking for player is stay on platform
		// TODO: calling moving provider and moving to selected level
		
		p.sendMessage(Messages.formatMessage("mech-started", "%floor%", target));
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onInventoryClose(InventoryCloseEvent event) {
		HumanEntity p = event.getPlayer();
		if(sessions.hasViewSession(p))
			sessions.doneViewSession(p);
	}
	
}
