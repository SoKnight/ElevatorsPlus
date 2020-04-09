package elevatorsplus.mechanic.sound;

import org.bukkit.Sound;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class AmbientSound {

	private final Sound sound;
	private final float volume, pitch;
	
	public void play(HumanEntity player) {
		if(player == null) return;
		
		if(player instanceof Player)
			((Player) player).playSound(player.getLocation(), sound, volume, pitch);
	}
	
	public void stop(HumanEntity player) {
		if(player == null) return;
		
		if(player instanceof Player)
			((Player) player).stopSound(sound);
	}
	
}
