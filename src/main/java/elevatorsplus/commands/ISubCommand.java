package elevatorsplus.commands;

public interface ISubCommand {
    
    void execute();
    
    void sendMessage(String message);
    
    void sendMessage(String message, Object... replacements);

    boolean hasPermission();
    
    boolean hasPermission(String permission);
    
    boolean isPlayerRequired();
    
    boolean isElevatorExist(String elevator);
    
    boolean isElevatorNotExist(String elevator);
    
    boolean argIsInteger(String arg);
    
    boolean isCorrectUsage();
    
    boolean isCorrectUsage(int minArgsLength);
	
}
