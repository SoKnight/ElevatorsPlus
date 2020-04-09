package elevatorsplus.command;

import org.bukkit.command.CommandSender;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandReload extends ExtendedSubcommandExecutor {

	private final ElevatorsPlus plugin;
	private final Config config;
	private final Messages messages;
	
	public CommandReload(ElevatorsPlus plugin, Config config, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		
		this.config = config;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("eplus.command.reload", permmsg);
		
		super.addValidators(permval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		if(!validateExecution(sender, args)) return;
		
		config.refresh();
		messages.refresh();
		
		plugin.registerCommands();
		plugin.registerMovingOperators();
		
		messages.getAndSend(sender, "general.reloaded");
	}
	
}
