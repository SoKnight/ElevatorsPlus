package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;

import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.listener.ElementType;
import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.tool.CollectionsTool;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandElements extends ExtendedSubcommandExecutor {

	private final DatabaseManager databaseManager;
	private final Config config;
	private final Messages messages;
	
	public CommandElements(DatabaseManager databaseManager, Config config, Messages messages) {
		super(messages);
		
		this.databaseManager = databaseManager;
		this.config = config;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.elements", permmsg);
		Validator argsval = new ArgsCountValidator(2, argsmsg);
		Validator elevval = new ElevatorValidator(databaseManager, elevmsg);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		String name = args.get(0);
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, name);
		if(!validateExecution(data)) return;
		
		ElementType element = ElementType.valueOf(args.get(1).toUpperCase());
		if(element == null) {
			messages.getAndSend(sender, "elements.unknown-type");
			return;
		}
		
		Elevator elevator = databaseManager.getElevator(name);
		
		int page = 1;
		if(args.size() > 2)
			try {
				page = Integer.parseInt(args.get(2));
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args.get(2));
				return;
			}
		
		String elementName = element.toString().toLowerCase();
		
		String header = messages.get("elements." + elementName + ".header");
		String body = messages.get("elements." + elementName + ".body");
		String footer = messages.get("elements." + elementName + ".footer");
		
		int size = config.getInt("messages.list-size");
		List<String> output = new ArrayList<>();
		
		switch (element) {
		case CALLBUTTONS: {
			Map<String, Integer> callbuttons = elevator.getCallbuttons();
			if(callbuttons.isEmpty()) {
				messages.getAndSend(sender, "elements.callbuttons.not-found");
				return;
			}
			
			Map<String, Integer> onpage = CollectionsTool.getSubMap(callbuttons, size, page);
			if(onpage.isEmpty()) {
				messages.sendFormatted(sender, "elements.callbuttons.empty-page", "%page%", page);
				return;
			}

			int pages = callbuttons.size() / size;
			if(callbuttons.size() % size != 0) pages++;
			
			header = messages.format(header, "%page%", page, "%total%", pages);
			
			onpage.forEach((k, v) -> {
				String[] loc = k.split(" ");
				output.add(messages.format(body, "%x%", loc[0], "%y%", loc[1], "%z%", loc[2], "%level%", v));
			});
			break;
		}
		case DOORS: {
			Map<String, Integer> doors = elevator.getDoors();
			if(doors.isEmpty()) {
				messages.getAndSend(sender, "elements.doors.not-found");
				return;
			}
			
			Map<String, Integer> onpage = CollectionsTool.getSubMap(doors, size, page);
			if(onpage.isEmpty()) {
				messages.sendFormatted(sender, "elements.doors.empty-page", "%page%", page);
				return;
			}

			int pages = doors.size() / size;
			if(doors.size() % size != 0) pages++;
			
			header = messages.format(header, "%page%", page, "%total%", pages);
			
			onpage.forEach((k, v) -> {
				String[] loc = k.split(" ");
				output.add(messages.format(body, "%x%", loc[0], "%y%", loc[1], "%z%", loc[2], "%level%", v));
			});
			break;
		}
		case LVLSHEIGHTS: {
			Map<Integer, Integer> lvlheights = elevator.getLvlsheights();
			if(lvlheights.isEmpty()) {
				messages.getAndSend(sender, "elements.lvlsheights.not-found");
				return;
			}
			
			Map<Integer, Integer> onpage = CollectionsTool.getSubMap(lvlheights, size, page);
			if(onpage.isEmpty()) {
				messages.sendFormatted(sender, "elements.lvlsheights.empty-page", "%page%", page);
				return;
			}

			int pages = lvlheights.size() / size;
			if(lvlheights.size() % size != 0) pages++;
			
			header = messages.format(header, "%page%", page, "%total%", pages);
			
			onpage.forEach((k, v) -> output.add(messages.format(body, "%level%", k, "%height%", v)));
			break;
		}
		case PLATFORM: {
			Map<String, Material> platform = elevator.getPlatform();
			if(platform.isEmpty()) {
				messages.getAndSend(sender, "elements.platform.not-found");
				return;
			}
			
			Map<String, Material> onpage = CollectionsTool.getSubMap(platform, size, page);
			if(onpage.isEmpty()) {
				messages.sendFormatted(sender, "elements.platform.empty-page", "%page%", page);
				return;
			}

			int pages = platform.size() / size;
			if(platform.size() % size != 0) pages++;
			
			header = messages.format(header, "%page%", page, "%total%", pages);
			
			onpage.forEach((key, material) -> {
				String[] loc = key.split(" ");
				String m = WordUtils.capitalize(material.toString().toLowerCase()).replace("_", " ");
				output.add(messages.format(body, "%x%", loc[0], "%y%", loc[1], "%z%", loc[2], "%material%", m));
			});
			break;
		}
		default:
			return;
		}
		
		messages.send(sender, header);
		output.forEach(s -> messages.send(sender, s));
		messages.send(sender, footer);
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, CommandArguments args) {
		if(args.size() > 2) return null;
		
		validateTabCompletion(sender, args);
		
		List<String> output = new ArrayList<>();
		
		String name = args.get(0);
		
		if(args.size() == 1) {
			List<String> elevators = databaseManager.getAllNames();
		
			elevators.stream()
				.filter(s -> s.toLowerCase().startsWith(name.toLowerCase()))
				.forEach(e -> output.add(e));
		} else {
			Elevator elevator = databaseManager.getElevator(name);
			if(elevator == null) return null;
				
			List<String> elements = ElementType.getSelectionValues();
				
			elements.stream()
				.filter(s -> s.toLowerCase().startsWith(args.get(1).toLowerCase()))
				.forEach(o -> output.add(o));
		}
		
		return output;
	}
	
}
