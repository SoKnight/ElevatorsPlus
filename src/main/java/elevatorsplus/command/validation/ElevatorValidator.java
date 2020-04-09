package elevatorsplus.command.validation;

import elevatorsplus.database.DatabaseManager;
import lombok.AllArgsConstructor;
import ru.soknight.lib.validation.CommandExecutionData;
import ru.soknight.lib.validation.ValidationResult;
import ru.soknight.lib.validation.validator.Validator;

@AllArgsConstructor
public class ElevatorValidator extends Validator {

	private final boolean existNeeded;
	private final String message;
	
	@Override
	public ValidationResult validate(CommandExecutionData data) {
		if(!(data instanceof ElevatorExecutionData)) return new ValidationResult(false);
		
		ElevatorExecutionData elevdata = (ElevatorExecutionData) data;
		String name = elevdata.getElevatorName();
		
		ValidationResult failed = new ValidationResult(false, message.replace("%elevator%", name));
		DatabaseManager dbm = elevdata.getDatabaseManager();
			
		if(name == null || dbm == null) return failed;
		
		boolean validated = dbm.getElevator(name) != null;
		if(!existNeeded) validated = !validated;
		
		if(validated) return new ValidationResult(true);
		else return failed;
	}
	
}
