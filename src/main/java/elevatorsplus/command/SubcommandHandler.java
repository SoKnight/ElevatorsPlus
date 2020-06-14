package elevatorsplus.command;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.mechanic.tool.ElevatorSignRefresher;
import ru.soknight.lib.command.AbstractSubcommandsHandler;
import ru.soknight.lib.configuration.Messages;

public class SubcommandHandler extends AbstractSubcommandsHandler {
	
	public SubcommandHandler(ElevatorsPlus plugin, DatabaseManager databaseManager, Config config,
			SessionManager sessionManager, ElevatorSignRefresher signRefresher, Messages messages) {
		
		super(messages);
		
		super.setExecutor("help", new CommandHelp(messages));
		super.setExecutor("create", new CommandCreate(databaseManager, config, messages));
		super.setExecutor("delete", new CommandDelete(databaseManager, messages));
		super.setExecutor("modify", new CommandModify(databaseManager, signRefresher, config, messages));
		super.setExecutor("info", new CommandInfo(databaseManager, messages));
		super.setExecutor("list", new CommandList(databaseManager, config, messages));
		super.setExecutor("reload", new CommandReload(plugin, messages));
		super.setExecutor("done", new CommandDone(sessionManager, messages));
		super.setExecutor("selection", new CommandSelection(databaseManager, sessionManager, messages));
		super.setExecutor("elements", new CommandElements(databaseManager, config, messages));
		super.setExecutor("gravityfix", new CommandGravityfix(messages));
	}

}
