package sodekovs.util.bike.persistence;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sodekovs.util.gnuplot.persistence.ConnectionMgr;
import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.model.benchmarking.description.HistoricDataDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

public class CapitalBikesLogDAO {

	private static CapitalBikesLogDAO capitalBikesLogDAO = null;

	private PreparedStatement insertNewLog;

	private PreparedStatement createLogTable;

	private PreparedStatement selectAllLogs;
	
	private PreparedStatement readRowSize;

	private Connection conn = ConnectionMgr.getConnection();

	private static final String ID = "id";

//	private static final String DATE = "Date";
//
//	private static final String TYPE = "Type";
//
//	private static final String NAME = "Name";
//
//	private static final String MAINFILE = "Mainfile";

	private static final String LOGFILE = "Logfile";

	private static final String LOGTIMESTAMP = "Logtimestamp";

	private CapitalBikesLogDAO() {
		try {
			createLogTable = conn.prepareStatement("create table " + GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA + "." + GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE + " (" + ID
					+ " integer not null primary key generated always as identity (start with 1, increment by 1), " + LOGFILE + " CLOB(64 K), " + LOGTIMESTAMP + " varchar(40))");
			createTableTestIfItDoesntExistYet();
			insertNewLog = conn.prepareStatement("INSERT INTO " + GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA + "." + GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE + " (" + LOGFILE + " , " + LOGTIMESTAMP + ") VALUES (?,?)");
			selectAllLogs = conn.prepareStatement("SELECT * FROM  " + GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA + "." + GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE);
			readRowSize = conn.prepareStatement("SELECT COUNT(*) FROM  " + GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA + "." + GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE);			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static CapitalBikesLogDAO getInstance() {
		if (capitalBikesLogDAO == null) {
			capitalBikesLogDAO = new CapitalBikesLogDAO();
		}
		return capitalBikesLogDAO;
	}

	public void insertNewLog(String xmlLog) {

		synchronized (insertNewLog) {
			try {			
				InputStream inSt = new ByteArrayInputStream(xmlLog.getBytes());
				
				insertNewLog.setAsciiStream(1, inSt, xmlLog.length());									
				insertNewLog.setString(2, String.valueOf(System.currentTimeMillis()));
				int res = insertNewLog.executeUpdate();
//				insertNewLog.close();
				// conn.commit();
				System.out.println("CapBikeDAO insert done: " + res);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();		
			}
		}
	}

	private void createTableTestIfItDoesntExistYet() throws Exception {
		ResultSet resultSet = conn.getMetaData().getTables("%", "%", "%", new String[] { "TABLE" });
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			if (resultSet.getString("TABLE_SCHEM").equalsIgnoreCase(GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA) && resultSet.getString("TABLE_NAME").equalsIgnoreCase(GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE)) {
				shouldCreateTable = false;
			}
		}
		resultSet.close();
		if (shouldCreateTable) {
			System.out.println("Creating Table : " + GlobalConstants.CAPITAL_BIKE_SHARE_SCHEMA + "." + GlobalConstants.CAPITAL_BIKE_SHARE_LOGTABLE);
			// logtimestamp: is required since it serves as link between the dat and plt file. both files contain this string as part of its file name! it is used as an "id".
			int res = createLogTable.executeUpdate();
			createLogTable.close();
		}
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

	public String[] loadAllLogs() {		
		String[] resArray = new String[getRowSizeOfLogTable()];

		synchronized (selectAllLogs) {

			ResultSet resultSet;
			try {
				resultSet = selectAllLogs.executeQuery();
//				resArray = toIHistoricDataDescriptionArray(resultSet);
				resArray = toXMLLogs(resultSet);
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return resArray;
	}
//	
//	private String[] toXMLLogs(ResultSet resultSet) {
////	private IHistoricDataDescription[] toIHistoricDataDescriptionArray(ResultSet resultSet) {
//		List<String> list = toXMLLog(resultSet);
//		IHistoricDataDescription[] desc = new IHistoricDataDescription[list.size()];
//		desc = list.toArray(desc);
//		
//		return desc;

//	}
	
	private String[] toXMLLogs(ResultSet resultSet) {
		ArrayList<String> tmp = new ArrayList<String>();
		
		try {
			while (resultSet.next()) {
				tmp.add(toXMLLog(resultSet));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String[] res = new String[tmp.size()];
		res  = tmp.toArray(res);
		return res;
	}
	
	private String toXMLLog(ResultSet resultSet) {
		String xmlLog = null;

		try {
//			String type = resultSet.getString(3);
//			String name = resultSet.getString(4);
//
//			java.io.InputStream mainFileStr = resultSet.getAsciiStream(5);
//			String mainFile = FileHandler.convertStreamToString(mainFileStr);

			java.io.InputStream log = resultSet.getAsciiStream(2);
			xmlLog = FileHandler.convertStreamToString(log);
//			String timestamp = resultSet.getString(7);

			// FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp+ ".dat", logEntries);
//			dataDesc = new HistoricDataDescription(name, type, timestamp, logEntries, GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp + ".png", mainFile);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return xmlLog;
	}	
}
