package elevatorsplus.configuration;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.ui.MenuPattern;
import lombok.Getter;
import ru.soknight.lib.configuration.AbstractConfiguration;
import ru.soknight.lib.logging.PluginLogger;

@Getter
public class Config extends AbstractConfiguration {

	private final PluginLogger logger;
	private final MenuPattern menuPattern;
	
	private final List<Material> callbuttons, signs, doors;
	private List<String> signcontent;
	
	public Config(ElevatorsPlus plugin) {
		super(plugin, "config.yml");
		this.logger = plugin.getPluginLogger();
		this.menuPattern = new MenuPattern(this);
		
		this.callbuttons = new ArrayList<>();
		this.signs = new ArrayList<>();
		this.doors = new ArrayList<>();
		
		refreshLists();
	}
	
	public void refreshLists() {
		this.callbuttons.clear();
		List<String> callbuttons = getList("elements.callbuttons");
		
		if(callbuttons != null && !callbuttons.isEmpty())
			callbuttons.forEach(s -> {
				Material m = Material.valueOf(s.toUpperCase());
				if(m != null) this.callbuttons.add(m);
				else logger.error("Unknown callbutton material type: " + s);
			});
		
		this.signs.clear();
		List<String> signs = getList("elements.signs");
		
		if(signs != null && !signs.isEmpty())
			signs.forEach(s -> {
				Material m = Material.valueOf(s.toUpperCase());
				if(m != null) this.signs.add(m);
				else logger.error("Unknown sign material type: " + s);
			});
		
		this.doors.clear();
		List<String> doors = getList("elements.doors");
		
		if(doors != null && !doors.isEmpty())
			doors.forEach(s -> {
				Material m = Material.valueOf(s.toUpperCase());
				if(m != null) this.doors.add(m);
				else logger.error("Unknown door material type: " + s);
			});
		
		this.signcontent = getColoredList("elements.sign-content");
		return;
	}

}
