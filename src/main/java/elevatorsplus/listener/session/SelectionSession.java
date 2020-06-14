package elevatorsplus.listener.session;

import elevatorsplus.listener.ElementType;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class SelectionSession {

	private final String name;
	private final String elevator;
	private final ElementType control;
	
}
