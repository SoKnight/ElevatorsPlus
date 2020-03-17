package elevatorsplus;

import org.bukkit.Bukkit;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import elevatorsplus.commands.CommandsHandler;
import elevatorsplus.commands.CommandsTabCompleter;
import elevatorsplus.database.Database;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.files.Config;
import elevatorsplus.files.Messages;
import elevatorsplus.listeners.ElevatorDestroyListener;
import elevatorsplus.listeners.LinkingSessionListener;
import elevatorsplus.listeners.SelectionSessionListener;
import elevatorsplus.listeners.SessionManager;
import elevatorsplus.listeners.SignClickListener;
import elevatorsplus.ui.MenuBuilder;
import elevatorsplus.ui.MenuListener;
import elevatorsplus.utils.Logger;
import lombok.Getter;

@Getter
public class ElevatorsPlus extends JavaPlugin {

	@Getter private static ElevatorsPlus instance;
	
	private DatabaseManager databaseManager;
	private SessionManager sessionManager;
	private MenuBuilder menuBuilder;
	
	@Override
	public void onEnable() {
		instance = this;
		
		Config.refresh();
		Messages.refresh();
		
		try {
			Database database = new Database();
			databaseManager = new DatabaseManager(database);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.sessionManager = new SessionManager();
		this.menuBuilder = new MenuBuilder(Config.getMenuPattern());
		
		getCommand("eplus").setExecutor(new CommandsHandler(sessionManager, databaseManager));
		getCommand("eplus").setTabCompleter(new CommandsTabCompleter(databaseManager));
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new SelectionSessionListener(sessionManager, databaseManager), this);
		manager.registerEvents(new LinkingSessionListener(sessionManager, databaseManager), this);
		manager.registerEvents(new SignClickListener(databaseManager, sessionManager), this);
		manager.registerEvents(new MenuListener(sessionManager), this);
		
		if(!Config.getConfig().getBoolean("allow-controls-destroy"))
			manager.registerEvents(new ElevatorDestroyListener(databaseManager), this);
		
		Logger.info("Oh, it's alive :D");
	}
	
	@Override
	public void onDisable() {
		if(databaseManager != null) databaseManager.shutdown();
		Logger.info("I'll be back..");
	}
	
}
