package elevatorsplus.mechanic.sound;

import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;

import elevatorsplus.configuration.Config;
import ru.soknight.lib.logging.PluginLogger;

public class AmbientSoundPlayer {

	private final PluginLogger logger;
	private final AmbientSound finished, started, selected;
	
	public AmbientSoundPlayer(PluginLogger logger, Config config) {
		this.logger = logger;
		
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
			logger.error("Failed to load ambient sound '" + section.getName() + "': Unspecified 'enabled' parameter.");
			return null;
		}
		
		if(!enabled) return null;
		
		Sound sound = Sound.valueOf(section.getString("sound", "").toUpperCase());
		if(sound == null) {
			logger.error("Failed to load ambient sound '" + section.getName() + "': Unknown sound type.");
			return null;
		}
		
		float volume = (float) section.getDouble("volume");
		if((Float) volume == null) {
			logger.error("Failed to load ambient sound '" + section.getName() + "': Unspecified 'volume' parameter.");
			return null;
		}
		
		float pitch = (float) section.getDouble("pitch", 1F);
		return new AmbientSound(sound, volume, pitch);
	}
	
}
