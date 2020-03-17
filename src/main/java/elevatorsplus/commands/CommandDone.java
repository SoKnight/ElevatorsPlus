package elevatorsplus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.listeners.SessionManager;
import elevatorsplus.objects.sessions.SelectionSession;
import elevatorsplus.utils.Logger;

public class CommandDone extends AbstractSubCommand {

	private final CommandSender sender;
	private final SessionManager sessionManager;
	
	public CommandDone(CommandSender sender, SessionManager sessionManager) {
		super(sender, null, "eplus.command.done", 1);
		this.sender = sender;
		this.sessionManager = sessionManager;
	}

	@Override
	public void execute() {
		if(!isPlayerRequired()) return;
		if(!hasPermission()) return;
		
		Player player = (Player) sender;
		String name = player.getName();
		
		SelectionSession session = sessionManager.getSelectionSession(name);
		String elevator = session.getElevator();
		
		if(sessionManager.doneSelectionSession(name))
			sendMessage("done-success", "%elevator%", elevator);
		else sendMessage("done-failed-no-session");
		
		// Debug message
		Logger.debug("User " + sender.getName() + " finish selection session for elevator '" + elevator + "'");
	}
	
}
