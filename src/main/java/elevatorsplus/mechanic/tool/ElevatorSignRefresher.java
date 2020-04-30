package elevatorsplus.mechanic.tool;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;

import elevatorsplus.configuration.Config;
import elevatorsplus.database.Elevator;
import elevatorsplus.database.TextLocation;
import lombok.AllArgsConstructor;
import ru.soknight.lib.configuration.Messages;

@AllArgsConstructor
public class ElevatorSignRefresher {
	
	private final Config config;
	private final Messages messages;
	
	public void createSign(Elevator elevator, BlockData signData, int targetY) {
		String l = elevator.getSignLocation();
		if(l.equals("none")) return;
		
		World world = elevator.getBukkitWorld();
		if(world == null) return;
		
		TextLocation textLoc = new TextLocation(l);
		textLoc.setY(targetY);
		
		Location location = textLoc.toLocation(world);
		if(location == null) return;
		
		elevator.setSignLocation(textLoc.getAsString());
		
		Block block = world.getBlockAt(location);
		block.setBlockData(signData);
		
		refreshInformation(elevator, block);
	}
	
	public BlockData removeSign(Elevator elevator) {
		String l = elevator.getSignLocation();
		if(l.equals("none")) return null;
		
		World world = elevator.getBukkitWorld();
		if(world == null) return null;
		
		Location location = new TextLocation(l).toLocation(world);
		if(location == null) return null;
		
		Block block = world.getBlockAt(location);
		if(!config.getSigns().contains(block.getType())) return null;
		
		BlockData data = block.getBlockData();
		block.setType(Material.AIR);
		
		return data;
	}
	
	public void refreshInformation(Elevator elevator, Block block) {
		List<String> content = config.getColoredList("elements.sign-content");
		
		String name = elevator.getName();
		int current = elevator.getCurrentLevel();
		int levels = elevator.getLevelsCount();
		
		Sign sign = (Sign) block.getState();
		
		int i = 0;
		for(String s : content) {
			s = messages.format(s, "%name%", name, "%current%", current, "%levels%", levels);
			sign.setLine(i, s);
			i++;
		}
		
		sign.update();
	}
	
}
