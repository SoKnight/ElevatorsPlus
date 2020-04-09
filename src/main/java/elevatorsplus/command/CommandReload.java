package elevatorsplus.command;

import org.bukkit.command.CommandSender;

import elevatorsplus.ElevatorsPlus;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandReload extends ExtendedSubcommandExecutor {

	private final ElevatorsPlus plugin;
	private final Messages messages;
	
	public CommandReload(ElevatorsPlus plugin, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("eplus.command.reload", permmsg);
		
		super.addValidators(permval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		if(!validateExecution(sender, args)) return;
		
		plugin.setConfigs();
		plugin.registerCommands();
		plugin.registerMovingOperators();
		
		messages.getAndSend(sender, "general.reloaded");
	}
	
}
