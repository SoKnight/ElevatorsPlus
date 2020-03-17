package elevatorsplus.commands;

import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.objects.Elevator;
import elevatorsplus.objects.TextLocation;
import elevatorsplus.utils.Logger;
import elevatorsplus.utils.StringUtils;

public class CommandModify extends AbstractSubCommand {
	
	private final CommandSender sender;
	private final String[] args;
	private final DatabaseManager dbm;
	
	private static final List<String> options = Arrays.asList("name", "world", "sign", "floorscount",
			"floorheight", "defaultheight");
	
	public CommandModify(CommandSender sender, String[] args, DatabaseManager dbm) {
		super(sender, args, "eplus.command.modify", 4);
		this.sender = sender;
		this.args = args;
		this.dbm = dbm;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		if(!isCorrectUsage()) return;
		
		String name = args[1];
		if(!isElevatorExist(name)) {
			sendMessage("create-failed-exist", "%name%", name);
			return;
		}
		
		String option = args[2];
		if(!options.contains(option)) {
			sendMessage("modify-unknown-option");
			return;
		}
			
		Elevator elevator = dbm.getElevator(name);
		String oldvalue, newvalue = null;
		int changed = -1;
		
		switch (option) {
		case "name": {
			String newname = args[3];
			
			if(!isElevatorNotExist(newname)) return;
			
			if(newname.equals(name)) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newname);
				return;
			}
			
			changed = dbm.renameElevator(elevator, newname);
			sendMessage("modify-renamed", "%old%", name, "%new%", newname);
			
			// Debug message
			Logger.debug("Elevator '" + name + "' renamed to '" + newname +  "' by user " + sender.getName(), changed);
			return;
		}
		case "world": {
			oldvalue = elevator.getWorld();
			newvalue = args[3];
			
			if(newvalue.equals("#current"))
				if(sender instanceof Player) newvalue = ((Player) sender).getWorld().getName();
				else {
					sendMessage("modify-denied-world-current");
					return;
				}
			
			if(newvalue.equals(oldvalue)) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newvalue);
				return;
			}
			
			World world = Bukkit.getWorld(newvalue);
			if(world == null) {
				sendMessage("modify-unknown-world");
				return;
			}
			
			elevator.setWorld(newvalue);
			changed = dbm.updateElevator(elevator);
			
			sendMessage("modify-success", "%option%", option, "%old%", oldvalue, "%new%", newvalue, "%elevator%", name);
			break;
		}
		case "sign": {
			oldvalue = elevator.getSignLocation();
			int[] values = new int[3];
			
			// Checking specified location
			for(int i = 3; i < 6; i++) {
				if(args.length < i + 1) {
					sendMessage("modify-wrong-location");
					return;
				}
				
				String arg = args[i];
				if(!argIsInteger(arg)) return;
				
				int value = Integer.parseInt(arg);
				values[i - 3] = value;
			}
			
			TextLocation textloc = new TextLocation(values[0], values[1], values[2]);
			newvalue = textloc.getAsString();
			
			World world = Bukkit.getWorld(elevator.getWorld());
			if(world == null) {
				sendMessage("modify-unknown-world");
				return;
			}
			
			Location location = textloc.toLocation(world);
			Material material = world.getBlockAt(location).getType();
			String materialName = StringUtils.capitalizeFirst(material.name());
			
			if(!Config.getSigns().contains(material)) {
				sendMessage("modify-is-not-sign", "%material%", materialName);
				return;
			}
			
			if(newvalue.equals(oldvalue)) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newvalue);
				return;
			}
			
			elevator.setSignLocation(newvalue);
			changed = dbm.updateElevator(elevator);
			
			sendMessage("modify-success", "%option%", option, "%old%", oldvalue, "%new%", newvalue, "%elevator%", name);
			break;
		}
		case "floorscount": {
			oldvalue = String.valueOf(elevator.getFloorsCount());
			newvalue = args[3];
			
			if(!argIsInteger(newvalue)) return;
			
			if(newvalue.equals(oldvalue)) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newvalue);
				return;
			}
			
			int count = Integer.parseInt(newvalue);
			elevator.setFloorsCount(count);
			changed = dbm.updateElevator(elevator);
			
			sendMessage("modify-success", "%option%", option, "%old%", oldvalue, "%new%", newvalue, "%elevator%", name);
			break;
		}
		case "floorheight": {
			int count = elevator.getFloorsCount();
			
			if(args.length < 5) {
				sendMessage("modify-wrong-floorheight");
				return;
			}
			
			if(!argIsInteger(args[3]) || !argIsInteger(args[4])) return;
			
			int floor = Integer.parseInt(args[3]);
			
			// Checking for matching with interval [1;countnow]
			if(floor < 1 || floor > count) {
				sendMessage("modify-unknown-floor", "%floors%", count);
				return;
			}
			
			int height = Integer.parseInt(args[4]);
			int currentHeight = elevator.getFloorHeight(floor);
			
			oldvalue = floor + " = " + currentHeight;
			newvalue = floor + " = " + height;
			
			// Checking height for matching with current height
			if(height == currentHeight) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newvalue);
				return;
			}
			
			elevator.setFloorHeight(floor, height);
			changed = dbm.updateElevator(elevator);
			
			sendMessage("modify-success", "%option%", option, "%old%", oldvalue, "%new%", newvalue, "%elevator%", name);
			break;
		}
		case "defaultheight": {
			int current = elevator.getDefaultFloorHeight();
			
			oldvalue = String.valueOf(current);
			newvalue = args[3];
			
			if(!argIsInteger(newvalue)) return;
			int height = Integer.parseInt(newvalue);
			
			if(height == current) {
				sendMessage("modify-failed-already", "%option%", option, "%value%", newvalue);
				return;
			}
			
			elevator.setDefaultFloorHeight(height);
			changed = dbm.updateElevator(elevator);
			
			sendMessage("modify-success", "%option%", option, "%old%", oldvalue, "%new%", newvalue, "%elevator%", name);
			break;
		}
		default:
			return;
		}
		
		// Debug message
		Logger.debug("Modified elevator '" + name + "' option '" + option +  "' by user " + sender.getName()
				+ " ('" + oldvalue + "' -> '" + newvalue + "')", changed);
	}
	
}
