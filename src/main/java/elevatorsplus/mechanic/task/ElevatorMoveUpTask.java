package elevatorsplus.mechanic.task;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;

public class ElevatorMoveUpTask extends AbstractElevatorMoveTask {

	private final float targetY;
	
	public ElevatorMoveUpTask(ElevatorsPlus plugin, Elevator elevator, int targetY, double speed) {
		super(plugin, elevator, speed);
		this.targetY = targetY;
	}

	@Override
	public boolean isReached(float currentY) {
		return currentY + 0.5 >= targetY;
	}
	
}
