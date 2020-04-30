package elevatorsplus.event;

import java.util.Set;

import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.unit.CallingSource;
import lombok.Getter;
import lombok.Setter;

/**
 * Event will be called when anyone elevator starts his moving
 */
@Getter
@Setter
public class ElevatorStartingMovingEvent extends Event implements Cancellable {

	@Getter private static HandlerList handlerList = new HandlerList();
	
	private boolean cancelled;
	
	private Elevator elevator;
	private CallingSource source;
	private Set<Entity> passengers;
	private BlockData signData;
	
	public ElevatorStartingMovingEvent(Elevator elevator, CallingSource source,
			Set<Entity> passengers, BlockData signData) {
		
		this.elevator = elevator;
		this.source = source;
		this.passengers = passengers;
		this.signData = signData;
	}
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}

}
