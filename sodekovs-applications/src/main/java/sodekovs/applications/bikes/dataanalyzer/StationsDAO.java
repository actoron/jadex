package sodekovs.applications.bikes.dataanalyzer;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sodekovs.applications.bikes.datafetcher.database.DatabaseConnection;
import sodekovs.applications.bikes.model.SystemSnapshot;
import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;

public class StationsDAO {

	private static StationsDAO stationsDAO = null;

//	private PreparedStatement insertNewLog;
//
//	private PreparedStatement createLogTable;

	private PreparedStatement selectAllLogsForCity;
	
//	private PreparedStatement selectTimeRange;
	
	private PreparedStatement readRowSize;
	
	private Connection conn =  DatabaseConnection.getConnection();

//	private static final String ID = "id";

	private static final String CITY = "city";

//	private static final String LOGFILE = "Logfile";

	private static final String LOGTIMESTAMP = "Logtimestamp";

	private StationsDAO() {
		try {
			selectAllLogsForCity = conn.prepareStatement("SELECT * FROM  " + GlobalConstants.BIKE_DB_TABLE + "." + GlobalConstants.BIKE_DB_STATIONS  + " WHERE (" + CITY + " = ?)");
			readRowSize = conn.prepareStatement("SELECT COUNT(*) FROM  " + GlobalConstants.BIKE_DB_TABLE + "." + GlobalConstants.BIKE_DB_STATIONS);			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static StationsDAO getInstance() {
		if (stationsDAO == null) {
			stationsDAO = new StationsDAO();
		}
		return stationsDAO;
	}


	public int getRowSizeOfLogTable() {
		int size = -1;

		synchronized (readRowSize) {

			ResultSet resultSet;
			try {
				resultSet = readRowSize.executeQuery();
				if (resultSet.next()) {
					size = resultSet.getInt(1);
				}
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return size;
	}

	public ArrayList<SystemSnapshot> loadAllLogsForCity(String cityName) {		
		ArrayList<SystemSnapshot> result = null;

		synchronized (selectAllLogsForCity) {

			ResultSet resultSet;
			try {
				selectAllLogsForCity.setString(1, cityName);
				resultSet = selectAllLogsForCity.executeQuery();
				result = toSystemSnapshots(resultSet);
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return result;
	}
	
	private ArrayList<SystemSnapshot> toSystemSnapshots(ResultSet resultSet) {
	ArrayList<SystemSnapshot> result = new ArrayList<SystemSnapshot>();
		try {
			while (resultSet.next()) {
				result.add(toSystemSnapshot(resultSet));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		
		return result;
	}
	
		
	private SystemSnapshot toSystemSnapshot(ResultSet resultSet) {
		SystemSnapshot res = new SystemSnapshot();

		try {
			res.setId(resultSet.getInt(1));
			res.setCityName(resultSet.getString(2));
			res.setTimestamp(Long.valueOf(resultSet.getString(3)));
		
//			String type = resultSet.getString(3);
//			String name = resultSet.getString(4);
//
//			java.io.InputStream mainFileStr = resultSet.getAsciiStream(5);
//			String mainFile = FileHandler.convertStreamToString(mainFileStr);

//			java.io.InputStream log = resultSet.getAsciiStream(2);
//			xmlLog = FileHandler.convertStreamToString(log);
//			String timestamp = resultSet.getString(7);

			// FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp+ ".dat", logEntries);
//			dataDesc = new HistoricDataDescription(name, type, timestamp, logEntries, GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp + ".png", mainFile);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return res;
	}	
}
