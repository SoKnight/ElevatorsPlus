package elevatorsplus.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandCreate extends ExtendedSubcommandExecutor {
	
	private final ElevatorsPlus plugin;
	private final Config config;
	private final Messages messages;
	
	public CommandCreate(ElevatorsPlus plugin, Config config, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.config = config;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("general.create.exist");
		
		Validator permval = new PermissionValidator("eplus.command.create", permmsg);
		Validator argsval = new ArgsCountValidator(2, argsmsg);
		Validator elevval = new ElevatorValidator(false, elevmsg);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		String name = args[1];
		
		DatabaseManager dbm = plugin.getDatabaseManager();
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, dbm, name);
		if(!validateExecution(data)) return;
			
		Elevator elevator = new Elevator(config, name);
		
		if(sender instanceof Player) {
			String world = ((Player) sender).getWorld().getName();
			elevator.setWorld(world);
			
			messages.sendFormatted(sender, "general.create.player", "%name%", name, "%world%", world);
		} else messages.sendFormatted(sender, "general.create.console", "%name%", name);
		
		dbm.createElevator(elevator);
	}
	
}
