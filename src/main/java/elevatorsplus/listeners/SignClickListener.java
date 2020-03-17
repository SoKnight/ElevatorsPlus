package elevatorsplus.listeners;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.objects.sessions.ViewSession;
import elevatorsplus.ui.MenuBuilder;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SignClickListener implements Listener {

	private final DatabaseManager dbm;
	private final SessionManager sessions;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onSignClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		EquipmentSlot hand = event.getHand();
		if(!hand.equals(EquipmentSlot.HAND)) return;
		
		Block b = event.getClickedBlock();
		if(!Config.getSigns().contains(b.getType())) return;
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		
		Elevator elevator = dbm.getElevatorBySign(textloc.getAsString());
		if(elevator == null || elevator.isWorking()) return;
		
		Player p = event.getPlayer();
		if(!p.hasPermission("eplus.use")) {
			p.sendMessage(Messages.getMessage("mech-no-permissions"));
			return;
		}
		
		String elevatorName = elevator.getName();
		
		if(!elevator.isConfigured()) {
			p.sendMessage(Messages.formatMessage("mech-unconfigured", "%elevator%", elevatorName));
			return;
		}
		
		Inventory inventory = elevator.getGui();
		if(inventory == null) {
			MenuBuilder builder = ElevatorsPlus.getInstance().getMenuBuilder();
			inventory = builder.build(elevator);
			elevator.setGui(inventory);
		}
		
		InventoryView view = p.openInventory(inventory);
		sessions.startViewSession(p, new ViewSession(view, elevator));
	}
	
}
