package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.listener.ElementType;
import elevatorsplus.listener.session.SelectionSession;
import elevatorsplus.listener.session.SessionManager;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandSelection extends ExtendedSubcommandExecutor {

	private final DatabaseManager databaseManager;
	private final SessionManager sessionManager;
	private final Messages messages;
	
	public CommandSelection(DatabaseManager databaseManager, SessionManager sessionManager, Messages messages) {
		super(messages);
		
		this.databaseManager = databaseManager;
		this.sessionManager = sessionManager;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String isplmsg = messages.get("error.only-for-players");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.selection", permmsg);
		Validator isplval = new SenderIsPlayerValidator(isplmsg);
		Validator argsval = new ArgsCountValidator(2, argsmsg);
		Validator elevval = new ElevatorValidator(databaseManager, elevmsg);
		
		super.addValidators(permval, isplval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		String elevator = args.get(0);
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, elevator);
		if(!validateExecution(data)) return;
		
		ElementType control = ElementType.valueOf(args.get(1).toUpperCase());
		if(control == null) {
			messages.getAndSend(sender, "selection.start.unknown-type");
			return;
		}
		
		Player player = (Player) sender;
		String name = player.getName();
		
		if(sessionManager.startSelectionSession(name, elevator, control))
			messages.sendFormatted(sender, "selection.start.success", "%elevator%", elevator);
		else {
			SelectionSession session = sessionManager.getSelectionSession(name);
			String selevator = session.getElevator();
			messages.sendFormatted(sender, "selection.start.already", "%elevator%", selevator);
			return;
		}
		
		String path = "selection.tip." + control.toString().toLowerCase();
		if(messages.getFileConfig().contains(path) && messages.getFileConfig().isList(path)) {
			List<String> tip = messages.getColoredList(path);
			tip.forEach(s -> messages.send(sender, s));
		}
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.isEmpty() || args.size() > 2 || !validateTabCompletion(sender, args)) return null;
		
		List<String> output = new ArrayList<>();
		
		String name = args.get(0);
		
		if(args.size() == 1) {
			List<String> elevators = databaseManager.getAllNames();
		
			elevators.stream()
				.filter(s -> s.toLowerCase().startsWith(name.toLowerCase()))
				.forEach(e -> output.add(e));
		} else {
			Elevator elevator = databaseManager.getElevator(name);
			if(elevator == null) return null;
			
			List<String> elements = ElementType.getSelectionValues();
				
			elements.stream()
				.filter(s -> s.toLowerCase().startsWith(args.get(1).toLowerCase()))
				.forEach(o -> output.add(o));
		}
		
		return output;
	}
	
}
