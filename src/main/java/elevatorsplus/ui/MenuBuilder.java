package elevatorsplus.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elevatorsplus.configuration.Config;
import elevatorsplus.database.Elevator;
import ru.soknight.lib.configuration.Messages;

public class MenuBuilder {

	private final Messages messages;
	private final MenuPattern pattern;
	
	private final Map<String, Inventory> guis;
	
	public MenuBuilder(Config config, Messages messages) {
		this.messages = messages;
		this.pattern = config.getMenuPattern();
		
		this.guis = new HashMap<>();
	}
	
	public Inventory getOrCreateGui(Elevator elevator) {
		String name = elevator.getName();
		
		if(guis.containsKey(name))
			return guis.get(name);
		
		Inventory gui = build(elevator);
		guis.put(name, gui);
		return gui;
	}
	
	public void updateGui(Elevator elevator) {
		Inventory gui = build(elevator);
		guis.put(elevator.getName(), gui);
	}
	
	private Inventory build(Elevator elevator) {
		String name = elevator.getName();
		int levels = elevator.getLevelsCount();
		int current = elevator.getCurrentLevel();
		
		String title = messages.format(pattern.getTitle(),
				"%name%", name,
				"%levels%", levels,
				"%current%", current);
		
		int size = pattern.getSize();
		if(pattern.isAutosize()) {
			size = levels / 9;
			if(levels % 9 > 0) size++;
			size *= 9;
		}
		
		Inventory inventory = Bukkit.createInventory(null, size, title);
		
		// Adding pre-current entries
		inventory = fillPreCurrent(inventory, elevator);
		
		// Adding current entry
		ItemStack curitem = pattern.getCurrent().clone();
		curitem = formatMeta(curitem, elevator, current);
		inventory.setItem(current - 1, curitem);
		
		// Adding post-current entries
		inventory = fillPostCurrent(inventory, elevator);
		
		return inventory;
	}
	
	private Inventory fillPreCurrent(Inventory inventory, Elevator elevator) {
		int current = elevator.getCurrentLevel();
		
		if(current == 1) return inventory;
		
		ItemStack other = pattern.getOther();
		for(int i = 1; i < current; i++) {
			ItemStack temp = other.clone();
			temp = formatMeta(temp, elevator, i);
			inventory.setItem(i - 1, temp);
		}
		
		return inventory;
	}
	
	private Inventory fillPostCurrent(Inventory inventory, Elevator elevator) {
		int current = elevator.getCurrentLevel();
		int levels = elevator.getLevelsCount();
		
		if(current == levels) return inventory;
		
		ItemStack other = pattern.getOther();
		for(int i = current; i < levels; i++) {
			ItemStack temp = other.clone();
			temp = formatMeta(temp, elevator, i + 1);
			inventory.setItem(i, temp);
		}
		
		return inventory;
	}
	
	private ItemStack formatMeta(ItemStack item, Elevator elevator, int level) {
		ItemMeta meta = item.getItemMeta();
		
		String name = elevator.getName();
		int levels = elevator.getLevelsCount();
		
		if(meta.hasDisplayName()) {
			String displayname = messages.format(meta.getDisplayName(),
					"%name%", name,
					"%level%", level,
					"%levels%", levels);
			meta.setDisplayName(displayname);
		}
		
		if(meta.hasLore()) {
			List<String> raw = meta.getLore();
			List<String> lore = new ArrayList<>();
			
			raw.forEach(s -> {
				if(s.contains("%"))
					s = messages.format(s,
							"%name%", name,
							"%level%", level,
							"%levels%", levels);
				lore.add(s);
			});
			meta.setLore(lore);
		}
		
		item.setItemMeta(meta);
		return item;
	}
	
}
