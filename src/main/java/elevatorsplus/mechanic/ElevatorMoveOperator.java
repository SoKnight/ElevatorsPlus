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
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.HumanEntity;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.PluginManager;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.event.ElevatorFinishedMovingEvent;
import elevatorsplus.event.ElevatorStartedMovingEvent;
import elevatorsplus.event.ElevatorStartingMovingEvent;
import elevatorsplus.mechanic.sound.AmbientSoundPlayer;
import elevatorsplus.mechanic.tool.ElevatorDoorsController;
import elevatorsplus.mechanic.tool.ElevatorSignRefresher;
import elevatorsplus.mechanic.type.CallingSourceType;
import elevatorsplus.mechanic.type.Direction;
import elevatorsplus.mechanic.unit.CallingSource;
import elevatorsplus.mechanic.unit.PlatformBlock;
import elevatorsplus.ui.MenuBuilder;
import lombok.Getter;
import ru.soknight.lib.configuration.Messages;

public class ElevatorMoveOperator {

	@Getter private static Map<String, ElevatorLauncher> sessions = new ConcurrentHashMap<>();
	
	private final ElevatorsPlus plugin;
	
	private final Config config;
	private final Messages messages;
	
	private final DatabaseManager databaseManager;
	private final MovingTasksExecutor tasksExecutor;
	private final MetadataValue value;
	
	private final AmbientSoundPlayer soundPlayer;
	private final ElevatorDoorsController doorsController;
	
	private final MenuBuilder menuBuilder;
	private final ElevatorSignRefresher signRefresher;
	
	private final PluginManager pluginManager;
	
	public ElevatorMoveOperator(
			ElevatorsPlus plugin,
			Config config,
			Messages messages,
			DatabaseManager databaseManager,
			MenuBuilder menuBuilder,
			AmbientSoundPlayer soundPlayer,
			ElevatorSignRefresher signRefresher) {
		
		this.plugin = plugin;
		this.config = config;
		this.messages = messages;
		
		this.databaseManager = databaseManager;
		this.value = new FixedMetadataValue(plugin, true);
		
		this.tasksExecutor = new MovingTasksExecutor(plugin, this);
		tasksExecutor.setSpeed(config.getDouble("moving-speed"));
		
		int frequency = config.getInt("moving-task-frequency");
		Bukkit.getScheduler().runTaskTimer(plugin, tasksExecutor, 0, frequency);
		
		this.menuBuilder = menuBuilder;
		this.signRefresher = signRefresher;
		
		this.soundPlayer = new AmbientSoundPlayer(plugin, config);
		this.doorsController = new ElevatorDoorsController(config);
		
		this.pluginManager = Bukkit.getServer().getPluginManager();
	}
	
	public void update() {
		tasksExecutor.setSpeed(config.getDouble("moving-speed"));
	}
	
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
		
		// Calling cancellable elevator moving starting event
		ElevatorStartingMovingEvent starting = new ElevatorStartingMovingEvent(elevator, source, passengers, data);
		pluginManager.callEvent(starting);
		
		if(starting.isCancelled()) return;
		
		elevator = starting.getElevator();
		source = starting.getSource();
		passengers = starting.getPassengers();
		data = starting.getSignData();
		
		this.doorsController.closeDoors(elevator, elevator.getCurrentLevel());
		
		List<PlatformBlock> platformBlocks = spawnFallingBlocks(elevator);
		
		elevator.setPassengers(passengers);
		elevator.setPlatformBlocks(platformBlocks);
		
		ElevatorLauncher launcher = new ElevatorLauncher(plugin, messages, elevator, source, data);
		sessions.put(elevator.getName(), launcher);
		
		launcher.launch();
		
		int targetY = launcher.getDestinationY();
		source.setTargetY(targetY);
		
		int target = source.getTarget();
		Direction direction = target > elevator.getCurrentLevel() ? Direction.UP : Direction.DOWN;
		
		tasksExecutor.addElevator(elevator, targetY, direction);
		
		this.soundPlayer.onStart(source.getCaller());
		
		// Calling elevator moving started event
		ElevatorStartedMovingEvent started = new ElevatorStartedMovingEvent(elevator, launcher, direction);
		pluginManager.callEvent(started);
	}
	
	public void doneMove(Elevator elevator) {
		String name = elevator.getName();
		if(!sessions.containsKey(name)) return;
		
		ElevatorLauncher launcher = sessions.get(name);
		launcher.stop(elevator);
		
		BlockData data = launcher.getSignData();
		String signloc = elevator.getSignLocation();
		
		if(signloc != null) {
			int y = new TextLocation(signloc).getY();
			y += launcher.getLength();
			
			this.signRefresher.createSign(elevator, data, y);
		}
		
		this.soundPlayer.onFinish(launcher.getCaller());
		this.doorsController.openDoors(elevator, elevator.getCurrentLevel());
		
		sessions.remove(name);
		elevator.setWorking(false);
		
		databaseManager.updateElevator(elevator);
		menuBuilder.updateGui(elevator);
		
		// Calling elevator moving finished event
		ElevatorFinishedMovingEvent finished = new ElevatorFinishedMovingEvent(elevator, launcher);
		pluginManager.callEvent(finished);
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
		
		platform.forEach((l, m) -> {
			TextLocation textLoc = new TextLocation(l);
			Location location = textLoc.toLocation(world);
			
			Block b = world.getBlockAt(location);
			BlockData data = b.getBlockData();
			
			if(data == null || data.getMaterial() != m) data = Bukkit.createBlockData(m);
			
			b.setType(Material.AIR);
			
			location.add(0.5, 0, 0.5);
			
			FallingBlock block = world.spawnFallingBlock(location, data);
			block.setGravity(false);
			block.setInvulnerable(true);
			block.setDropItem(false);
			block.setMetadata("eplus_platform_" + name, value);
			
			fallingBlocks.add(new PlatformBlock(block, textLoc));
		});
		
		return fallingBlocks;
	}
	
}
