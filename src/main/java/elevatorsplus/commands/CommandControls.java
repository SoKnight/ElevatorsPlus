package elevatorsplus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.ListUtils;
import elevatorsplus.utils.StringUtils;

public class CommandControls extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	private static final List<String> controls = Arrays.asList("callbuttons", "platform", "floorheights");
	
	public CommandControls(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "eplus.command.controls", 3);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!isCorrectUsage()) return;
		if(!hasPermission()) return;
		
		String elevatorName = args[1];
		if(!isElevatorExist(elevatorName)) return;
		
		String controls = args[2].toLowerCase();
		if(!CommandControls.controls.contains(controls)) {
			sendMessage("controls-failed-unknown-controls");
			return;
		}
		
		Elevator elevator = dbm.getElevator(elevatorName);
		
		int page = 1;
		if(args.length > 3)
			if(!argIsInteger(args[3])) return;
			else page = Integer.parseInt(args[3]);
		
		String header = Messages.getMessage("controls-" + controls + "-header");
		String body = Messages.getMessage("controls-" + controls + "-body");
		String footer = Messages.getMessage("controls-" + controls + "-footer");
		
		int size = Config.getConfig().getInt("messages.list-size");
		List<String> output = new ArrayList<>();
		
		switch (controls) {
		case "callbuttons": {
			Map<String, Integer> callbuttons = elevator.getCallButtons();
			
			if(callbuttons.isEmpty()) {
				sendMessage("controls-callbuttons-not-found");
				return;
			}
			
			Map<String, Integer> onpage = ListUtils.getCallbuttonsSubMap(callbuttons, size, page);

			if(onpage.isEmpty()) {
				sendMessage("controls-callbuttons-empty-page", "%page%", page);
				return;
			}

			int pages = callbuttons.size() / size;
			if(callbuttons.size() % size != 0) pages++;
			
			header = StringUtils.format(header, "%page%", page, "%max_page%", pages);
			
			onpage.forEach((k, v) -> {
				String[] loc = k.split(" ");
				output.add(StringUtils.format(body, "%x%", loc[0], "%y%", loc[1], "%z%", loc[2], "%floor%", v));
			});
			break;
		}
		case "platform": {
			Map<String, Material> platform = elevator.getPlatformBlocks();
			
			if(platform.isEmpty()) {
				sendMessage("controls-platform-not-found");
				return;
			}
			
			Map<String, Material> onpage = ListUtils.getPlatformSubMap(platform, size, page);

			if(onpage.isEmpty()) {
				sendMessage("controls-platform-empty-page", "%page%", page);
				return;
			}

			int pages = platform.size() / size;
			if(platform.size() % size != 0) pages++;
			
			header = StringUtils.format(header, "%page%", page, "%max_page%", pages);
			
			onpage.forEach((key, material) -> {
				String[] loc = key.split(" ");
				String m = StringUtils.capitalizeFirst(material.toString()).replace("_", " ");
				output.add(StringUtils.format(body, "%x%", loc[0], "%y%", loc[1], "%z%", loc[2], "%material%", m));
			});
			break;
		}
		case "floorheights": {
			Map<Integer, Integer> floorheights = elevator.getFloorHeights();
			
			if(floorheights.isEmpty()) {
				sender.sendMessage(Messages.getMessage("controls-floorheights-not-found"));
				return;
			}
			
			Map<Integer, Integer> onpage = ListUtils.getFloorheightsSubMap(floorheights, size, page);

			if(onpage.isEmpty()) {
				sendMessage("controls-floorheights-empty-page", "%page%", page);
				return;
			}

			int pages = floorheights.size() / size;
			if(floorheights.size() % size != 0) pages++;
			
			header = StringUtils.format(header, "%page%", page, "%max_page%", pages);
			
			onpage.forEach((k, v) -> output.add(StringUtils.format(body, "%floor%", k, "%height%", v)));
			break;
		}
		default:
			return;
		}
		
		sender.sendMessage(header);
		output.forEach(s -> sender.sendMessage(s));
		sender.sendMessage(footer);
		return;
	}
	
}
