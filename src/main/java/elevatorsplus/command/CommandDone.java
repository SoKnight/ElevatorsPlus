package elevatorsplus.command;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.listener.session.SelectionSession;
import elevatorsplus.listener.session.SessionManager;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.SenderIsPlayerValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandDone extends ExtendedSubcommandExecutor {

	private final ElevatorsPlus plugin;
	private final Messages messages;
	
	public CommandDone(ElevatorsPlus plugin, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String isplmsg = messages.get("error.only-for-players");
		
		Validator permval = new PermissionValidator("eplus.command.done", permmsg);
		Validator isplval = new SenderIsPlayerValidator(isplmsg);
		
		super.addValidators(permval, isplval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		if(!validateExecution(sender, args)) return;
		
		Player player = (Player) sender;
		String name = player.getName();
		
		SessionManager sm = plugin.getSessionManager();
		SelectionSession session = sm.getSelectionSession(name);
		
		if(sm.doneSelectionSession(name)) {
			String elevator = session.getElevator();
			messages.sendFormatted(sender, "selection.done.success", "%elevator%", elevator);
		} else messages.getAndSend(sender, "selection.done.no-session");
	}
	
}
