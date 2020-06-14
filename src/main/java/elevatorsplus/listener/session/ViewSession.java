package elevatorsplus.listener.session;

import org.bukkit.inventory.InventoryView;

import elevatorsplus.database.Elevator;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ViewSession {

	private final InventoryView view;
	private final Elevator elevator;
	
}
