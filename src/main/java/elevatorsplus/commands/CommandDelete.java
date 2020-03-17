package elevatorsplus.commands;

import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.Logger;

public class CommandDelete extends AbstractSubCommand {

	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandDelete(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "eplus.command.delete", 2);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String name = args[1];
		if(!isElevatorExist(name)) return;
		
		Elevator elevator = dbm.getElevator(name);
		
		int changed = dbm.removeElevator(elevator);
		sendMessage("delete-success", "%elevator%", name);
		
		// Debug message
		Logger.debug("Deleted elevator '" + name + "' by user " + sender.getName(), changed);
	}
	
}
