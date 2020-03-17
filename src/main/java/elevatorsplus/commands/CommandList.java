package elevatorsplus.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.ListUtils;
import elevatorsplus.utils.StringUtils;

public class CommandList extends AbstractSubCommand {

	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	public CommandList(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, null, "eplus.command.list", 1);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}
	
	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		int page = 1;
		if(args.length > 1)
			if(!argIsInteger(args[1])) return;
			else page = Integer.parseInt(args[1]);
		
		List<Elevator> elevators = dbm.getAllElevators();
		if(elevators.isEmpty()) {
			sendMessage("list-failed-not-found");
			return;
		}
		
		int size = Config.getConfig().getInt("messages.list-size");
		List<Elevator> onpage = ListUtils.getElevatorsOnPage(elevators, size, page);
		
		if(onpage.isEmpty()) {
			sendMessage("list-failed-page-is-empty", "%page%", page);
			return;
		}
		
		int pages = elevators.size() / size;
		if(elevators.size() % size != 0) pages++;
		
		String body = Messages.getMessage("list-body");
		
		List<String> output = new ArrayList<>();
		onpage.forEach(e -> {
			String message = StringUtils.format(body, "%elevator%", e.getName(), "%current_floor%",
					e.getCurrentFloor(), "%floors_count%", e.getFloorsCount(), "%world%", e.getWorld());
			output.add(message);
		});
		
		sendMessage("list-header", "%page%", page, "%max_page%", pages);
		output.forEach(s -> sender.sendMessage(s));
		sendMessage("list-footer");
		return;
	}
	
}
