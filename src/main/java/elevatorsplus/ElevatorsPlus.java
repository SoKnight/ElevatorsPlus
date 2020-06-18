package elevatorsplus;

import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import elevatorsplus.command.SubcommandHandler;
import elevatorsplus.configuration.Config;
import elevatorsplus.configuration.MessagesProvider;
import elevatorsplus.database.Database;
import elevatorsplus.database.DatabaseManager;
import elevatorsplus.listener.ElementsClickListener;
import elevatorsplus.listener.ElementsDestroyingListener;
import elevatorsplus.listener.LinkingSessionListener;
import elevatorsplus.listener.SelectionSessionListener;
import elevatorsplus.listener.session.SessionManager;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.sound.AmbientSoundPlayer;
import elevatorsplus.mechanic.tool.ElevatorSignRefresher;
import elevatorsplus.ui.MenuBuilder;
import elevatorsplus.ui.MenuListener;
import ru.soknight.lib.configuration.Messages;

public class ElevatorsPlus extends JavaPlugin {
	
	protected AmbientSoundPlayer soundPlayer;
	protected ElevatorSignRefresher signRefresher;
	protected ElevatorMoveOperator moveOperator;
	
	protected MenuBuilder menuBuilder;
	
	protected DatabaseManager databaseManager;
	protected SessionManager sessionManager;
	
	protected Config config;
	protected Messages messages;
	protected MessagesProvider messagesProvider;
	
	@Override
	public void onEnable() {
		long start = System.currentTimeMillis();
		
		// Configs initialization
		refreshConfigs();
		
		// Database initialization
		try {
			Database database = new Database(this, config);
			this.databaseManager = new DatabaseManager(this, database);
		} catch (Exception e) {
			getLogger().severe("Failed to initialize database: " + e.getLocalizedMessage());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}
		
		// Any providers initialize
		initializeProviders();
		
		// Command executors registration
		registerCommand();
		
		// Moving task launching
		launchMovingTask();
		
		// Listeners initialization
		registerListeners();
		
		long time = System.currentTimeMillis() - start;
		getLogger().info("Bootstrapped in " + time + " ms.");
	}
	
	private void refreshConfigs() {
		this.config = new Config(this);
		
		this.messagesProvider = new MessagesProvider(this, config);
		this.messages = messagesProvider.getMessages();
	}
	
	private void registerCommand() {
		SubcommandHandler handler = new SubcommandHandler(
				this,
				databaseManager,
				config,
				sessionManager,
				signRefresher,
				messages);
		
		PluginCommand eplus = getCommand("eplus");
		
		eplus.setExecutor(handler);
		eplus.setTabCompleter(handler);
	}
	
	private void registerListeners() {
		SelectionSessionListener selectionSessionListener = new SelectionSessionListener(
				config,
				messages,
				sessionManager,
				databaseManager,
				soundPlayer);
		
		ElementsClickListener elementsClickListener = new ElementsClickListener(
				config,
				messages,
				databaseManager,
				sessionManager,
				menuBuilder,
				moveOperator);
		
		PluginManager manager = Bukkit.getPluginManager();
		
		manager.registerEvents(selectionSessionListener, this);
		manager.registerEvents(elementsClickListener, this);
		
		manager.registerEvents(new LinkingSessionListener(messages, sessionManager, databaseManager), this);
		manager.registerEvents(new MenuListener(messages, sessionManager, moveOperator), this);
		
		if(!config.getBoolean("allow-elements-destroy"))
			manager.registerEvents(new ElementsDestroyingListener(config, messages, databaseManager), this);
	}
	
	private void initializeProviders() {
		this.sessionManager = new SessionManager();
		this.soundPlayer = new AmbientSoundPlayer(this, config);
		
		this.signRefresher = new ElevatorSignRefresher(config, messages);
		this.menuBuilder = new MenuBuilder(config, messages);
	}
	
	private void launchMovingTask() {
		this.moveOperator = new ElevatorMoveOperator(
				this,
				config,
				messages,
				databaseManager,
				menuBuilder,
				soundPlayer,
				signRefresher);
	}
	
	public void refresh() {
		config.reload();
		messagesProvider.update(config);
		
		moveOperator.update();
		
		registerCommand();
	}
	
	@Override
	public void onDisable() {
		if(databaseManager != null)
			databaseManager.shutdown();
	}
	
}
