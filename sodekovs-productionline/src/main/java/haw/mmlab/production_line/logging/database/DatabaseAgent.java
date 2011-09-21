/**
 * 
 */
package haw.mmlab.production_line.logging.database;

import haw.mmlab.production_line.domain.HelpRequest;
import haw.mmlab.production_line.service.DatabaseService;
import haw.mmlab.production_line.service.IDatabaseService;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author thomas
 * 
 */
@Description("This agent handles the connection to the database.")
@ProvidedServices(@ProvidedService(type = IDatabaseService.class, implementation = @Implementation(DatabaseService.class)))
public class DatabaseAgent extends MicroAgent {

	private boolean loggingEnabled = false;

	private int runId = 0;

	@Override
	public IFuture<Void> agentCreated() {
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream("database.properties"));
			loggingEnabled = Boolean.valueOf(properties.getProperty("logging.enabled"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return IFuture.DONE;
	}

	/**
	 * @return - the {@link Connection} to the database from the {@link DatabaseConnection}
	 */
	private Connection getConnection() {
		return DatabaseConnection.getConnection();
	}

	/**
	 * @return the loggingEnabled
	 */
	public boolean isLoggingEnabled() {
		return loggingEnabled;
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

	public void insertMetadata(double redundancyRate, int robotCount, int transportCount, int capabilityCount, int roleCount, String strategy, double workload) {
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
				stmt.execute(sql.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertLog(String agentId, String agentType, int intervalTime, int mainState, int deficientState, int noRoles, int bufferLoad, int bufferCapacity) {
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
				stmt.execute(sb.toString());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void incrementHopCount(int increment) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET hopCount=hopCount+" + increment + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void incrementMessageCountBy(int increment) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET messageCount=messageCount+" + increment + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void incrementRoleChangeAction(int count) {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET roleChangeCount=roleChangeCount+" + count + " WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setErrorRun() {
		if (isLoggingEnabled()) {
			String sql = "UPDATE RunMetadata SET runError=1 WHERE runid=" + getRunId();

			try {
				Statement stmt = getConnection().createStatement();
				stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void setIntervalTime(int time) {
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

				st.execute(query);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void storeRoleChangeDistance(HelpRequest request) {
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
				stmt.execute(sql);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public IFuture<Void> agentKilled() {
		if (loggingEnabled) {
			try {
				getConnection().close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		return IFuture.DONE;
	}

	public Integer getCurrentTime() {
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
}