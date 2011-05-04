package haw.mmlab.production_line.logging.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Helper class for truncating the tables in the database to remove a special
 * run.
 * 
 * @author thomas
 */
public class DatabaseRunTruncater {

	public static void main(String[] args) {
		System.out.println("Please insert the id of the run you would like to delete:");

		try {
			Scanner s = new Scanner(System.in);
			String input = s.next();

			int runId = Integer.parseInt(input);

			DatabaseRunTruncater truncater = new DatabaseRunTruncater();
			truncater.cleanRunMetadata(runId);
			truncater.cleanRunMessageHops(runId);
			truncater.cleanLog(runId);
			truncater.truncateIntervalTime();

			System.out.println("...finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return - the {@link Connection} to the database from the
	 *         {@link DatabaseConnection}
	 */
	private Connection getConnection() {
		return DatabaseConnection.getConnection();
	}

	private void cleanRunMetadata(int runId) {
		String sql = "DELETE FROM RunMetadata WHERE runid=" + runId;

		try {
			Statement statement = getConnection().createStatement();
			statement.execute(sql);
			System.out.println("Deleted runId " + runId + " from RunMetadata.");
		} catch (SQLException e) {
			System.out.println("Could not delete runId " + runId + " from RunMetadata!");
			e.printStackTrace();
		}
	}

	private void cleanRunMessageHops(int runId) {
		String sql = "DELETE FROM RunMessageHops WHERE runid=" + runId;

		try {
			Statement statement = getConnection().createStatement();
			statement.execute(sql);
			System.out.println("Deleted runId " + runId + " from RunMessageHops.");
		} catch (SQLException e) {
			System.out.println("Could not delete runId " + runId + " from RunMessageHops!");
			e.printStackTrace();
		}
	}

	private void cleanLog(int runId) {
		String sql = "DELETE FROM Log WHERE runid=" + runId;

		try {
			Statement statement = getConnection().createStatement();
			statement.execute(sql);
			System.out.println("Deleted runId " + runId + " from Log.");
		} catch (SQLException e) {
			System.out.println("Could not delete runId " + runId + " from Log!");
			e.printStackTrace();
		}
	}

	private void truncateIntervalTime() {
		String sql = "TRUNCATE Table IntervalTime";

		try {
			Statement statement = getConnection().createStatement();
			statement.execute(sql);
			System.out.println("Truncated IntervalTime table.");
		} catch (SQLException e) {
			System.out.println("Could not truncate IntervalTime table!");
			e.printStackTrace();
		}
	}
}