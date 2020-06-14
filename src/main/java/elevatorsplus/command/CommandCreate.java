package elevatorsplus.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandCreate extends ExtendedSubcommandExecutor {
	
	private final DatabaseManager databaseManager;
	
	private final Config config;
	private final Messages messages;
	
	public CommandCreate(DatabaseManager databaseManager, Config config, Messages messages) {
		super(messages);
		
		this.databaseManager = databaseManager;
		this.config = config;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("general.create.exist");
		
		Validator permval = new PermissionValidator("eplus.command.create", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		Validator elevval = new ElevatorValidator(databaseManager, elevmsg, false);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		String name = args.get(0);
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, name);
		if(!validateExecution(data)) return;
			
		Elevator elevator = new Elevator(config, name);
		
		if(sender instanceof Player) {
			String world = ((Player) sender).getWorld().getName();
			elevator.setWorld(world);
			
			messages.sendFormatted(sender, "general.create.player", "%name%", name, "%world%", world);
		} else messages.sendFormatted(sender, "general.create.console", "%name%", name);
		
		databaseManager.createElevator(elevator);
	}
	
}
