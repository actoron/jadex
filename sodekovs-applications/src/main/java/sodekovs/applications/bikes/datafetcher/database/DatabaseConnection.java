package sodekovs.applications.bikes.datafetcher.database;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Utility class holding the {@link Connection} to the database.
 * 
 * @author Thomas Preisler
 */
public class DatabaseConnection {
	
	/**
	 * The Path to the database properties file
	 */
	public static String DB_FILE = null;	

	/**
	 * The {@link Connection} to the Database
	 */
	private static Connection connection = null;

	/**
	 * Return the {@link Connection} to the Database, if the connection is <code>null</code> or closed a new connection will be opened.
	 * 
	 * @return the connection to the Database.
	 */
	public static Connection getConnection() {
		try {
			if (connection == null || connection.isClosed()) {
				
				
				Properties properties = new Properties();
				//Version for server
//				properties.load(new FileInputStream(DB_FILE));
				properties.load(new FileInputStream("../sodekovs-applications/src/main/java/sodekovs/applications/bikes/datafetcher/database.properties"));
				

				String dbUrl = properties.getProperty("database.url");
				String dbUser = properties.getProperty("database.user");
				String dbPwd = properties.getProperty("database.password");
				
				//Version for local instance

				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connection = DriverManager.getConnection(dbUrl, dbUser, dbPwd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return connection;
	}
}