package sodekovs.bikesharing.data;

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
				String dbUrl = "jdbc:mysql://141.22.64.3:3306/bicycle_rental";
				String dbUser = "root";

				Scanner s = new Scanner(System.in);
				System.out.println("Please insert the password to connect to " + dbUrl + " as user " + dbUser + ":");

				String dbPwd = s.nextLine();

				// Version for local instance

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
		}

		return connection;
	}
}