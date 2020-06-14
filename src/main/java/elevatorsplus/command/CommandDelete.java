package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandDelete extends ExtendedSubcommandExecutor {

	private final DatabaseManager databaseManager;
	private final Messages messages;
	
	public CommandDelete(DatabaseManager databaseManager, Messages messages) {
		super(messages);
		
		this.databaseManager = databaseManager;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.delete", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		Validator elevval = new ElevatorValidator(databaseManager, elevmsg);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		String name = args.get(0);
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, name);
		if(!validateExecution(data)) return;
		
		Elevator elevator = databaseManager.getElevator(name);
		databaseManager.removeElevator(elevator);
		
		messages.sendFormatted(sender, "general.deleted", "%elevator%", name);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.size() != 1 || !validateTabCompletion(sender, args)) return null;
		
		String arg = args.get(0).toLowerCase();
		
		List<String> elevators = databaseManager.getAllNames();
		List<String> output = new ArrayList<>();
		
		elevators.stream()
			.filter(s -> s.toLowerCase().startsWith(arg))
			.forEach(e -> output.add(e));
		
		return output;
	}
	
}
