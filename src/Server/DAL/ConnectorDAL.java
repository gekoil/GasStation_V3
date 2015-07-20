package DAL;

import BL.Car;
import BL.GasStation;
import BL.Pump;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Vector;

public class ConnectorDAL implements IDAL {

	private static IDAL instance;
	private MysqlDataSource dataSource;

	private DateTimeFormatter dateFormat = DateTimeFormatter
			.ofPattern("yyyyMMdd");
	private DateTimeFormatter timeFormat = DateTimeFormatter
			.ofPattern("HHmmss");

	// database connection should be a singleton
	private ConnectorDAL() {
		defineDriver();
		setupDataSource();
	}

	public IDAL getInstance() {
		if (instance == null) {
			instance = new ConnectorDAL();
		}
		return instance;
	}

	@Override
	public boolean addCar(Car car) {
		Connection connection;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			statement.executeUpdate("INSERT INTO cars (ID, GS, WANT_CLEANING, NUM_OF_LITERS, PUMP_NUM) VALUES " +
					"(" + car.getID() + ", " + car.getGasStation().getId() + ", " + car.isWantCleaning() + ", " + car.getNumOfLiters() + ", " + car.getPumpNum() + ")" +
					" ON DUPLICATE KEY UPDATE WANT_CLEANING=VALUES(WANT_CLEANING)");
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	private void defineDriver() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private void setupDataSource() {
		dataSource = new MysqlDataSource();
		dataSource.setURL("jdbc:mysql://localhost/my_db");
		dataSource.setPort(3306);
		dataSource.setDatabaseName("my_db");
		dataSource.setUser("root");
		dataSource.setPassword("");
	}

	@Override
	public boolean checkGasStation(GasStation gs) {
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			ResultSet res = statement
					.executeQuery("SELECT ID FROM gas_stations WHERE ID = " + gs.getId());
			if (!res.first()) {
				int rowCount = statement
						.executeUpdate("INSERT INTO gas_stations (ID, GAS_REVENUE, CLEAN_REVENUE, CARS_FUELED, CARS_CLEANED) VALUES ("
								+ gs.getId() + ", 0, 0, 0, 0)");
				res.close();
				statement.close();
				connection.close();
				setPumps(gs.getPumps(), gs);
				return rowCount > 0;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean setPumps(ArrayList<Pump> pumps, GasStation gs) {
		Connection connection;
		try {
			connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			int rows = 0;
			for (int i = 0; i < pumps.size(); i++)
				rows += statement.executeUpdate(String.format(
						"INSERT INTO pumps (ID, STATION_ID) VALUES (%d, %d)",
						pumps.get(i).getNum(), gs.getId()));
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public boolean storeTransaction(Transaction transaction) {
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			String query;
			if (transaction.type == ServiceType.FUEL) {
				query = String
						.format("INSERT INTO transactions (STATION_ID, CAR_ID, AMOUNT, DATE_ADDED, TIME_ADDED, SERVICE_TYPE, PUMP) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', '%s')",
								transaction.gasStationId, transaction.carId ,transaction.amount,
								dateFormat.format(transaction.timeStamp),
								timeFormat.format(transaction.timeStamp),
								transaction.type.ordinal(), transaction.pump);
			} else { // cleaning service
				query = String
						.format("INSERT INTO transactions (STATION_ID, CAR_ID, AMOUNT, DATE_ADDED, TIME_ADDED, SERVICE_TYPE, PUMP) VALUES ('%s', '%s', '%s', '%s', '%s', '%s', NULL)",
								transaction.gasStationId, transaction.carId ,transaction.amount,
								dateFormat.format(transaction.timeStamp),
								timeFormat.format(transaction.timeStamp),
								transaction.type.ordinal());
			}
			int rowCount = statement.executeUpdate(query);
			statement.close();
			connection.close();
			return rowCount > 0;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}

	@Override
	public Vector<Transaction> getTransactions(LocalDateTime first,
											   LocalDateTime last, int option) {
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();

			Vector<Transaction> trans = new Vector<Transaction>();
			ResultSet res;
			res = statement.executeQuery(setQuery(first, last, option));
			while (res.next()) {
				Transaction tr = new Transaction();
				tr.amount = res.getDouble("SUM");
				tr.pump = res.getInt("PUMP");
				tr.timeStamp = LocalDateTime.of(res.getDate("DATE_ADDED")
						.toLocalDate(), res.getTime("TIME_ADDED").toLocalTime());
				tr.type = (res.getInt("SERVICE_TYPE") == 0 ? ServiceType.FUEL
						: ServiceType.CLEANING);
				trans.add(tr);
			}
			res.close();
			statement.close();
			connection.close();
			return trans;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private String setQuery(LocalDateTime first, LocalDateTime last, int option) {
		String select = "SELECT DATE_ADDED, TIME_ADDED, PUMP, SERVICE_TYPE, SUM(AMOUNT) AS 'SUM' FROM transactions ";
		switch (option) {
		case 1:
			select += "WHERE TIME_ADDED BETWEEN " + timeFormat.format(first)
					+ " AND " + timeFormat.format(last) + " AND DATE_ADDED BETWEEN "
					+ dateFormat.format(first) + " AND "
					+ dateFormat.format(last) + " GROUP BY PUMP";
			break;
		case 2:
			select += "WHERE DATE_ADDED BETWEEN " + dateFormat.format(first)
					+ " AND " + dateFormat.format(last) + " GROUP BY PUMP";
			break;
		case 3:
			select += "WHERE DATE_ADDED BETWEEN " + dateFormat.format(first)
					+ " AND " + dateFormat.format(last) + " GROUP BY DATE_ADDED";
			break;
		default:
			select = "";
		}
		return select;
	}
	
	@Override
	public String getCarFee(int id) {
		String sumFee = "Car No." + id;
		try {
			Connection connection = dataSource.getConnection();
			Statement statement = connection.createStatement();
			//SELECT DATE_ADDED, TIME_ADDED, PUMP, SERVICE_TYPE, SUM(AMOUNT) AS 'SUM' FROM transactions "
			Vector<Transaction> trans = new Vector<Transaction>();
			ResultSet res = statement.executeQuery("SELECT SUM(AMOUNT) AS 'FEE', COUNT(CAR_ID) AS 'SIZE' FROM my_db.transactions WHERE CAR_ID=" + id);
			res.next();
			if(res.getInt("SIZE") != 0)
				sumFee += " has paid " + res.getDouble("FEE") + " in total";
			else
				sumFee += " does not exist.";
			res.close();
			statement.close();
			connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return sumFee;
	}
}
