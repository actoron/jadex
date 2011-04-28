package sodekovs.util.gnuplot.persistence;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import sodekovs.util.misc.GlobalConstants;

/**
 * @author Vilenica 
 * 
 */
public class ConnectionMgr {

	private static ConnectionMgr conMgr = null;
	private static Connection conn = null;

	private ConnectionMgr() {
	}

	public static ConnectionMgr getInstance() {
		if (conMgr == null)
			conMgr = new ConnectionMgr();
		return conMgr;

	}

	private static void newConnection() {
		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			Properties properties = new Properties();
			properties.put("user", GlobalConstants.DB_GNUPLOT_SCHEMA);
			properties.put("password", "user");
			conn = DriverManager.getConnection("jdbc:derby:C:/Users/vilenica/MyDB;create=true", properties);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

	/**
	 * @return returns a connection to the db used for your application
	 * @throws SQLException
	 */
	public static Connection getConnection() {
		if (conn == null) {
			newConnection();
		}
		return conn;
	}

	/**
	 * Closes current connection to the db
	 */
	public void closeConnection() {
		try {
			conn.close();
			conn = null;
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
