package elevatorsplus.objects.sessions;

import elevatorsplus.enums.Control;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SelectionSession {

	private final String name;
	private final String elevator;
	private final Control control;
	
}
