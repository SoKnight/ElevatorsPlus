package elevatorsplus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.Logger;

public class CommandCreate extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandCreate(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "eplus.command.create", 2);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String name = args[1];
		
		if(!isElevatorNotExist(name)) return;
			
		String world = sender instanceof Player ? ((Player) sender).getWorld().getName() : "none";
		
		Elevator elevator = new Elevator(name);
		elevator.setWorld(world);
		
		int changed = dbm.createElevator(elevator);
		sendMessage("create-success", "%name%", name, "%world%", world);
		
		// Debug message
		Logger.debug("Created elevator '" + name + "' in '" + world + "' by user " + sender.getName(), changed);
	}
	
}
