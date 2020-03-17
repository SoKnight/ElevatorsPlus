package elevatorsplus.objects.sessions;

import org.bukkit.inventory.InventoryView;

import elevatorsplus.objects.Elevator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ViewSession {

	private final InventoryView view;
	private final Elevator elevator;
	
}
