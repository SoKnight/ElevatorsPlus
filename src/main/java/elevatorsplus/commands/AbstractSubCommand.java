package elevatorsplus.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.files.Messages;
import elevatorsplus.objects.Elevator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public abstract class AbstractSubCommand implements ISubCommand {

	private final CommandSender sender;
	
	private String[] args;
	private String permission;
    private int minArgsLength = 0;
    
    @Override
    public void sendMessage(String message) {
    	sender.sendMessage(Messages.getMessage(message));
    }
    
    @Override
    public void sendMessage(String message, Object... replacements) {
    	sender.sendMessage(Messages.formatMessage(message, replacements));
    }
    
    @Override
    public boolean isCorrectUsage() {
    	if(args.length >= minArgsLength) return true;
    		
    	sender.sendMessage(Messages.getMessage("error-wrong-syntax"));
    	return false;
    }
    
    @Override
    public boolean isCorrectUsage(int minArgsLength) {
    	if(args.length >= minArgsLength) return true;
		
    	sender.sendMessage(Messages.getMessage("error-wrong-syntax"));
    	return false;
    }

    @Override
    public boolean hasPermission() {
    	if(sender.hasPermission(permission)) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-no-permissions"));
        return false;
    }
    
    @Override
    public boolean hasPermission(String permission) {
    	if(sender.hasPermission(permission)) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-no-permissions"));
        return false;
    }
    
    @Override
    public boolean isPlayerRequired() {
    	if(sender instanceof Player) return true;
    	
    	sender.sendMessage(Messages.getMessage("error-only-for-players"));
    	return false;
    }
    
    @Override
    public boolean isElevatorExist(String name) {
    	Elevator elevator = ElevatorsPlus.getInstance().getDatabaseManager().getElevator(name);
    	if(elevator != null) return true;
    	
    	sender.sendMessage(Messages.formatMessage("error-unknown-elevator", "%elevator%", name));
    	return false;
    }
    
    @Override
    public boolean isElevatorNotExist(String name) {
    	Elevator elevator = ElevatorsPlus.getInstance().getDatabaseManager().getElevator(name);
    	if(elevator == null) return true;
    	
    	sender.sendMessage(Messages.formatMessage("error-elevator-already-exist", "%elevator%", name));
    	return false;
    }
    
    @Override
    public boolean argIsInteger(String arg) {
    	try {
			Integer.parseInt(arg);
			return true;
		} catch (NumberFormatException ignored) {
			sender.sendMessage(Messages.formatMessage("error-arg-is-not-int", "%arg%", arg));
			return false;
		}
    }
	
}
