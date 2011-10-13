package sodekovs.applications.bikes.london;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

/**
 * Utility class holding the {@link Connection} to the database.
 * 
 * @author Thomas Preisler
 */
public class DatabaseConnection {

	private static final String DB_URL = "jdbc:mysql://192.168.2.104:3306/london";

	private static final String DB_USER = "root";

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
				System.out.println("Please insert the password to access " + DB_URL + " as " + DB_USER);
				Scanner s = new Scanner(System.in);
				String password = s.next();

				Class.forName("com.mysql.jdbc.Driver").newInstance();
				connection = DriverManager.getConnection(DB_URL, DB_USER, password);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return connection;
	}
}