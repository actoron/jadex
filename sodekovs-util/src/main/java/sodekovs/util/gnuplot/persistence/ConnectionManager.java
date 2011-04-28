package sodekovs.util.persistence;

/**
 * 
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Properties;

import sodekovs.util.gnuplot.CreateImagesThread;
import sodekovs.util.gnuplot.GnuPlotHandler;
import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.FileHandler;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.model.benchmarking.description.HistoricDataDescription;
import sodekovs.util.model.benchmarking.description.IHistoricDataDescription;

/**
 * @author Vilenica
 */
public class ConnectionManager {

	// private ConnectionManager conMgr = new ConnectionManager();
	private Connection connection = null;

	public ConnectionManager() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Properties properties = new Properties();
			properties.put("user", GlobalConstants.DB_GNUPLOT_SCHEMA);
			properties.put("password", "user");
			// Connection connection = DriverManager.getConnection("jdbc:derby:c:/TEMP/tutorialsDB;create=true", properties);
			connection = DriverManager.getConnection("jdbc:derby:C:/Users/vilenica/MyDB;create=true", properties);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// public static ConnectionManager getInstance() {
	// return conMgr;
	// }

	/**
	 * 
	 * @return
	 */
	public synchronized void executeStatement(String statement) {
		try {
			createTableTestIfItDoesntExistYet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Populating Table " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE + " VALUES (?,?,?)");
			// String[] data = { "AAA", "BBB", "CCC", "DDD", "EEE" };
			// for (int i = 0; i < data.length; i++) {
			java.io.File file = new java.io.File("asciifile.txt");
			int fileLength = (int) file.length();

			// - first, create an input stream
			java.io.InputStream fin = new java.io.FileInputStream(file);

			preparedStatement.setInt(1, GetRandom.getRandom(10000));
			// preparedStatement.setString(2, statement);
			preparedStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setAsciiStream(3, fin, fileLength);
			preparedStatement.execute();
			// }
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @return
	 */
	public synchronized void storeGnuPlotLogs(String fileName, String benchType, String benchName, String logtimestamp) {

		try {
			createTableTestIfItDoesntExistYet();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("Populating Table " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
		PreparedStatement preparedStatement;
		try {
			preparedStatement = connection.prepareStatement("INSERT INTO " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE
					+ " (Date, Type, Name, Mainfile,Logfile,Logtimestamp) VALUES (?,?,?,?,?,?)");

			java.io.File gnuPlotMainFile = new java.io.File(fileName + "plt");
			java.io.InputStream gnuPlotMainFileInStr = new java.io.FileInputStream(gnuPlotMainFile);
			java.io.File gnuPlotDataFile = new java.io.File(fileName + "dat");
			java.io.InputStream gnuPlotDataFileInStr = new java.io.FileInputStream(gnuPlotDataFile);

			// preparedStatement.setInt(1, GetRandom.getRandom(10000));
			preparedStatement.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
			preparedStatement.setString(2, benchType);
			preparedStatement.setString(3, benchName);
			preparedStatement.setAsciiStream(4, gnuPlotMainFileInStr, (int) gnuPlotMainFile.length());
			preparedStatement.setAsciiStream(5, gnuPlotDataFileInStr, (int) gnuPlotDataFile.length());
			preparedStatement.setString(6, logtimestamp);
			preparedStatement.execute();
			// }
			preparedStatement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createTableTestIfItDoesntExistYet() throws Exception {
		ResultSet resultSet = connection.getMetaData().getTables("%", "%", "%", new String[] { "TABLE" });
		// int columnCnt = resultSet.getMetaData().getColumnCount();
		boolean shouldCreateTable = true;
		while (resultSet.next() && shouldCreateTable) {
			if (resultSet.getString("TABLE_SCHEM").equalsIgnoreCase(GlobalConstants.DB_GNUPLOT_SCHEMA) && resultSet.getString("TABLE_NAME").equalsIgnoreCase(GlobalConstants.DB_GNUPLOT_LOGTABLE)) {
				shouldCreateTable = false;
			}
		}
		resultSet.close();
		if (shouldCreateTable) {
			System.out.println("Creating Table : " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
			Statement statement = connection.createStatement();
			// logtimestamp: is required since it serves as link between the dat and plt file. both files contain this string as part of its file name! it is used as an "id".
			statement
					.executeUpdate("create table "
							+ GlobalConstants.DB_GNUPLOT_SCHEMA
							+ "."
							+ GlobalConstants.DB_GNUPLOT_LOGTABLE
							+ " (id integer not null primary key generated always as identity (start with 1, increment by 1), date timestamp, type varchar(64), name varchar(64), mainfile CLOB(64 K), logfile CLOB(64 K), logtimestamp varchar(20))");
			statement.close();
		}
	}


//	public synchronized void readTable() {
//		Statement statement;
//		try {
//			statement = connection.createStatement();
//			ResultSet rs = statement.executeQuery("SELECT * FROM  " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
//			while (rs.next()) {
//				// java.sql.Clob aclob = rs.getClob(1);
//
//				java.io.InputStream dataFile = rs.getAsciiStream(6);
//				String fileAsString = FileHandler.convertStreamToString(dataFile);
//				FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + rs.getString(7) + ".dat", fileAsString);
//				// int c = ip.read();
//				// while (c > 0) {
//				// System.out.print((char) c);
//				// c = ip.read();
//				// }
//				java.io.InputStream mainFile = rs.getAsciiStream(5);
//				String test = FileHandler.convertStreamToString(mainFile);
//				System.out.println("Begin\n" + test);
//				System.out.println("End");
//
//				String[] testArray = test.split("\t");
//				System.out.println("Begin2");
//
//				for (String a : testArray) {
//					System.out.println(a);
//				}
//				System.out.println("End2");
//				GnuPlotHandler.exec(testArray);
//
//			}
//			rs.close();
//			statement.close();
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

	public synchronized IHistoricDataDescription[] getLog() {
		Statement statement;
		IHistoricDataDescription[] res = new IHistoricDataDescription[getRowSize()];
		
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT * FROM  " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);		
			int counter = 0;
			while (rs.next()) {
				
				
				String type = rs.getString(3);
				String name = rs.getString(4);				
				
				java.io.InputStream mainFileStr = rs.getAsciiStream(5);
				String mainFile = FileHandler.convertStreamToString(mainFileStr);
				
				java.io.InputStream dataFile = rs.getAsciiStream(6);				
				String logEntries = FileHandler.convertStreamToString(dataFile);
				String timestamp = rs.getString(7);
				
//				FileHandler.writeToFile(GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp+ ".dat", logEntries);
												
				res[counter] = new HistoricDataDescription(name, type, timestamp, logEntries, GlobalConstants.LOGGING_DIRECTORY + "\\" + timestamp+ ".png", mainFile);
				counter++;								
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Create the PNG image of the history
		new CreateImagesThread(res).run();
		
		return res;
	}

	public synchronized int getRowSize() {
		Statement statement;
		int ret = -1;
		try {
			statement = connection.createStatement();
			ResultSet rs = statement.executeQuery("SELECT COUNT(*) FROM  " + GlobalConstants.DB_GNUPLOT_SCHEMA + "." + GlobalConstants.DB_GNUPLOT_LOGTABLE);
			if (rs.next()) {
				ret = rs.getInt(1);
			}
			rs.close();
			statement.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ret;
	}
}