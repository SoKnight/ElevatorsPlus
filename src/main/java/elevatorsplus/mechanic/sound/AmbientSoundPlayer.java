package elevatorsplus.mechanic.sound;

import java.util.logging.Logger;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;

public class AmbientSoundPlayer {

	private final Logger logger;
	
	private AmbientSound finished, started, selected;
	
	public AmbientSoundPlayer(ElevatorsPlus plugin, Config config) {
		this.logger = plugin.getLogger();
		
		update(config);
	}
	
	public void update(Config config) {
		ConfigurationSection section = config.getFileConfig().getConfigurationSection("sound-playing");
		
		this.finished = initialize(section.getConfigurationSection("finished"));
		this.started = initialize(section.getConfigurationSection("started"));
		this.selected = initialize(section.getConfigurationSection("selection"));
	}
	
	public void onFinish(HumanEntity player) {
		if(started != null) started.stop(player);
		if(selected != null) selected.stop(player);
		
		if(finished != null) finished.play(player);
	}
	
	public void onStart(HumanEntity player) {
		if(selected != null) selected.stop(player);
		if(finished != null) finished.stop(player);
		
		if(started != null) started.play(player);
	}
	
	public void onSelect(HumanEntity player) {
		if(started != null) started.stop(player);
		if(finished != null) started.stop(player);
		
		if(selected != null) selected.play(player);
	}
	
	private AmbientSound initialize(ConfigurationSection section) {
		boolean enabled = section.getBoolean("enabled");
		if((Boolean) enabled == null) {
			logger.severe("Failed to load ambient sound '" + section.getName() + "': Unspecified 'enabled' parameter.");
			return null;
		}
		
		if(!enabled) return null;
		
		Sound sound = Sound.valueOf(section.getString("sound", "").toUpperCase());
		if(sound == null) {
			logger.severe("Failed to load ambient sound '" + section.getName() + "': Unknown sound type.");
			return null;
		}
		
		float volume = (float) section.getDouble("volume");
		if((Float) volume == null) {
			logger.severe("Failed to load ambient sound '" + section.getName() + "': Unspecified 'volume' parameter.");
			return null;
		}
		
		float pitch = (float) section.getDouble("pitch", 1F);
		return new AmbientSound(sound, volume, pitch);
	}
	
}
