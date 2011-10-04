package haw.mmlab.production_line.logging.database;

import haw.mmlab.production_line.domain.HelpRequest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Utility class for operation on the database.
 * 
 * @author thomas
 */
public class DatabaseLogger {

	private boolean loggingEnabled = false;

	private int runId = 0;

	public DatabaseLogger() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("database.properties"));
			this.setLoggingEnabled(Boolean.valueOf(properties.getProperty("logging.enabled")));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return - the {@link Connection} to the database from the {@link DatabaseConnection}
	 */
	private Connection getConnection() {
		return DatabaseConnection.getConnection();
	}

	/**
	 * Initializes the Connection to the Database and archives all the data from the Log Table in the Archive_Log Table.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// initialize the connection
		System.out.println("Initializing the Database Connection.");
		DatabaseLogger instance = new DatabaseLogger();
		if (instance.isLoggingEnabled()) {
			instance.getConnection();

			instance.clearIntervalTime();

			int runId = instance.getRunId();
			instance.setRunId(runId + 1);

			System.out.println("Closing the Database Connection.");
			instance.closeConnection();
		}
	}

	/**
	 * Inserts a new run id in the Run table.
	 * 
	 * @param i
	 *            - the new run id.
	 * @return true if the new value was successfully inserted, else false.
	 */
	private boolean setRunId(int i) {
		String query;

		if (i == 1) {
			query = "INSERT INTO Run(currentRun) VALUES (" + i + ")";
		} else {
			query = "UPDATE Run SET currentRun=" + i + " WHERE currentRUN=" + (i - 1);
		}

		Statement st;
		try {
			st = getConnection().createStatement();

			System.out.println("setting next run id to: " + i);
			runId = i;
			return st.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Returns the Id for the next run from the Run Table.
	 * 
	 * @return the next run id or 0 in case an error occurs or the table does not contain a value
	 */
	public int getRunId() {
		runId = 0;

		if (isLoggingEnabled()) {

			String query = "SELECT currentRun FROM Run";
			try {
				Statement st = getConnection().createStatement();
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					runId = rs.getInt("currentRun");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}

			// System.out.println("current run id is: " + runId);
		}

		return runId;
	}

	/**
	 * Returns the current Time from the Logical_Time Table.
	 * 
	 * @return the current logical time
	 */
	public int getCurrentTime() {
		int time = 0;

		if (loggingEnabled) {

			String query = "SELECT currentTime FROM IntervalTime";
			try {
				Statement st = getConnection().createStatement();
				ResultSet rs = st.executeQuery(query);

				while (rs.next()) {
					time = rs.getInt("currentTime");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return time;
	}

	/**
	 * Closes the {@link Connection} to the Database.
	 */
	public void closeConnection() {
		if (loggingEnabled) {
			try {
				getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Inserts the given data in the Log table in the database.
	 * 
	 * @param agentId
	 *            - the agent id.
	 * @param agentType
	 *            - the agent's type.
	 * @param intervalTime
	 *            - the logical time.
	 * @param mainState
	 *            - the agent's main state.
	 * @param deficientState
	 *            - the agent's deficient state.
	 * @param noRoles
	 *            - the number of role the agent has
	 * @param bufferLoad
	 *            - the number of elements in the buffer
	 * @param bufferCapacity
	 *            - the capacity (max size) of the buffer
	 * @return true if the data was successfully logged in the database, else false.
	 */
	public boolean insertLog(String agentId, String agentType, int intervalTime, int mainState, int deficientState, int noRoles, int bufferLoad, int bufferCapacity) {
		if (loggingEnabled) {
			String insert = "INSERT INTO Log(runid, agentId, agentType, intervalTime, mainState, deficientState, noRoles, bufferLoad, bufferCapacity) VALUES(";
			StringBuilder sb = new StringBuilder(insert);
			sb.append("'" + getRunId() + "'" + ",");
			sb.append("'" + agentId + "'" + ",");
			sb.append("'" + agentType + "'" + ",");
			sb.append(intervalTime + ",");
			sb.append(mainState + ",");
			sb.append(deficientState + ",");
			sb.append(noRoles + ",");
			sb.append(bufferLoad + ",");
			sb.append(bufferCapacity + ")");

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sb.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Updates the currentTime value in the Logical_Time table to keep track of the logical time.
	 * 
	 * @param time
	 *            - the new logical time value.
	 * @return true if the new value was successfully inserted, else false.
	 */
	public boolean setIntervalTime(int time) {
		if (loggingEnabled) {
			String query;

			if (time == 0) {
				query = "INSERT INTO IntervalTime(currentTime) VALUES (" + time + ")";
			} else {
				query = "UPDATE IntervalTime SET currentTime=" + time + " WHERE currentTime=" + (time - 1);
			}

			Statement st;
			try {
				st = getConnection().createStatement();

				return st.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Truncates all the data from the Logical_Time Table.
	 * 
	 * @return true if successful, else false
	 */
	private boolean clearIntervalTime() {
		String query = "TRUNCATE TABLE IntervalTime";

		try {
			Statement stmt = getConnection().createStatement();
			return stmt.execute(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * @return the loggingEnabled
	 */
	public boolean isLoggingEnabled() {
		return loggingEnabled;
	}

	/**
	 * @param loggingEnabled
	 *            the loggingEnabled to set
	 */
	public void setLoggingEnabled(boolean loggingEnabled) {
		this.loggingEnabled = loggingEnabled;
	}

	/**
	 * Cleans up the database.
	 * 
	 * @return the id of the last run which is copied to the archive table or 0 if logging was not enabled.
	 */
	public int cleanupDatabase() {
		if (isLoggingEnabled()) {
			clearIntervalTime();

			int runId = getRunId();
			setRunId(runId + 1);

			closeConnection();

			return runId;
		}

		return 0;
	}

	/**
	 * Inserts the meta data of a run into the database.
	 * 
	 * @param redundancyRate
	 *            The redundancy rate of the capabilities.
	 * @param robotCount
	 *            The count of robots
	 * @param transportCount
	 *            The count of transports
	 * @param capabilityCount
	 *            The count of available capabilities
	 * @param roleCount
	 *            The count of roles
	 * @param strategy
	 *            The applied strategy
	 * @param workload
	 *            The middled workload
	 * 
	 * @return <code>true</code> if the meta data was inserted.
	 */
	public boolean insertMetadata(double redundancyRate, int robotCount, int transportCount, int capabilityCount, int roleCount, String strategy, double workload) {
		if (isLoggingEnabled()) {
			StringBuffer sql = new StringBuffer("INSERT INTO RunMetadata (runid, redundancyRate, ");
			sql.append("robotCount, transportCount, capabilityCount, roleCount, strategy, workload)");
			sql.append("VALUES (");
			sql.append(getRunId());
			sql.append(", ");
			sql.append(redundancyRate);
			sql.append(", ");
			sql.append(robotCount);
			sql.append(", ");
			sql.append(transportCount);
			sql.append(", ");
			sql.append(capabilityCount);
			sql.append(", ");
			sql.append(roleCount);
			sql.append(",  ");
			sql.append("'" + strategy + "',");
			sql.append(workload + ")");

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Marks the current run as a error run (a run which ended with an error condition).
	 * 
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public boolean setErrorRun() {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET runError=1 WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Increments the roleChangeCount value by 1 in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param count
	 *            Number of roes that have been changed.
	 * 
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public boolean incrementRoleChangeAction(int count) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET roleChangeCount=roleChangeCount+" + count + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Increments the messageCount value by the given increment value in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param increment
	 *            the value by which should by incremented
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public boolean incrementMessageCountBy(int increment) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET messageCount=messageCount+" + increment + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Stores the distance (hop count) of the given {@link HelpRequest} in the database.
	 * 
	 * @param request
	 *            - the given {@link HelpRequest}
	 * @return <code>true</code> if the role change distance was inserted, else <code>false</code>
	 */
	public boolean storeRoleChangeDistance(HelpRequest request) {
		if (isLoggingEnabled()) {
			StringBuilder sb = new StringBuilder("INSERT INTO RunMessageHops(runid, messageType, sender, hops) VALUES(");
			sb.append(getRunId());
			sb.append(", ");
			sb.append("'" + request.getClass().getName() + "', ");
			sb.append("'" + request.getAgentId() + "', ");
			sb.append(request.getHopCount());
			sb.append(")");

			String sql = sb.toString();
			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}

	/**
	 * Increment the overall hopCount value by the given increment value in the RunMetadata table where the runid is like the current runid.
	 * 
	 * @param increment
	 *            the value by which should be incremented
	 * @return <code>true</code> if the value was successfully updated, else <code>false</code>
	 */
	public boolean incrementHopCount(int increment) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET hopCount=hopCount+" + increment + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				return stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return false;
	}
}