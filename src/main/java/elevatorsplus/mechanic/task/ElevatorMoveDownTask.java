package elevatorsplus.mechanic.task;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;

public class ElevatorMoveDownTask extends AbstractElevatorMoveTask {

	private final float targetY;
	
	public ElevatorMoveDownTask(ElevatorsPlus plugin, Elevator elevator, float targetY, double speed) {
		super(plugin, elevator, -speed);
		this.targetY = targetY;
	}

	@Override
	public boolean isReached(float currentY) {
		return currentY - 0.5 <= targetY;
	}
	
}
