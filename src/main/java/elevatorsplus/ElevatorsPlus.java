package elevatorsplus;

import org.bukkit.Bukkit;
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
import elevatorsplus.mechanic.ElevatorDoorsOpener;
import elevatorsplus.mechanic.ElevatorMoveOperator;
import elevatorsplus.mechanic.ElevatorSignRefresher;
import elevatorsplus.mechanic.MovingTasksExecutor;
import elevatorsplus.mechanic.sound.AmbientSoundPlayer;
import elevatorsplus.ui.MenuBuilder;
import elevatorsplus.ui.MenuListener;
import lombok.Getter;
import ru.soknight.lib.configuration.Messages;
import ru.soknight.lib.logging.PluginLogger;

@Getter
public class ElevatorsPlus extends JavaPlugin {

	@Getter private static ElevatorsPlus instance;
	
	private AmbientSoundPlayer soundPlayer;
	private ElevatorSignRefresher signRefresher;
	private ElevatorDoorsOpener doorsOpener;
	private ElevatorMoveOperator moveOperator;
	private MovingTasksExecutor movingTasksExecutor;
	
	private MenuBuilder menuBuilder;
	
	private DatabaseManager databaseManager;
	private SessionManager sessionManager;
	
	private PluginLogger pluginLogger;
	private Config mainConfig;
	private Messages messages;
	
	@Override
	public void onEnable() {
		instance = this;
		
		this.pluginLogger = new PluginLogger(this);
		
		this.setConfigs();
		
		try {
			Database database = new Database(mainConfig, pluginLogger);
			this.databaseManager = new DatabaseManager(database, pluginLogger);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.sessionManager = new SessionManager();
		this.soundPlayer = new AmbientSoundPlayer(pluginLogger, mainConfig);
		this.menuBuilder = new MenuBuilder(messages, mainConfig.getMenuPattern());
		
		// Command executors registration
		this.registerCommands();
		
		// Moving operators and providers registration
		this.registerMovingOperators();
		
		PluginManager manager = Bukkit.getPluginManager();
		manager.registerEvents(new SelectionSessionListener(mainConfig, messages, sessionManager, databaseManager, soundPlayer), this);
		manager.registerEvents(new LinkingSessionListener(messages, sessionManager, databaseManager), this);
		manager.registerEvents(new ElementsClickListener(this, mainConfig, messages, databaseManager, sessionManager), this);
		manager.registerEvents(new MenuListener(this, messages, sessionManager), this);
		
		if(!mainConfig.getBoolean("allow-controls-destroy"))
			manager.registerEvents(new ElementsDestroyingListener(mainConfig, messages, databaseManager), this);
		
		/*
		 *  Launching elevator platform move task
		 */
		
		double speed = mainConfig.getDouble("moving-speed");
		this.movingTasksExecutor = new MovingTasksExecutor(this, speed);
		
		int frequency = mainConfig.getInt("moving-task-frequency");
		Bukkit.getScheduler().runTaskTimer(this, movingTasksExecutor, 0, frequency);
		
		pluginLogger.info("Oh, it's alive :D");
	}
	
	public void setConfigs() {
		this.mainConfig = new Config(this);
		this.messages = new MessagesProvider(this, mainConfig).getMessages();
	}
	
	public void registerCommands() {
		SubcommandHandler handler = new SubcommandHandler(this, mainConfig, messages);
		
		getCommand("eplus").setExecutor(handler);
		getCommand("eplus").setTabCompleter(handler);
	}
	
	public void registerMovingOperators() {
		this.soundPlayer = new AmbientSoundPlayer(pluginLogger, mainConfig);
		this.signRefresher = new ElevatorSignRefresher(mainConfig, messages);
		this.doorsOpener = new ElevatorDoorsOpener(mainConfig);
		this.moveOperator = new ElevatorMoveOperator(this, messages, soundPlayer, signRefresher, doorsOpener);
	}
	
	@Override
	public void onDisable() {
		if(databaseManager != null) databaseManager.shutdown();
		pluginLogger.info("I'll be back..");
	}
	
}
