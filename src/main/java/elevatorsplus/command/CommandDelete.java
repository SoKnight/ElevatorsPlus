package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandDelete extends ExtendedSubcommandExecutor {

	private final ElevatorsPlus plugin;
	private final Messages messages;
	
	public CommandDelete(ElevatorsPlus plugin, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.delete", permmsg);
		Validator argsval = new ArgsCountValidator(2, argsmsg);
		Validator elevval = new ElevatorValidator(true, elevmsg);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		String name = args[1];
		
		DatabaseManager dbm = plugin.getDatabaseManager();
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, dbm, name);
		if(!validateExecution(data)) return;
		
		Elevator elevator = dbm.getElevator(name);
		dbm.removeElevator(elevator);
		
		messages.sendFormatted(sender, "general.deleted", "%elevator%", name);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, String[] args) {
		if(args.length != 2 || !validateTabCompletion(sender, args)) return null;
		
		String arg = args[1].toLowerCase();
		
		DatabaseManager dbm = plugin.getDatabaseManager();
		
		List<String> elevators = dbm.getAllNames();
		List<String> output = new ArrayList<>();
		
		elevators.stream()
			.filter(s -> s.toLowerCase().startsWith(arg))
			.forEach(e -> output.add(e));
		
		return output;
	}
	
}
