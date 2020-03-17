package elevatorsplus.commands;

import org.bukkit.command.CommandSender;

import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.utils.Logger;

public class CommandReload extends AbstractSubCommand {

	private CommandSender sender;
	
	public CommandReload(CommandSender sender) {
		super(sender, null, "eplus.command.reload", 1);
		this.sender = sender;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		Config.refresh();
		Messages.refresh();
		
		sendMessage("reload-success");
		
		// Debug message
		Logger.debug("Plugin reloaded by user " + sender.getName());
	}
	
}
