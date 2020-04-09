package elevatorsplus.mechanic;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.data.Bisected.Half;
import org.bukkit.block.data.type.Door;

import elevatorsplus.configuration.Config;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class ElevatorDoorsOpener {

	private Config config;
	
	public void openDoors(Elevator elevator, int level) {
		World world = elevator.getBukkitWorld();
		if(world == null) return;
		
		Map<String, Integer> doors = elevator.getDoors();
		if(doors.isEmpty()) return;
		
		doors.forEach((k, v) -> {
			if(v != level) return;
			
			Location l = new TextLocation(k).toLocation(world);
			Block b = world.getBlockAt(l);
			
			if(!config.getDoors().contains(b.getType())) return;
			
			BlockState s = b.getState();
			open(s);
			
			Half half = ((Door) s.getBlockData()).getHalf();
			
			Location lo = l.add(0, half == Half.BOTTOM ? 1 : -1, 0);
			Block bo = world.getBlockAt(lo);
			
			if(!config.getDoors().contains(bo.getType())) return;
			
			BlockState so = bo.getState();
			open(so);
		});
	}
	
	public void closeDoors(Elevator elevator, int level) {
		World world = elevator.getBukkitWorld();
		if(world == null) return;
		
		Map<String, Integer> doors = elevator.getDoors();
		if(doors.isEmpty()) return;
		
		doors.forEach((k, v) -> {
			if(v != level) return;
			
			Location l = new TextLocation(k).toLocation(world);
			Block b = world.getBlockAt(l);
			
			if(!config.getDoors().contains(b.getType())) return;
			
			BlockState s = b.getState();
			close(s);
			
			Half half = ((Door) s.getBlockData()).getHalf();
			
			Location lo = l.add(0, half == Half.BOTTOM ? 1 : -1, 0);
			Block bo = world.getBlockAt(lo);
			
			if(!config.getDoors().contains(bo.getType())) return;
			
			BlockState so = bo.getState();
			close(so);
		});
	}
	
	private void open(BlockState state) {
		Door door = (Door) state.getBlockData();
			
		if(door.isOpen()) return;
		
		door.setOpen(true);
			
		state.setBlockData(door);
		state.update(true, false);
	}
	
	private void close(BlockState state) {
		Door door = (Door) state.getBlockData();
			
		if(!door.isOpen()) return;
		
		door.setOpen(false);
			
		state.setBlockData(door);
		state.update(true, false);
	}
	
}
