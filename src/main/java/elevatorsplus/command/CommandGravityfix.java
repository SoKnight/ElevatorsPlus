package elevatorsplus.command;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.World.Environment;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import ru.soknight.lib.argument.CommandArguments;
import ru.soknight.lib.command.ExtendedSubcommandExecutor;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.validation.validator.ArgsCountValidator;
import ru.soknight.lib.validation.validator.PermissionValidator;
import ru.soknight.lib.validation.validator.Validator;

public class CommandGravityfix extends ExtendedSubcommandExecutor {
	
	private final Messages messages;
	private final Server server;
	
	public CommandGravityfix(Messages messages) {
		super(messages);
		
		this.messages = messages;
		this.server = Bukkit.getServer();
		
		String permmsg = messages.get("error.no-permissions");
		String argsmsg = messages.get("error.wrong-syntax");
		
		Validator permval = new PermissionValidator("eplus.command.gravityfix", permmsg);
		Validator argsval = new ArgsCountValidator(1, argsmsg);
		
		super.addValidators(permval, argsval);
	}

	@Override
	public void executeCommand(CommandSender sender, CommandArguments args) {
		if(!validateExecution(sender, args)) return;
			
		String action = args.remove(0).toLowerCase();
		
		switch (action) {
		case "all":
			fixAll(sender);
			break;
		case "player":
			fixPlayer(sender, args);
			break;
		case "world":
			fixWorld(sender, args);
			break;
		default:
			break;
		}
	}
	
	private void fixAll(CommandSender sender) {
		Collection<? extends Player> online = server.getOnlinePlayers();
		
		if(online == null || online.isEmpty()) {
			messages.getAndSend(sender, "gravityfix.all.no-players");
			return;
		}
		
		List<Player> players = online
				.parallelStream()
				.filter(p -> !p.hasGravity())
				.collect(Collectors.toList());
		
		players.forEach(p -> {
			Location location = p.getLocation();
			World world = location.getWorld();
			
			if(world.getEnvironment() == Environment.NORMAL) {
				Block foot = world.getBlockAt(location.add(0, -1, 0));
				
				if(foot == null || foot.getType() == Material.AIR) {
					Location target = location.getWorld().getHighestBlockAt(location).getLocation();
					p.teleport(target.add(0.5, 1, 0.5));
				}
			}
			
			p.setGravity(true);
		});
		
		messages.sendFormatted(sender, "gravityfix.all.enabled", "%count%", players.size());
	}
	
	private void fixPlayer(CommandSender sender, CommandArguments args) {
		if(args.isEmpty()) return;
		
		String name = args.get(0);
		if(name == null || name.isEmpty()) return;
		
		OfflinePlayer offline = server.getOfflinePlayer(name);
		
		if(offline == null || !offline.isOnline()) {
			messages.getAndSend(sender, "gravityfix.player.not-found");
			return;
		}
		
		Player p = offline.getPlayer();
		if(p.hasGravity()) {
			messages.getAndSend(sender, "gravityfix.player.already");
			return;
		}
		
		Location location = p.getLocation();
		World world = location.getWorld();
		
		if(world.getEnvironment() == Environment.NORMAL) {
			Block foot = world.getBlockAt(location.add(0, -1, 0));
			
			if(foot == null || foot.getType() == Material.AIR) {
				Location target = location.getWorld().getHighestBlockAt(location).getLocation();
				p.teleport(target.add(0.5, 1, 0.5));
			}
		}
			
		p.setGravity(true);
		
		messages.sendFormatted(sender, "gravityfix.player.enabled", "%player%", name);
	}
	
	private void fixWorld(CommandSender sender, CommandArguments args) {
		if(args.isEmpty()) return;
		
		String name = args.get(0);
		if(name == null || name.isEmpty()) return;
		
		World world = server.getWorld(name);
		if(world == null) {
			messages.getAndSend(sender, "gravityfix.world.unknown");
			return;
		}
		
		List<Player> online = world.getEntities()
				.parallelStream()
				.filter(e -> e instanceof Player)
				.map(e -> (Player) e)
				.collect(Collectors.toList());
		
		if(online == null || online.isEmpty()) {
			messages.getAndSend(sender, "gravityfix.world.no-players");
			return;
		}
		
		List<Player> players = online
				.parallelStream()
				.filter(p -> !p.hasGravity())
				.collect(Collectors.toList());
		
		players.forEach(p -> {
			if(world.getEnvironment() == Environment.NORMAL) {
				Location location = p.getLocation();
				Block foot = world.getBlockAt(location.add(0, -1, 0));
			
				if(foot == null || foot.getType() == Material.AIR) {
					Location target = location.getWorld().getHighestBlockAt(location).getLocation();
					p.teleport(target.add(0.5, 1, 0.5));
				}
			}
			
			p.setGravity(true);
		});
		
		messages.sendFormatted(sender, "gravityfix.world.enabled", "%count%", players.size(), "%world%", name);
	}

}
