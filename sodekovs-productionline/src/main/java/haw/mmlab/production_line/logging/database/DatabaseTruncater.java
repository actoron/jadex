package haw.mmlab.production_line.logging.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Helper class for truncating the tables in the database.
 * 
 * @author thomas
 */
public class DatabaseTruncater {

	/**
	 * Array with the name of the tables in the database.
	 */
	private static final String[] TABLES = { "IntervalTime", "Log", "Run", "RunMetadata", "RunMessageHops" };

	/**
	 * Main method.
	 * 
	 * @param args
	 *            - a string array of table names, if the class is started
	 *            without any parameters the {@link DatabaseTruncater#TABLES}
	 *            array is used.
	 */
	public static void main(String[] args) {
		System.out.println("Starting DatabaseTruncater");

		DatabaseTruncater truncater = new DatabaseTruncater();

		if (args.length > 0) {
			System.out.println(" with given args.");
			truncater.truncateTables(args);
		} else {
			System.out.println(" truncating default tables.");
			truncater.truncateTables(TABLES);
		}
		System.out.println("DatabaseTruncater is finish.");
	}

	/**
	 * @return - the {@link Connection} to the database from the
	 *         {@link DatabaseConnection}
	 */
	private Connection getConnection() {
		return DatabaseConnection.getConnection();
	}

	/**
	 * Truncates the given table.
	 * 
	 * @param table
	 *            - the given table.
	 */
	private void truncateTable(String table) {
		String sql = "TRUNCATE Table " + table;

		try {
			Statement statement = getConnection().createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Truncates all the given tables.
	 * 
	 * @param tables
	 *            - the given tables.
	 */
	private void truncateTables(String[] tables) {
		for (String table : tables) {
			truncateTable(table);
		}
	}
}