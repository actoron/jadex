package sodekovs.util.gnuplot.persistence;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import sodekovs.util.gnuplot.CreateImagesThread;
import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.model.benchmarking.description.HistoricDataDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

public class DataDAO {

	private static DataDAO dataDAO = null;

	private PreparedStatement insertNewLog;

	private PreparedStatement createLogTable;

	private PreparedStatement selectAllLogs;
	
	private PreparedStatement readRowSize;

	private Connection conn = ConnectionMgr.getConnection();

	private static final String ID = "id";

	private static final String DATE = "Date";

	private static final String TYPE = "Type";

	private static final String NAME = "Name";

	private static final String MAINFILE = "Mainfile";

	private static final String LOGFILE = "Logfile";

	private static final String LOGTIMESTAMP = "Logtimestamp";

	private DataDAO() {
		try {
			createLogTable = conn.prepareStatement("create table " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE + " (" + ID
					+ " integer not null primary key generated always as identity (start with 1, increment by 1), " + DATE + " timestamp, " + TYPE + " varchar(64), " + NAME + " varchar(64), "
					+ MAINFILE + " CLOB(64 K), " + LOGFILE + " CLOB(64 K), " + LOGTIMESTAMP + " varchar(20))");
			createTableTestIfItDoesntExistYet();
			insertNewLog = conn.prepareStatement("INSERT INTO " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE + " (" + DATE + ", " + TYPE + ", " + NAME + ", "
					+ MAINFILE + ", " + LOGFILE + " , " + LOGTIMESTAMP + ") VALUES (?,?,?,?,?,?)");
			selectAllLogs = conn.prepareStatement("SELECT * FROM  " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
			readRowSize = conn.prepareStatement("SELECT COUNT(*) FROM  " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);			
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static DataDAO getInstance() {
		if (dataDAO == null) {
			dataDAO = new DataDAO();
		}
		return dataDAO;
	}

	public void insertNewGnuPlotLog(String fileName, String benchType, String benchName, String logtimestamp) {

		synchronized (insertNewLog) {
			try {

				java.io.File gnuPlotMainFile = new java.io.File(fileName + "plt");
				java.io.InputStream gnuPlotMainFileInStr = new java.io.FileInputStream(gnuPlotMainFile);
				java.io.File gnuPlotDataFile = new java.io.File(fileName + "dat");
				java.io.InputStream gnuPlotDataFileInStr = new java.io.FileInputStream(gnuPlotDataFile);

				insertNewLog.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
				insertNewLog.setString(2, benchType);
				insertNewLog.setString(3, benchName);
				insertNewLog.setAsciiStream(4, gnuPlotMainFileInStr, (int) gnuPlotMainFile.length());
				insertNewLog.setAsciiStream(5, gnuPlotDataFileInStr, (int) gnuPlotDataFile.length());
				insertNewLog.setString(6, logtimestamp);
				int res = insertNewLog.executeUpdate();
				insertNewLog.close();
				// conn.commit();
				System.out.println("DAO insert done: " + res);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void createTableTestIfItDoesntExistYet() throws Exception {
		ResultSet resultSet = conn.getMetaData().getTables("%", "%", "%", new String[] { "TABLE" });
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			if (resultSet.getString("TABLE_SCHEM").equalsIgnoreCase(GlobalConstants.DB_GNUPLOT_SCHEMA) && resultSet.getString("TABLE_NAME").equalsIgnoreCase(GlobalConstants.DB_GNUPLOT_LOGTABLE)) {
				shouldCreateTable = false;
			}
		}
		resultSet.close();
		if (shouldCreateTable) {
			System.out.println("Creating Table : " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
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
	
	public IHistoricDataDescription[] loadAllLogs() {
		IHistoricDataDescription[] resArray = new IHistoricDataDescription[getRowSizeOfLogTable()];

		synchronized (selectAllLogs) {

			ResultSet resultSet;
			try {
				resultSet = selectAllLogs.executeQuery();
				resArray = toIHistoricDataDescriptionArray(resultSet);
				resultSet.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Create the PNG image of the history
		new CreateImagesThread(resArray).run();
		return resArray;
	}

	private IHistoricDataDescription[] toIHistoricDataDescriptionArray(ResultSet resultSet) {
		List<IHistoricDataDescription> list = toIHistoricDataDescriptionList(resultSet);
		IHistoricDataDescription[] desc = new IHistoricDataDescription[list.size()];
		desc = list.toArray(desc);
		
		return desc;

	}
	
	private List<IHistoricDataDescription> toIHistoricDataDescriptionList(ResultSet resultSet) {
		List<IHistoricDataDescription> histDataList = new ArrayList<IHistoricDataDescription>();
		
		try {
			while (resultSet.next()) {
				histDataList.add(toIHistoricDataDescription(resultSet));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return histDataList;
	}
	
	private IHistoricDataDescription toIHistoricDataDescription(ResultSet resultSet) {
		IHistoricDataDescription dataDesc = null;

		try {
			String type = resultSet.getString(3);
			String name = resultSet.getString(4);

			java.io.InputStream mainFileStr = resultSet.getAsciiStream(5);
			String mainFile = FileHandler.convertStreamToString(mainFileStr);

			java.io.InputStream dataFile = resultSet.getAsciiStream(6);
			String logEntries = FileHandler.convertStreamToString(dataFile);
			String timestamp = resultSet.getString(7);

			// FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp+ ".dat", logEntries);
			dataDesc = new HistoricDataDescription(name, type, timestamp, logEntries, GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp + ".png", mainFile);
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return dataDesc;
	}	
}
