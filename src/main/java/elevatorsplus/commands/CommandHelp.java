package elevatorsplus.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;

import elevatorsplus.enums.Node;
import elevatorsplus.files.Messages;
import elevatorsplus.utils.HelpMessageFactory;

public class CommandHelp extends AbstractSubCommand {

	private final CommandSender sender;
	private final String[] args;
	
	public CommandHelp(CommandSender sender, String[] args) {
		super(sender, args, "eplus.command.help", 1);
		this.sender = sender;
		this.args = args;
	}

	@Override
	public void execute() {
		if(!hasPermission()) return;
		
		String header = Messages.getMessage("help-header");
		String footer = Messages.getMessage("help-footer");
		List<String> body = new ArrayList<>();
		
		if(args.length == 1) {
			HelpMessageFactory hmf = new HelpMessageFactory(sender);
			hmf.addHelpMessage("help", "eplus.command.help", Node.COMMAND);
			hmf.addHelpMessage("create", "eplus.command.create", Node.NAME);
			hmf.addHelpMessage("delete", "eplus.command.delete", Node.ELEVATOR);
			hmf.addHelpMessage("modify", "eplus.command.modify", Node.ELEVATOR, Node.OPTION, Node.VALUES);
			hmf.addHelpMessage("info", "eplus.command.info", Node.ELEVATOR);
			hmf.addHelpMessage("list", "eplus.command.list", Node.PAGE);
			hmf.addHelpMessage("reload", "eplus.command.reload");
			hmf.addHelpMessage("done", "eplus.command.done");
			hmf.addHelpMessage("selection", "eplus.command.selection", Node.ELEVATOR, Node.CONTROLS);
			hmf.addHelpMessage("controls", "eplus.command.controls", Node.ELEVATOR, Node.CONTROLS, Node.PAGE);
			body = hmf.create();
		} else {
			if(!hasPermission("eplus.command.help")) return;
			
			String path = "help-command-" + args[1].toLowerCase();
			if(Messages.getConfig().contains(path))
				body = Messages.getMessagesList(path);
			else {
				sendMessage("help-command-not-found");
				return;
			}
		}
		
		sender.sendMessage(header);
		body.forEach(str -> sender.sendMessage(str));
		sender.sendMessage(footer);
		return;
	}
	
}
