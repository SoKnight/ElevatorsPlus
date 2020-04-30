package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.tool.CollectionsTool;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandList extends ExtendedSubcommandExecutor {

	private final DatabaseManager databaseManager;
	private final Config config;
	private final Messages messages;
	
	private final String header, body, footer;
	
	public CommandList(DatabaseManager databaseManager, Config config, Messages messages) {
		super(messages);
		
		this.databaseManager = databaseManager;
		this.config = config;
		this.messages = messages;
		
		this.header = messages.get("list.header");
		this.body = messages.get("list.body");
		this.footer = messages.get("list.footer");
		
		String permmsg = messages.get("error.no-permissions");
		
		Validator permval = new PermissionValidator("eplus.command.list", permmsg);
		
		super.addValidators(permval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
		
		int page = 1;
		if(!args.isEmpty())
			try {
				page = Integer.parseInt(args.get(0));
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args.get(0));
				return;
			}
		
		List<Elevator> elevators = databaseManager.getAllElevators();
		if(elevators.isEmpty()) {
			messages.getAndSend(sender, "list.not-found");
			return;
		}
		
		int size = config.getInt("messages.list-size");
		List<Elevator> onpage = CollectionsTool.getSubList(elevators, size, page);
		
		if(onpage.isEmpty()) {
			messages.sendFormatted(sender, "list.page-is-empty", "%page%", page);
			return;
		}
		
		int total = elevators.size() / size;
		if(elevators.size() % size != 0) total++;
		
		String none = messages.get("list.none");
		List<String> output = new ArrayList<>();
		
		onpage.forEach(e -> {
			String name = e.getName();
			String world = e.getWorld();
			int current = e.getCurrentLevel();
			int levels = e.getLevelsCount();
			
			if(world == null) world = none;
			
			String message = messages.format(this.body, "%elevator%", name, "%current%", current,
					"%levels%", levels, "%world%", world);
			output.add(message);
		});
		
		String header = messages.format(this.header, "%page%", page, "%total%", total);
		
		messages.send(sender, header);
		output.forEach(s -> messages.send(sender, s));
		messages.send(sender, this.footer);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.size() != 1 || !validateTabCompletion(sender, args)) return null;
		
		List<String> elevators = databaseManager.getAllNames();
		if(elevators.isEmpty()) return null;
		
		List<String> output = new ArrayList<>();
		
		int pages = elevators.size() / 10;
		if(elevators.size() % 10 > 1) pages++;
		
		for(int i = 1; i <= pages; i++)
			output.add(String.valueOf(i));
		
		return output;
	}
	
}
