package elevatorsplus.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.StringUtils;

public class CommandInfo extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;

	public CommandInfo(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "eplus.command.info", 2);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String name = args[1];
		if(!isElevatorExist(name)) return;
		
		Elevator elevator = dbm.getElevator(name);
		
		String world = elevator.getWorld();
		int current = elevator.getCurrentFloor();
		int count = elevator.getFloorsCount();
		int callbuttons = elevator.getCallButtons().size();
		int platformblocks = elevator.getPlatformBlocks().size();
		String signlocation = elevator.getSignLocation();
		
		List<String> info = new ArrayList<>();
		for(String s : Messages.getMessagesList("info-list"))
			info.add(StringUtils.format(s, "%name%", name, "%world%", world, "%current_floor%", current,
					"%floors_count%", count, "%call_buttons%", callbuttons, "%platform_blocks%", platformblocks,
					"%sign_location%", signlocation));
		
		sendMessage("info-header");
		info.forEach(s -> sender.sendMessage(s));
		sendMessage("info-footer");
		return;
	}
	
}
