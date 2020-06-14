package elevatorsplus.mechanic;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.database.Elevator;
import elevatorsplus.mechanic.task.AbstractElevatorMoveTask;
import elevatorsplus.mechanic.task.ElevatorMoveDownTask;
import elevatorsplus.mechanic.task.ElevatorMoveUpTask;
import elevatorsplus.mechanic.type.Direction;
import lombok.Setter;

@Setter
public class MovingTasksExecutor implements Runnable {

	private final ElevatorsPlus plugin;
	private final Set<AbstractElevatorMoveTask> tasks;
	private final ElevatorMoveOperator moveOperator;
	
	private double speed;
	
	public MovingTasksExecutor(ElevatorsPlus plugin, ElevatorMoveOperator moveOperator) {
		this.plugin = plugin;
		this.moveOperator = moveOperator;
		
		this.tasks = Collections.synchronizedSet(new CopyOnWriteArraySet<AbstractElevatorMoveTask>());
	}
	
	public void addElevator(Elevator elevator, int targetY, Direction direction) {
		AbstractElevatorMoveTask task = direction == Direction.UP
				? new ElevatorMoveUpTask(plugin, moveOperator, elevator, targetY, speed)
				: new ElevatorMoveDownTask(plugin, moveOperator, elevator, targetY, speed);
				
		tasks.add(task);
	}
	
	@Override
	public void run() {
		if(tasks.isEmpty()) return;
		
		tasks.removeIf(AbstractElevatorMoveTask::isDone);
	}

}
