package elevatorsplus.command.validation;

import elevatorsplus.database.DatabaseManager;
import lombok.AllArgsConstructor;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.ValidationResult;
import ru.soknight.lib.validation.validator.Validator;

@AllArgsConstructor
public class ElevatorValidator implements Validator {

	private final DatabaseManager databaseManager;
	private final String message;
	
	private boolean existNeeded;
	
	public ElevatorValidator(DatabaseManager databaseManager, String message) {
		this.databaseManager = databaseManager;
		this.message = message;
		this.existNeeded = true;
	}
	
	@Override
	public ValidationResult validate(CommandExecutionData data) {
		if(!(data instanceof ElevatorExecutionData)) return new ValidationResult(false);
		
		ElevatorExecutionData elevdata = (ElevatorExecutionData) data;
		String name = elevdata.getElevator();
		
		ValidationResult failed = new ValidationResult(false, message.replace("%elevator%", name));
			
		if(name == null || databaseManager == null) return failed;
		
		boolean validated = databaseManager.getElevator(name) != null;
		if(!existNeeded) validated = !validated;
		
		if(validated) return new ValidationResult(true);
		else return failed;
	}
	
}
