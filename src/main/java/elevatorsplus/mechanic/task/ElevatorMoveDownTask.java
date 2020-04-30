package elevatorsplus.mechanic.task;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.ElevatorMoveOperator;

public class ElevatorMoveDownTask extends AbstractElevatorMoveTask {

	private final float targetY;
	
	public ElevatorMoveDownTask(ElevatorsPlus plugin, ElevatorMoveOperator moveOperator,
			Elevator elevator, float targetY, double speed) {
		
		super(plugin, moveOperator, elevator, -speed);
		this.targetY = targetY;
	}

	@Override
	public boolean isReached(float currentY) {
		return currentY - 0.5 <= targetY;
	}
	
}
