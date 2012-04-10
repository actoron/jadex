package sodekovs.graphanalysis.database;

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
				properties.load(new FileInputStream("../sodekovs-applications/src/main/java/sodekovs/graphanalysis/database.properties"));

				String dbUrl = properties.getProperty("database.url");
				String dbUser = properties.getProperty("database.user");
				String dbPwd = properties.getProperty("database.password");

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