package elevatorsplus.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.command.CommandSender;

import elevatorsplus.enums.Node;
import elevatorsplus.files.Messages;

public class HelpMessageFactory {

	private static Map<String, String> cache = new HashMap<>();
	
	private final CommandSender sender;
	private List<String> messages;
	
	public HelpMessageFactory(CommandSender sender) {
		this.sender = sender;
		this.messages = new ArrayList<>();
	}
	
	public void addHelpMessage(String command, String permission, String description, Node... nodes) {
		if(!sender.hasPermission(permission)) return;
		
		if(cache.containsKey(description)) {
			messages.add(cache.get(description));
			return;
		}
		
		String description2 = Messages.getMessage("help-descriptions." + description);
		
		if(nodes != null && nodes.length != 0)
			for(Node node : nodes)
				command += " " + node.getNode();

		String message = Messages.formatMessage("help-body", "%command%", command, "%description%", description2);
		cache.put(description, message);
		messages.add(message);
	}
	
	public void addHelpMessage(String command, String permission, Node... nodes) {
		addHelpMessage(command, permission, command, nodes);
	}
	
	public List<String> create() {
		return messages;
	}
	
}
