package elevatorsplus.database;

import java.io.File;
import java.sql.SQLException;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import elevatorsplus.ElevatorsPlus;
import elevatorsplus.configuration.Config;
import ru.soknight.lib.logging.PluginLogger;

public class Database {

	private String url;
	private String host;
	private String name;
	private String user;
	private String password;
	private String file;
	private boolean useSQLite;
	private int port;
	
	public Database(Config config, PluginLogger logger) throws Exception {
		useSQLite = config.getBoolean("database.use-sqlite", true);
		if(!useSQLite) {
			host = config.getString("database.host", "localhost");
			name = config.getString("database.name", "elevatorsplus");
			user = config.getString("database.user", "admin");
			password = config.getString("database.password", "eplus");
			port = config.getInt("database.port", 3306);
			url = "jdbc:mysql://" + host + ":" + port + "/" + name;
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} else {
			file = config.getString("database.file", "database.db");
			url = "jdbc:sqlite:" + ElevatorsPlus.getInstance().getDataFolder() + File.separator + file;
			Class.forName("org.sqlite.JDBC").newInstance();
		}
		
		// Allowing only ORMLite errors logging
		System.setProperty("com.j256.ormlite.logger.type", "GLOBAL");
		System.setProperty("com.j256.ormlite.logger.level", "ERROR");
				
		ConnectionSource source = getConnection();

		TableUtils.createTableIfNotExists(source, Elevator.class);
		
		source.close();
		
		logger.info("Database type " + (useSQLite ? "SQLite" : "MySQL") + " connected.");
	}
	
	public ConnectionSource getConnection() throws SQLException {
		return useSQLite ? new JdbcConnectionSource(url, user, password) : new JdbcConnectionSource(url);
	}
	
}
