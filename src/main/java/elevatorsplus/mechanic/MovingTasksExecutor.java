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

public class MovingTasksExecutor implements Runnable {

	private Set<AbstractElevatorMoveTask> tasks;
	
	private final ElevatorsPlus plugin;
	private final double speed;
	
	public MovingTasksExecutor(ElevatorsPlus plugin, double speed) {
		this.tasks = Collections.synchronizedSet(new CopyOnWriteArraySet<AbstractElevatorMoveTask>());
		this.plugin = plugin;
		this.speed = speed;
	}
	
	public void addElevator(Elevator elevator, int targetY, Direction direction) {
		AbstractElevatorMoveTask task;
		if(direction == Direction.UP) task = new ElevatorMoveUpTask(plugin, elevator, targetY, speed);
		else task = new ElevatorMoveDownTask(plugin, elevator, targetY, speed);
		tasks.add(task);
	}
	
	@Override
	public void run() {
		if(tasks.isEmpty()) return;
		
		tasks.removeIf(AbstractElevatorMoveTask::isDone);
	}

}
