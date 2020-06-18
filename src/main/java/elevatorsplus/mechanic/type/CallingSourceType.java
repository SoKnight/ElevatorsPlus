package elevatorsplus.mechanic.type;

public enum CallingSourceType {

	SELF, CALL;
	
	public boolean isCalled() {
		return this == CALL;
	}
	
}
