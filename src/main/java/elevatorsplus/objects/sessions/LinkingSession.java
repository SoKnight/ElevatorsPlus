package elevatorsplus.objects.sessions;

import elevatorsplus.objects.TextLocation;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@AllArgsConstructor
@RequiredArgsConstructor
public class LinkingSession {

	private final String name;
	private final String elevator;
	private final TextLocation location;
	private final boolean relinking;
	private int currentFloor;
	
}
