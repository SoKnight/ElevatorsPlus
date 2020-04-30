package elevatorsplus.mechanic.task;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Slime;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.unit.PlatformBlock;

public abstract class AbstractElevatorMoveTask {
	
	private final MetadataValue value;
	private final PotionEffect effect;
	
	private final ElevatorsPlus plugin;
	private final ElevatorMoveOperator moveOperator;
	
	private final Elevator elevator;
	private final Vector vector;
	private final Slime slime;
	
	public AbstractElevatorMoveTask(ElevatorsPlus plugin, ElevatorMoveOperator moveOperator,
			Elevator elevator, double speed) {
		
		this.plugin = plugin;
		this.moveOperator = moveOperator;
		
		this.value = new FixedMetadataValue(plugin, true);
		this.effect = new PotionEffect(PotionEffectType.INVISIBILITY, 6000, 1, false, false);

		this.elevator = elevator;
		this.vector = new Vector(0, speed, 0);
		
		World world = elevator.getBukkitWorld();
		Location location = elevator.getPlatformBlocks().get(0).getEntity().getLocation();
		location = location.add(0, 1, 0);
		
		Slime slime = (Slime) world.spawnEntity(location, EntityType.SLIME);
		slime.setGravity(false);
		slime.setAI(false);
		slime.setSize(1);
		slime.setInvulnerable(true);
		slime.setCollidable(false);
		slime.addPotionEffect(effect);
		slime.setMetadata("eplus_conductor_" + elevator.getName(), value);
		this.slime = slime;
		
		elevator.getPassengers().add(slime);
	}
	
	public abstract boolean isReached(float currentY);
	
	public boolean isDone() {
		List<PlatformBlock> platform = elevator.getPlatformBlocks();
		platform.parallelStream().forEach(b -> {
			FallingBlock pb = b.getEntity();
			if(pb != null) pb.setVelocity(vector);
		});
		
		Set<Entity> passengers = elevator.getPassengers();
		if(!passengers.isEmpty())
			passengers.parallelStream().forEach(p -> {
				if(p != null) p.setVelocity(vector);
				else passengers.remove(p);
			});
		
		elevator.setPassengers(passengers);
		
		slime.teleport(platform.get(0).getEntity());
		float current = (float) slime.getLocation().getY();
		
		if(isReached(current)) {
			slime.remove();
			Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> moveOperator.doneMove(elevator));
			return true;
		} else return false;
	}
	
}
