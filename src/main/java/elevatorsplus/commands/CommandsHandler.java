package elevatorsplus.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Messages;
import elevatorsplus.listeners.SessionManager;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandsHandler implements CommandExecutor {

	private final SessionManager sessionManager;
	private final DatabaseManager dbm;
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) {
			sender.sendMessage(Messages.getMessage("error-no-args"));
			return true;
		}
		
		switch(args[0]) {
		case "help":
			new CommandHelp(sender, args).execute();
			break;
		case "create":
			new CommandCreate(sender, args, dbm).execute();
			break;
		case "delete":
			new CommandDelete(sender, args, dbm).execute();
			break;
		case "modify":
			new CommandModify(sender, args, dbm).execute();
			break;
		case "info":
			new CommandInfo(sender, args, dbm).execute();
			break;
		case "list":
			new CommandList(sender, args, dbm).execute();
			break;
		case "done":
			new CommandDone(sender, sessionManager).execute();
			break;
		case "selection":
			new CommandSelection(sender, args, sessionManager).execute();
			break;
		case "reload":
			new CommandReload(sender).execute();
			break;
		case "controls":
			new CommandControls(sender, args, dbm).execute();
			break;
		default:
			sender.sendMessage(Messages.getMessage("error-command-not-found"));
			break;
		}
		return true;
	}

}
