package elevatorsplus.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.ElevatorLauncher;
import elevatorsplus.mechanic.type.Direction;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event will be called when anyone elevator started his moving
 */
@Getter
@AllArgsConstructor
public class ElevatorStartedMovingEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();
	
	private final Elevator elevator;
	private final ElevatorLauncher launcher;
	private final Direction direction;
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
}
