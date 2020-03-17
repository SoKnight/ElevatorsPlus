package elevatorsplus.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import elevatorsplus.database.DatabaseManager;
import elevatorsplus.objects.Elevator;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class CommandsTabCompleter implements TabCompleter {

	private static final List<String> subcommands = Arrays.asList("help", "create", "delete", "modify", "info",
			"list", "reload", "done", "selection", "controls");
	private static final List<String> controls = Arrays.asList("callbuttons", "platform", "floorheights");
	private static final List<String> options = Arrays.asList("name", "world", "sign", "floorscount",
			"floorheight", "defaultheight");
	
	private final DatabaseManager dbm;
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if(args.length == 0) return null;
		List<String> output = new ArrayList<>();
		if(args.length < 2) {
			String arg = args[0].toLowerCase();
			subcommands.stream()
				.filter(s -> s.startsWith(arg))
				.forEach(c -> output.add(c));
			return output;
		}
		
		List<String> elevators = dbm.getAllNames();
		String arg = args[1].toLowerCase();
		
		switch(args[0]) {
		case "help": {
			subcommands.stream()
				.filter(s -> !s.equals("help") && s.startsWith(arg))
				.forEach(c -> output.add(c));
			
			break;
		}
		case "delete": {
			if(args.length > 2) break;
			
			elevators.stream()
				.filter(s -> s.toLowerCase().startsWith(arg))
				.forEach(e -> output.add(e));
			
			break;
		}
		case "modify": {
			if(args.length == 2) {
				elevators.stream()
					.filter(s -> s.toLowerCase().startsWith(arg))
					.forEach(e -> output.add(e));
				break;
			}

			Elevator elevator = dbm.getElevator(args[1]);
			if(elevator == null) break;
			
			String option = args[2].toLowerCase();
			
			if(args.length == 3) {
				options.stream()
					.filter(s -> s.startsWith(option))
					.forEach(o -> output.add(o));
				break;
			}
			
			String value = args[3].toLowerCase();
			List<String> values = new ArrayList<>();
			
			switch (option) {
			case "world": {
				if(sender instanceof Player)
					values.add("#current");
				
				Bukkit.getWorlds().forEach(w -> values.add(w.getName()));
				values.stream()
					.filter(s -> s.toLowerCase().startsWith(value))
					.forEach(v -> output.add(v));
				
				break;
			}
			case "sign": {
				if(sender instanceof Player) {
					Player player = (Player) sender;
					
					Block block = player.getTargetBlock(10);
					if(block == null) break;
					
					Location location = block.getLocation();
					switch (args.length) {
					case 4: {
						values.add(String.valueOf(location.getBlockX()));
						break;
					}
					case 5: {
						values.add(String.valueOf(location.getBlockY()));
						break;
					}
					case 6: {
						values.add(String.valueOf(location.getBlockZ()));
						break;
					}
					default:
						break;
					}
				}
				
				break;
			}
			case "floorheight": {
				if(args.length > 5) break;
				
				if(args.length == 4) {
					int count = elevator.getFloorsCount();
					if(count == 1) {
						output.add(String.valueOf(1));
						break;
					}
					
					for(int i = 1; i <= count; i++)
						output.add(String.valueOf(i));
				} else
					for(int i = 2; i <= 10; i++)
						output.add(String.valueOf(i));
				
			}
			default:
				break;
			}
			
			return values;
		}
		case "info": {
			if(args.length > 2) break;
			
			elevators.stream()
				.filter(s -> s.toLowerCase().startsWith(arg))
				.forEach(e -> output.add(e));
			
			break;
		}
		case "list": {
			if(args.length > 2) break;
			
			if(elevators.isEmpty()) break;
			
			int pages = elevators.size() / 10;
			if(elevators.size() % 10 > 1) pages++;
			for(int i = 1; i <= pages; i++)
				output.add(String.valueOf(i));
			
			break;
		}
		case "selection": {
			if(args.length > 3) break;
			
			if(args.length == 2) {
				elevators.stream()
					.filter(s -> s.toLowerCase().startsWith(arg))
					.forEach(e -> output.add(e));
			} else {
				String control = args[2].toLowerCase();
				controls.stream()
					.filter(s -> !s.equals("floorheights") && s.toLowerCase().startsWith(control))
					.forEach(e -> output.add(e));
			}
			
			break;
		}
		case "controls": {
			if(args.length > 4) break;
			
			if(args.length == 2) {
				elevators.stream()
					.filter(s -> s.toLowerCase().startsWith(arg))
					.forEach(e -> output.add(e));
				break;
			}
				
			String control = args[2].toLowerCase();
			if(args.length == 3) {
				controls.stream()
					.filter(s -> s.toLowerCase().startsWith(control))
					.forEach(e -> output.add(e));
			} else {
				if(!controls.contains(control)) break;
				
				Elevator elevator = dbm.getElevator(arg);
				int count = control.equals("callbuttons") ? elevator.getCallButtons().size() : elevator.getPlatformBlocks().size();
				
				if(count == 0) break;
				
				int pages2 = count / 10;
				if(count % 10 > 1) pages2++;
				
				for(int i = 1; i <= pages2; i++)
					output.add(String.valueOf(i));
			}
			
			break;
		}
		default:
			break;
		}
		return output;
	}

}
