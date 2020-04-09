package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;

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

public class CommandInfo extends ExtendedSubcommandExecutor {

	private final Messages messages;
	private final ElevatorsPlus plugin;
	
	private final String header, footer;
	private final ConfigurationSection section;
	
	public CommandInfo(ElevatorsPlus plugin, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.messages = messages;
		
		this.header = messages.get("info.header");
		this.section = messages.getFileConfig().getConfigurationSection("info.list");
		this.footer = messages.get("info.footer");
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.info", permmsg);
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
		
		String world = elevator.getWorld();
		int current = elevator.getCurrentLevel();
		int levels = elevator.getLevelsCount();
		int callbuttons = elevator.getCallbuttons().size();
		int doors = elevator.getDoors().size();
		int platform = elevator.getPlatform().size();
		String sign = elevator.getSignLocation();
		
		String none = messages.get("info.none");
		
		if(world == null) world = none;
		if(sign == null) sign = none;
		
		messages.send(sender, header);
		
		if(section.contains("name"))
			messages.sendFormatted(sender, "info.list.name", "%name%", name);
		if(section.contains("world"))
			messages.sendFormatted(sender, "info.list.world", "%world%", world);
		if(section.contains("current"))
			messages.sendFormatted(sender, "info.list.current", "%current%", current);
		if(section.contains("levels"))
			messages.sendFormatted(sender, "info.list.levels", "%levels%", levels);
		if(section.contains("callbuttons"))
			messages.sendFormatted(sender, "info.list.callbuttons", "%callbuttons%", callbuttons);
		if(section.contains("doors"))
			messages.sendFormatted(sender, "info.list.doors", "%doors%", doors);
		if(section.contains("platform"))
			messages.sendFormatted(sender, "info.list.platform", "%platform%", platform);
		if(section.contains("sign"))
			messages.sendFormatted(sender, "info.list.sign", "%sign%", sign);
		
		messages.send(sender, footer);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, String[] args) {
		if(args.length != 2) return null;
		
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
