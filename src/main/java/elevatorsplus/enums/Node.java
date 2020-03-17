package elevatorsplus.enums;

import elevatorsplus.files.Messages;

public enum Node {

	COMMAND, NAME, ELEVATOR, PLAYER, OPTION, VALUES, PAGE, CONTROLS;
	
	public String getNode() {
		return Messages.getMessage("help-nodes." + name().toLowerCase());
	}
	
}
