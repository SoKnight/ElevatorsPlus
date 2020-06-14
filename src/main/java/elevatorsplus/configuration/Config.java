package elevatorsplus.configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Material;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.ui.MenuPattern;
import lombok.Getter;
import ru.soknight.lib.configuration.AbstractConfiguration;

@Getter
public class Config extends AbstractConfiguration {

	private final Logger logger;
	private MenuPattern menuPattern;
	
	private List<Material> callbuttons, signs, doors;
	private List<String> signcontent;
	
	public Config(ElevatorsPlus plugin) {
		super(plugin, "config.yml");
		this.logger = plugin.getLogger();
		this.menuPattern = new MenuPattern(this);
		
		this.callbuttons = new ArrayList<>();
		this.signs = new ArrayList<>();
		this.doors = new ArrayList<>();
		
		refreshLists();
	}
	
	public void reload() {
		super.refresh();
		
		this.menuPattern = new MenuPattern(this);
		
		refreshLists();
	}
	
	private void refreshLists() {
		this.callbuttons.clear();
		List<String> callbuttons = getList("elements.callbuttons");
		
		if(callbuttons != null && !callbuttons.isEmpty())
			callbuttons.forEach(s -> {
				try {
					Material m = Material.valueOf(s.toUpperCase());
					if(m == null) logger.severe("Unknown callbutton material type: " + s);
					
					this.callbuttons.add(m);
				} catch (IllegalArgumentException e) {
					logger.severe("Unknown callbutton material type: " + s);
					return;
				}
			});
		
		this.signs.clear();
		List<String> signs = getList("elements.signs");
		
		if(signs != null && !signs.isEmpty())
			signs.forEach(s -> {
				try {
					Material m = Material.valueOf(s.toUpperCase());
					if(m == null) logger.severe("Unknown sign material type: " + s);
					
					this.signs.add(m);
				} catch (IllegalArgumentException e) {
					logger.severe("Unknown sign material type: " + s);
					return;
				}
			});
		
		this.doors.clear();
		List<String> doors = getList("elements.doors");
		
		if(doors != null && !doors.isEmpty())
			doors.forEach(s -> {
				try {
					Material m = Material.valueOf(s.toUpperCase());
					if(m == null) logger.severe("Unknown door material type: " + s);
					
					this.doors.add(m);
				} catch (IllegalArgumentException e) {
					logger.severe("Unknown door material type: " + s);
					return;
				}
			});
		
		this.signcontent = getColoredList("elements.sign-content");
	}

}
