package elevatorsplus.listener.session;

import elevatorsplus.database.TextLocation;
import elevatorsplus.listener.ElementType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class LinkingSession {

	private final String name;
	private final String elevator;
	private final ElementType type;
	private final TextLocation location;
	private final boolean relinking;
	private int currentFloor;
	
}
