package elevatorsplus.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.enums.Control;
import elevatorsplus.files.Messages;
import elevatorsplus.listeners.SessionManager;
import elevatorsplus.objects.sessions.SelectionSession;
import elevatorsplus.utils.Logger;

public class CommandSelection extends AbstractSubCommand {

	private final CommandSender sender;
	private final String[] args;
	private final SessionManager sessionManager;
	
	public CommandSelection(CommandSender sender, String[] args, SessionManager sessionManager) {
		super(sender, args, "eplus.command.selection", 3);
		this.sender = sender;
		this.args = args;
		this.sessionManager = sessionManager;
	}

	@Override
	public void execute() {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String elevator = args[1];
		if(!isElevatorExist(elevator));
		
		Control control = Control.valueOf(args[2].toUpperCase());
		if(control == null) {
			sendMessage("selection-failed-unknown-controls");
			return;
		}
		
		Player player = (Player) sender;
		String name = player.getName();
		
		if(sessionManager.startSelectionSession(name, elevator, control))
			sendMessage("selection-success", "%elevator%", elevator);
		else {
			SelectionSession session = sessionManager.getSelectionSession(name);
			String selevator = session.getElevator();
			sendMessage("selection-failed-already", "%elevator%", selevator);
		}
		
		String path = "selection-tip-" + control.toString().toLowerCase();
		if(Messages.getConfig().isSet(path) && Messages.getConfig().isList(path)) {
			List<String> tip = Messages.getMessagesList(path);
			tip.forEach(s -> player.sendMessage(s));
		}
	
		// Debug message
		Logger.debug("User " + sender.getName() + " start new selection session for elevator '" + elevator + "'");
	}
	
}
