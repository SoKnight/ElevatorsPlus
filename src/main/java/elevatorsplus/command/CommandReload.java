package elevatorsplus.command;

import org.bukkit.command.CommandSender;

import elevatorsplus.ElevatorsPlus;
import ru.soknight.lib.argument.CommandArguments;
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
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		long start = System.currentTimeMillis();
		
		plugin.refresh();
		
		long time = System.currentTimeMillis() - start;
		
		messages.sendFormatted(sender, "general.reloaded", "%time%", time);
	}
	
}
