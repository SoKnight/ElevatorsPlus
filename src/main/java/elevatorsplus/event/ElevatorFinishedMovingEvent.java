package elevatorsplus.event;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.ElevatorLauncher;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Event will be called when anyone elevator finished his moving
 */
@Getter
@AllArgsConstructor
public class ElevatorFinishedMovingEvent extends Event {

	@Getter private static HandlerList handlerList = new HandlerList();
	
	private final Elevator elevator;
	private final ElevatorLauncher launcher;
	
	@Override
	public HandlerList getHandlers() {
		return handlerList;
	}
	
}
