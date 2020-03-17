package elevatorsplus.database;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.ConnectionSource;

import elevatorsplus.objects.Elevator;
import elevatorsplus.utils.Logger;

public class DatabaseManager {
	
	private ConnectionSource source;
	private Dao<Elevator, String> dao;
	
	public DatabaseManager(Database database) throws SQLException {
		source = database.getConnection();
		dao = DaoManager.createDao(source, Elevator.class);
	}
	
	public void shutdown() {
		try {
			source.close();
			Logger.info("Database connection closed.");
		} catch (IOException e) {
			Logger.error("Failed close database connection: " + e.getLocalizedMessage());
		}
	}
	
	public Elevator getElevator(String name) {
		try {
			return dao.queryForId(name);
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public Elevator getElevatorBySign(String location) {
		try {
			QueryBuilder<Elevator, String> builder = dao.queryBuilder();
			Where<Elevator, String> where = builder.where();
			
			where.eq("signlocation", location);
			
			return builder.queryForFirst();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<Elevator> getAllElevators() {
		try {
			return dao.queryForAll();
		} catch (SQLException e) {
			e.printStackTrace();
			return new ArrayList<>();
		}
	}
	
	public List<Elevator> getElevatorsInWorld(String world) {
		try {
			QueryBuilder<Elevator, String> builder = dao.queryBuilder();
			Where<Elevator, String> where = builder.where();
			
			where.eq("world", world);
			
			return builder.query();
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public List<String> getAllNames() {
		List<String> output = new ArrayList<>();
		List<Elevator> elevators = getAllElevators();
		
		if(elevators.isEmpty()) return output;
		elevators.forEach(e -> output.add(e.getName()));
		return output;
	}
	
	public int createElevator(Elevator elevator) {
		try {
			return dao.create(elevator);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int updateElevator(Elevator elevator) {
		try {
			return dao.update(elevator);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int removeElevator(Elevator elevator) {
		try {
			return dao.delete(elevator);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public int renameElevator(Elevator elevator, String name) {
		try {
			return dao.updateId(elevator, name);
		} catch (SQLException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
}
