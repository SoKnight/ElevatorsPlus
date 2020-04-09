 package elevatorsplus.mechanic;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.mechanic.sound.AmbientSoundPlayer;
import elevatorsplus.mechanic.type.CallingSourceType;
import elevatorsplus.mechanic.type.Direction;
import elevatorsplus.mechanic.unit.CallingSource;
import elevatorsplus.mechanic.unit.PlatformBlock;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class ElevatorMoveOperator {

	@Getter private static Map<String, ElevatorLauncher> sessions = new ConcurrentHashMap<>();
	
	private final ElevatorsPlus plugin;
	private final Messages messages;
	
	private final AmbientSoundPlayer soundPlayer;
	private final ElevatorSignRefresher signRefresher;
	private final ElevatorDoorsOpener doorsOpener;
	
	public void startMove(Elevator elevator, CallingSource source) {
		HumanEntity caller = source.getCaller();
		
		World world = elevator.getBukkitWorld();
		Set<String> platform = elevator.getPlatform().keySet();
		
		Set<Entity> passengers = getPassengers(world, platform);
		if(source.getType() == CallingSourceType.SELF && passengers.isEmpty()) {
			messages.getAndSend(caller, "moving.no-passengers");
			return;
		}
		
		BlockData data = signRefresher.removeSign(elevator);
		if(data == null) {
			messages.sendFormatted(caller, "moving.sign-not-found", "%elevator%", elevator.getName());
			return;
		}
		
		this.doorsOpener.closeDoors(elevator, elevator.getCurrentLevel());
		
		List<PlatformBlock> platformBlocks = spawnFallingBlocks(elevator);
		
		elevator.setPassengers(passengers);
		elevator.setPlatformBlocks(platformBlocks);
		
		ElevatorLauncher launcher = new ElevatorLauncher(messages, elevator, source, data);
		sessions.put(elevator.getName(), launcher);
		
		launcher.launch();
		
		int targetY = launcher.getDestinationY();
		source.setTargetY(targetY);
		
		int target = source.getTarget();
		Direction direction = target > elevator.getCurrentLevel() ? Direction.UP : Direction.DOWN;
		
		plugin.getMovingTasksExecutor().addElevator(elevator, targetY, direction);
		
		this.soundPlayer.onStart(source.getCaller());
	}
	
	public void doneMove(Elevator elevator) {
		String name = elevator.getName();
		if(!sessions.containsKey(name)) return;
		
		ElevatorLauncher launcher = sessions.get(name);
		launcher.stop();
		
		BlockData data = launcher.getSignData();
		String signloc = elevator.getSignLocation();
		
		if(signloc != null) {
			int y = new TextLocation(signloc).getY();
			y += launcher.getLength();
			
			this.signRefresher.createSign(elevator, data, y);
		}
		
		this.soundPlayer.onFinish(launcher.getCaller());
		this.doorsOpener.openDoors(elevator, elevator.getCurrentLevel());
		
		sessions.remove(name);
		elevator.setWorking(false);
		
		plugin.getDatabaseManager().updateElevator(elevator);
	}
	
	private Set<Entity> getPassengers(World world, Set<String> platformBlocks) {
		List<Entity> all = world.getEntities();
		Set<Entity> passengers = new HashSet<>();
		
		all.forEach(e -> {
			if(!e.isOnGround()) return;
			Location location = e.getLocation().add(0, -1, 0);
			TextLocation textLoc = new TextLocation(location);
			
			if(!platformBlocks.contains(textLoc.getAsString())) return;
			passengers.add(e);
		});
		
		return passengers;
	}
	
	private List<PlatformBlock> spawnFallingBlocks(Elevator elevator) {
		Map<String, Material> platform = elevator.getPlatform();
		World world = elevator.getBukkitWorld();
		String name = elevator.getName();
		
		List<PlatformBlock> fallingBlocks = new ArrayList<>();
		MetadataValue value = new FixedMetadataValue(ElevatorsPlus.getInstance(), true);
		
		platform.forEach((l, m) -> {
			TextLocation textLoc = new TextLocation(l);
			Location location = textLoc.toLocation(world);
			world.getBlockAt(location).setType(Material.AIR);
			
			location.add(0.5, 0, 0.5);
			
			FallingBlock block = world.spawnFallingBlock(location, Bukkit.createBlockData(m));
			block.setGravity(false);
			block.setInvulnerable(true);
			block.setDropItem(false);
			block.setMetadata("eplus_platform_" + name, value);
			
			fallingBlocks.add(new PlatformBlock(block, textLoc));
		});
		
		return fallingBlocks;
	}
	
}
