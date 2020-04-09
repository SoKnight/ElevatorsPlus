package elevatorsplus.mechanic;

import java.util.List;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.util.Vector;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.mechanic.unit.CallingSource;
import elevatorsplus.mechanic.unit.PlatformBlock;
import lombok.Getter;
import ru.soknight.lib.configuration.Messages;

public class ElevatorLauncher {

	private static final MetadataValue VALUE = new FixedMetadataValue(ElevatorsPlus.getInstance(), true);
	private static final Vector STOP_VECTOR = new Vector(0, 0, 0);
	
	private final ElevatorsPlus plugin;
	private final Messages messages;
	
	private final int destinationLevel;
	@Getter private final HumanEntity caller;
	private final Elevator elevator;
	
	@Getter private int length;
	private int currentY;
	@Getter private int destinationY;
	
	@Getter private BlockData signData;
	
	public ElevatorLauncher(Messages messages, Elevator elevator, CallingSource source, BlockData signData) {
		this.messages = messages;
		this.plugin = ElevatorsPlus.getInstance();
		
		this.destinationLevel = source.getTarget();
		this.caller = source.getCaller();
		
		this.elevator = elevator;
		this.signData = signData;
	}
	
	public void launch() {
		elevator.setWorking(true);
		String name = elevator.getName();
		
		Set<Entity> passengers = elevator.getPassengers();
		List<PlatformBlock> platformBlocks = elevator.getPlatformBlocks();
		
		this.length = calculatePathLength();
		
		this.currentY = platformBlocks.get(0).getTextLocation().getY();
		this.destinationY = currentY + length;
		
		passengers.forEach(e -> {
			e.setGravity(false);
			e.setMetadata("eplus_passenger_" + name, VALUE);
			if(e instanceof Player)
				messages.sendFormatted(caller, "moving.move.started", "%level%", destinationLevel);
		});
		
		if(!passengers.contains(caller))
			messages.sendFormatted(caller, "moving.call.started", "%level%", destinationLevel);
		
		return;
	}
	
	public void stop() {
		String name = elevator.getName();
		World world = elevator.getBukkitWorld();
		
		Set<Entity> passengers = elevator.getPassengers();
		
		elevator.getPlatform().clear();
		
		elevator.getPlatformBlocks().forEach(pb -> {
			FallingBlock b = pb.getEntity();
			BlockData data = b.getBlockData();
			
			TextLocation start = pb.getTextLocation();
			Location loc = b.getLocation();
			
			b.remove();
			loc.setY(start.getY() + length);
			
			Block block = world.getBlockAt(loc);
			block.setBlockData(data, false);
			
			TextLocation textLoc = new TextLocation(loc);
			elevator.setPlatformBlock(textLoc.getAsString(), data.getMaterial());
		});
		
		if(!passengers.isEmpty())
			passengers.forEach(p -> {
				if(p == null) return;
			
				p.setVelocity(STOP_VECTOR);
			
				Location location = p.getLocation();
				location.setY(destinationY + 1);
			
				p.teleport(location);
				p.setFallDistance(0);
				p.setGravity(true);
			
				p.removeMetadata("eplus_platform_" + name, plugin);
				if(p instanceof Player)
					messages.sendFormatted(caller, "moving.move.finished", "%level%", destinationLevel);
			});
		
		if(!passengers.contains(caller))
			messages.sendFormatted(caller, "moving.call.finished", "%level%", destinationLevel);
		
		elevator.setPassengers(null);
		elevator.setPlatformBlocks(null);
		
		elevator.setCurrentLevel(destinationLevel);
	}
	
	private int calculatePathLength() {
		int current = elevator.getCurrentLevel();
		int length = 0;
		
		if(destinationLevel > current)
			for(int i = current; i < this.destinationLevel; i++)
				length += elevator.getLevelHeight(i) + 1;
		else
			for(int i = this.destinationLevel; i < current; i++)
				length -= elevator.getLevelHeight(i) + 1;
		
		return length;
	}
	
}
