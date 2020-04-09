package elevatorsplus.command;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import ru.soknight.lib.command.AbstractSubcommandsHandler;
import ru.soknight.lib.configuration.Messages;

public class SubcommandHandler extends AbstractSubcommandsHandler {
	
	public SubcommandHandler(ElevatorsPlus plugin, Config config, Messages messages) {
		
		super(messages);
		
		super.setExecutor("help", new CommandHelp(messages));
		super.setExecutor("create", new CommandCreate(plugin, config, messages));
		super.setExecutor("delete", new CommandDelete(plugin, messages));
		super.setExecutor("modify", new CommandModify(plugin, config, messages));
		super.setExecutor("info", new CommandInfo(plugin, messages));
		super.setExecutor("list", new CommandList(plugin, config, messages));
		super.setExecutor("reload", new CommandReload(plugin, config, messages));
		super.setExecutor("done", new CommandDone(plugin, messages));
		super.setExecutor("selection", new CommandSelection(plugin, messages));
		super.setExecutor("elements", new CommandElements(plugin, config, messages));
	}

}
