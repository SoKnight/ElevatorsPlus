package elevatorsplus.ui;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import elevatorsplus.files.Config;
import lombok.Getter;

@Getter
public class MenuPattern {

	private final String title;
	private final boolean autosize;
	private int size;
	
	private final ItemStack current;
	private final ItemStack other;
	
	public MenuPattern() {
		this.title = Config.getColoredString("gui.title");
		this.size = Config.getConfig().getInt("gui.size", 0) * 9;
		if(size == 0) this.autosize = true;
		else this.autosize = false;
		
		String cmstr = Config.getConfig().getString("gui.current.material").toUpperCase();
		Material currentMaterial = Material.valueOf(cmstr);
		if(currentMaterial != null) {
			this.current = new ItemStack(currentMaterial);
			
			String name = Config.getColoredString("gui.current.name");
			List<String> lore = Config.getColoredList("gui.current.lore");
			
			ItemMeta meta = current.getItemMeta();
			if(!name.equals("")) meta.setDisplayName(name);
			if(!lore.isEmpty()) meta.setLore(lore);
			
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
			
			if(Config.getConfig().getBoolean("gui.current.enchanted", false)) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			}
			
			current.setItemMeta(meta);
		} else this.current = null;
		
		String omstr = Config.getConfig().getString("gui.other.material").toUpperCase();
		Material otherMaterial = Material.valueOf(omstr);
		if(otherMaterial != null) {
			this.other = new ItemStack(otherMaterial);
			
			String name = Config.getColoredString("gui.other.name");
			List<String> lore = Config.getColoredList("gui.other.lore");
			
			ItemMeta meta = other.getItemMeta();
			if(!name.equals("")) meta.setDisplayName(name);
			if(!lore.isEmpty()) meta.setLore(lore);
			
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_POTION_EFFECTS);
			
			if(Config.getConfig().getBoolean("gui.other.enchanted", false)) {
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				meta.addEnchant(Enchantment.ARROW_DAMAGE, 1, true);
			}
			
			other.setItemMeta(meta);
		} else this.other = null;
		
	}
	
}
