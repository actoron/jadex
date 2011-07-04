package sodekovs.util.bike.persistence;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.derby.drda.NetworkServerControl;

/**
 * @author Vilenica
 * 
 */
public class ConnectionMgr {

	private static ConnectionMgr conMgr = null;
	private static Connection conn = null;
	public static NetworkServerControl serverControl= null;

	private ConnectionMgr() {
	}

	public static ConnectionMgr getInstance() {
		if (conMgr == null)
			conMgr = new ConnectionMgr();
		return conMgr;

	}

	private static void newConnection() {
		
		 NetworkServerControl serverControl;             
			try {
				serverControl = new NetworkServerControl(InetAddress.getByName("localhost"),1527);
				serverControl.start(new PrintWriter(System.out,true));
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}   
			
			
		try {
			Class.forName("org.apache.derby.jdbc.ClientDriver");
			Properties properties = new Properties();
			properties.put("user", "user2");
			properties.put("password", "user2");
			// conn = DriverManager.getConnection("jdbc:derby:C:/Users/vilenica/MyDB;create=true", properties);
			conn = DriverManager.getConnection("jdbc:derby://localhost:1527/" + "AnteDB1" + ";create=true", properties);
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
