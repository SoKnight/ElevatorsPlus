package elevatorsplus.listener;

import java.util.List;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.type.Door;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.listener.session.ViewSession;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.type.CallingSourceType;
import elevatorsplus.mechanic.unit.CallingSource;
import elevatorsplus.ui.MenuBuilder;
import lombok.AllArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class ElementsClickListener implements Listener {

	private final ElevatorsPlus plugin;
	private final Config config;
	private final Messages messages;
	private final DatabaseManager dbm;
	private final SessionManager sm;
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onClick(PlayerInteractEvent event) {
		Action action = event.getAction();
		if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
		
		EquipmentSlot hand = event.getHand();
		if(!hand.equals(EquipmentSlot.HAND)) return;
		
		Block b = event.getClickedBlock();
		Material type = b.getType();
		
		Player p = event.getPlayer();
		
		if(config.getSigns().contains(type)) {
			handleSignClick(p, b);
			event.setCancelled(true);
		} else if(config.getCallbuttons().contains(type))
			handleCallbuttonClick(p, b);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onRedstone(BlockRedstoneEvent event) {
		Block b = event.getBlock();
		
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String l = textloc.getAsString();
		
		List<Elevator> elevators = dbm.getElevatorsInWorld(b.getWorld().getName());
		if(elevators == null || elevators.isEmpty()) return;
		
		for(Elevator e : elevators) {
			if(!e.isDoor(l)) continue;
			
			event.setNewCurrent(event.getOldCurrent());
			
			if(!config.getDoors().contains(b.getType())) break;
			
			BlockState state = b.getState();
			if(state == null) break;

			Door door = (Door) state.getBlockData();
			door.setPowered(!door.isPowered());
			
			state.setBlockData(door);
			state.update(true, false);
			
			break;
		}
		
	}
	
	private void handleSignClick(Player p, Block b) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		
		Elevator elevator = dbm.getElevatorBySign(textloc.getAsString());
		if(elevator == null || elevator.isWorking()) return;
		
		if(!p.hasPermission("eplus.use")) {
			messages.getAndSend(p, "moving.no-permissions");
			return;
		}
		
		String name = elevator.getName();
		if(!elevator.isConfigured()) {
			messages.sendFormatted(p, "moving.is-unconfigured", "%elevator%", name);
			return;
		}
		
		Inventory inventory = elevator.getGui();
		if(inventory == null) {
			MenuBuilder builder = ElevatorsPlus.getInstance().getMenuBuilder();
			inventory = builder.build(elevator);
			elevator.setGui(inventory);
		}
		
		InventoryView view = p.openInventory(inventory);
		sm.startViewSession(p, new ViewSession(view, elevator));
	}
	
	private void handleCallbuttonClick(Player p, Block b) {
		Location location = b.getLocation();
		TextLocation textloc = new TextLocation(location);
		String l = textloc.getAsString();
		
		List<Elevator> elevators = dbm.getElevatorsInWorld(p.getWorld().getName());
		if(elevators == null || elevators.isEmpty()) return;
		
		Elevator elevator = null;
		for(Elevator e : elevators) {
			if(!e.isCallButton(l)) continue;
			elevator = e;
			break;
		}
		
		if(elevator == null) return;
		
		if(!p.hasPermission("eplus.use")) {
			messages.getAndSend(p, "moving.no-permissions");
			return;
		}
		
		String name = elevator.getName();
		if(!elevator.isConfigured()) {
			messages.sendFormatted(p, "moving.is-unconfigured", "%elevator%", name);
			return;
		}
		
		int target = elevator.getCallbuttonLevel(l);
		if(elevator.getLevelsCount() < target) {
			messages.getAndSend(p, "moving.changed");
			return;
		}
		
		if(target == elevator.getCurrentLevel()) {
			messages.getAndSend(p, "moving.already-there");
			return;
		}
		
		World world = elevator.getBukkitWorld();
		Map<String, Material> platform = elevator.getPlatform();
		if(world == null || platform.isEmpty()) {
			messages.sendFormatted(p, "moving.is-unconfigured", "%elevator%", name);
			return;
		}
		
		ElevatorMoveOperator moveOperator = plugin.getMoveOperator();
		CallingSource source = new CallingSource(CallingSourceType.CALL, p, target);
		
		moveOperator.startMove(elevator, source);
	}
	
}
