package elevatorsplus.command.validation;

import org.bukkit.command.CommandSender;

import elevatorsplus.database.DatabaseManager;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.soknight.lib.validation.CommandExecutionData;

@Getter
@AllArgsConstructor
public class ElevatorExecutionData implements CommandExecutionData {

	private final CommandSender sender;
	private final String[] args;
	
	private final DatabaseManager databaseManager;
	private final String elevatorName;

}
