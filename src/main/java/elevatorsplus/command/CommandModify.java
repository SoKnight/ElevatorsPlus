package elevatorsplus.command;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.command.type.ModifyOptionType;
import elevatorsplus.command.validation.ElevatorExecutionData;
import elevatorsplus.command.validation.ElevatorValidator;
import elevatorsplus.configuration.Config;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import elevatorsplus.mechanic.ElevatorSignRefresher;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.BaseExecutionData;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandModify extends ExtendedSubcommandExecutor {
	
	private final ElevatorsPlus plugin;
	private final Config config;
	private final Messages messages;
	
	public CommandModify(ElevatorsPlus plugin, Config config, Messages messages) {
		super(messages);
		
		this.plugin = plugin;
		this.config = config;
		this.messages = messages;
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		String elevmsg = messages.get("error.unknown-elevator");
		
		Validator permval = new PermissionValidator("eplus.command.modify", permmsg);
		Validator argsval = new ArgsCountValidator(4, argsmsg);
		Validator elevval = new ElevatorValidator(true, elevmsg);
		
		super.addValidators(permval, argsval, elevval);
	}

	@Override
	public void executeCommand(CommandSender sender, String[] args) {
		String name = args[1];
		
		DatabaseManager dbm = plugin.getDatabaseManager();
		
		CommandExecutionData data = new ElevatorExecutionData(sender, args, dbm, name);
		if(!validateExecution(data)) return;
		
		ModifyOptionType option = ModifyOptionType.valueOf(args[2].toUpperCase());
		if(option == null) {
			messages.getAndSend(sender,"modify.unknown-option");
			return;
		}
			
		Elevator elevator = dbm.getElevator(name);
		
		String none = messages.get("modify.none");
		
		switch (option) {
		case DEFHEIGHT: {
			int current = elevator.getDefaultLevelHeight();
			
			String arg = args[3];
			
			int height;
			try {
				height = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", arg);
				return;
			}

			if(height == current) {
				messages.sendFormatted(sender, "modify.defheight.already", "%value%", current);
				return;
			}

			elevator.setDefaultLevelHeight(height);
			dbm.updateElevator(elevator);

			String section = "modify.defheight.changed";
			messages.sendFormatted(sender, section, "%elevator%", name, "%old%", current, "%new%", height);
			break;
		}
		case LVLHEIGHT: {
			int count = elevator.getLevelsCount();

			if(args.length < 5) {
				messages.getAndSend(sender, "modify.lvlheight.wrong-syntax");
				return;
			}
			
			int level;
			try {
				level = Integer.parseInt(args[3]);
			} catch (NumberFormatException e1) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args[3]);
				return;
			}
			
			if(level < 1 || level > count) {
				messages.sendFormatted(sender, "modify.lvlheight.unknown", "%levels%", count);
				return;
			}
			
			int height;
			try {
				height = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args[3]);
				return;
			}

			int current = elevator.getLevelHeight(level);

			if(height == current) {
				messages.sendFormatted(sender, "modify.lvlheight.already", "%level%", level, "%value%", height);
				return;
			}

			elevator.setLevelHeight(level, height);
			dbm.updateElevator(elevator);

			String section = "modify.lvlheight.changed";
			messages.sendFormatted(sender, section, "%elevator%", name, "%level%", level, "%old%", current, "%new%", height);
			break;
		}
		case LVLSCOUNT: {
			int count = elevator.getLevelsCount();
			
			int newcount;
			try {
				newcount = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", args[3]);
				return;
			}

			if(count == newcount) {
				messages.sendFormatted(sender, "modify.lvlscount.already", "%value%", count);
				return;
			}

			elevator.setLevelsCount(newcount);
			dbm.updateElevator(elevator);
			
			String section = "modify.lvlscount.changed";
			messages.sendFormatted(sender, section, "%elevator%", name, "%old%", count, "%new%", newcount);
			break;
		}
		case NAME: {
			String newname = args[3];

			if(newname.equals(name)) {
				messages.sendFormatted(sender, "modify.name.already", "%elevator%", name);
				return;
			}
			
			if(dbm.getElevator(newname) != null) {
				messages.sendFormatted(sender, "modify.name.exist", "%name%", newname);
				return;
			}

			dbm.renameElevator(elevator, newname);
			
			String section = "modify.name.changed";
			messages.sendFormatted(sender, section, "%old%", name, "%new%", newname);
			break;
		}
		case SIGN: {
			World world = Bukkit.getWorld(elevator.getWorld());
			if(world == null) {
				messages.getAndSend(sender, "modify.sign.unknown-world");
				return;
			}
			
			if(args.length < 6) {
				messages.getAndSend(sender, "modify.sign.wrong-syntax");
				return;
			}
			
			String current = elevator.getSignLocation();
			int[] values = new int[3];
			
			for(int i = 0; i < 3; i++) {
				String arg = args[i + 3];
				int value;
				try {
					value = Integer.parseInt(arg);
				} catch (NumberFormatException e) {
					messages.sendFormatted(sender, "error.arg-is-not-int", "%arg%", arg);
					return;
				}
				
				values[i] = value;
			}

			TextLocation textloc = new TextLocation(values[0], values[1], values[2]);
			String value = textloc.getAsString();
			
			Elevator otherexist = dbm.getElevatorBySign(value);
			if(otherexist != null) {
				messages.sendFormatted(sender, "modify.sign.linked-other", "%elevator%", otherexist.getName());
				return;
			}
			
			if(current != null && value.equals(current)) {
				messages.getAndSend(sender, "modify.sign.already");
				return;
			}
			
			Location location = textloc.toLocation(world);
			
			Block block = world.getBlockAt(location);
			Material material = block.getType();
			
			if(!config.getSigns().contains(material)) {
				String matname = WordUtils.capitalize(material.name());
				messages.sendFormatted(sender, "modify.sign.cannot-be", "%material%", matname);
				return;
			}
			
			elevator.setSignLocation(value);
			dbm.updateElevator(elevator);
			
			ElevatorSignRefresher signRefresher = ElevatorsPlus.getInstance().getSignRefresher();
			signRefresher.refreshInformation(elevator, block);

			if(current == null) current = none;
			
			String section = "modify.sign.changed";
			messages.sendFormatted(sender, section, "%elevator%", name, "%old%", current, "%new%", value);
			break;
		}
		case WORLD: {
			String current = elevator.getWorld();
			String newworld = args[3];
			
			if(newworld.equalsIgnoreCase("#current"))
				if(sender instanceof Player) newworld = ((Player) sender).getWorld().getName();
				else {
					messages.getAndSend(sender, "modify.world.current-denied");
					return;
				}

			if(current != null && newworld.equals(current)) {
				messages.sendFormatted(sender, "modify.world.already", "%value%", current);
				return;
			}

			World world = Bukkit.getWorld(newworld);
			if(world == null) {
				messages.getAndSend(sender, "modify.world.unknown");
				return;
			}

			elevator.setWorld(newworld);
			dbm.updateElevator(elevator);
			
			if(current == null) current = none;
			
			String section = "modify.world.changed";
			messages.sendFormatted(sender, section, "%elevator%", name, "%old%", current, "%new%", newworld);
			break;
		}
		default:
			break;
		}
	}
	
	@Override
	public List<String> executeTabCompletion(CommandSender sender, String[] args) {
		if(args.length == 1 || args.length > 6) return null;
		
		CommandExecutionData data = new BaseExecutionData(sender, args);
		validateTabCompletion(data);
		
		List<String> output = new ArrayList<>();
		
		String name = args[1];
		
		DatabaseManager dbm = plugin.getDatabaseManager();
		
		if(args.length == 2) {
			List<String> elevators = dbm.getAllNames();
		
			elevators.stream()
				.filter(s -> s.toLowerCase().startsWith(name.toLowerCase()))
				.forEach(e -> output.add(e));
		} else {
			Elevator elevator = dbm.getElevator(name);
			if(elevator == null) return null;
			
			if(args.length == 3) {
				List<String> options = ModifyOptionType.getValues();
				
				options.stream()
					.filter(s -> s.toLowerCase().startsWith(args[2].toLowerCase()))
					.forEach(o -> output.add(o));
			} else {
				ModifyOptionType option = ModifyOptionType.valueOf(args[2].toUpperCase());
				if(option == null) return null;
				
				List<String> values = new ArrayList<>();
				
				switch(option) {
				case LVLHEIGHT:
					if(args.length != 4) return null;
					
					int count = elevator.getLevelsCount();
					
					if(count == 1) output.add("1");
					else
						for(int i = 1; i <= count; i++)
							output.add(String.valueOf(i));
					break;
				case SIGN:
					if(sender instanceof Player) {
						Player player = (Player) sender;
						
						Block block = player.getTargetBlockExact(10);
						if(block == null) break;
						
						Location location = block.getLocation();
						
						switch (args.length) {
						case 4:
							output.add(String.valueOf(location.getBlockX()));
							break;
						case 5:
							output.add(String.valueOf(location.getBlockY()));
							break;
						case 6:
							output.add(String.valueOf(location.getBlockZ()));
							break;
						default:
							break;
						}
					}
					break;
				case WORLD:
					if(args.length != 4) return null;
					
					if(sender instanceof Player) values.add("#current");
					Bukkit.getWorlds().forEach(w -> values.add(w.getName()));
					
					String arg = args[3].toLowerCase();
					values.stream()
						.filter(s -> s.toLowerCase().startsWith(arg))
						.forEach(v -> output.add(v));
					
					break;
				default:
					break;
				}
			}
		}
		
		return output;
	}
	
}
