package haw.mmlab.production_line.logging.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wertet die Daten von Simulationslaeufen bezueglich der Rekonfiguration aus.
 * Da die Rekonfiguration u.U. nicht erfolgreich war (error-flag in DB ist 1)
 * muessen diese Laufe dennoch mit in die Auswertung einfliessen. Da die Daten
 * in der DB aber nicht immer aussagekraeftig sind, werden fuer die benoetigten
 * Daten Ersatzwerte vergeben. Diese werden dann in die gemittelten Daten
 * entsprechend eingeflochten.
 * 
 * @author peter
 */
public class ReconfQualityLogConverter {

	public static void main(String[] args) {
		try {
			ReconfQualityLogConverter converter = new ReconfQualityLogConverter();
			converter.generateDynamicData();
			converter.convert(20, 200, 10);
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

	private void convert(double avgMessageCountVal, double avgRequestHopsVal, double avgRequestCountVal) {
		String sql1 = "SELECT * FROM ReconfQualityView2";
		String sql2 = "SELECT count(*) AS count FROM ReconfQualityView1 WHERE error = 1 AND redundancyRate2 = ? AND workload = ?";
		try {
			Statement statement = getConnection().createStatement();
			PreparedStatement ps = getConnection().prepareStatement(sql2);
			ResultSet rs1 = statement.executeQuery(sql1);

			System.out.println("redundancyRate2\tworkload\trecords\tavgMessageCount\tavgRequestHops\tavgRequestCount");

			while (rs1.next()) {
				double rr = rs1.getDouble("redundancyRate2");
				double wl = rs1.getDouble("workload");
				int records = rs1.getInt("records");
				double avgMessageCount = rs1.getDouble("avgMessageCount");
				double avgRequestHops = rs1.getDouble("avgRequestHops");
				double avgRequestCount = rs1.getDouble("avgRequestCount");

				ps.setDouble(1, rr);
				ps.setDouble(2, wl);

				ResultSet rs2 = ps.executeQuery();
				if (rs2.next() && rs2.getInt("count") > 0) {
					avgMessageCount *= records;
					avgRequestHops *= records;
					avgRequestCount *= records;

					int count = rs2.getInt("count");
					records += count;
					avgMessageCount += (avgMessageCountVal * count);
					avgRequestHops += (avgRequestHopsVal * count);
					avgRequestCount += (avgRequestCountVal * count);

					avgMessageCount /= records;
					avgRequestHops /= records;
					avgRequestCount /= records;

					avgMessageCount = Math.round(avgMessageCount * 10) / 10;
					avgRequestHops = Math.round(avgRequestHops * 10) / 10;
					avgRequestCount = Math.round(avgRequestCount * 10) / 10;
				}

				StringBuilder sb = new StringBuilder();
				sb.append(rr);
				sb.append("\t");
				sb.append(wl);
				sb.append("\t");
				sb.append(records);
				sb.append("\t");
				sb.append(avgMessageCount);
				sb.append("\t");
				sb.append(avgRequestHops);
				sb.append("\t");
				sb.append(avgRequestCount);
				System.out.println(sb);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void generateDynamicData() {
		String sql = "SELECT run, MIN(intervalTime) as min, MAX(intervalTime) as max FROM Log WHERE mainState=0 GROUP BY runid";
		String sql2 = "SELECT bufferLoad/bufferCapacity as dwl FROM Log WHERE runid=? AND agentType='robot' AND intervalTime BETWEEN ? AND ? GROUP BY agentId";
		String update = "UPDATE RunMetadata SET dynamicWorkload=?, reconfTimesteps=? WHERE runid=?";
		try {
			Statement statement = getConnection().createStatement();
			ResultSet result = statement.executeQuery(sql);

			PreparedStatement preparedStatement = getConnection().prepareStatement(sql2);
			PreparedStatement preparedStatement2 = getConnection().prepareStatement(update);

			while (result.next()) {
				int run = result.getInt("run");
				int min = result.getInt("min");
				int max = result.getInt("max");

				preparedStatement.setInt(1, run);
				preparedStatement.setInt(2, min);
				preparedStatement.setInt(3, max);

				ResultSet result2 = preparedStatement.executeQuery();
				double dwl = 0;
				int i = 0;
				while (result2.next()) {
					dwl += result2.getDouble("dwl");
					i++;
				}
				dwl /= i;

				preparedStatement2.setDouble(1, dwl);
				preparedStatement2.setInt(2, max - min);
				preparedStatement2.setInt(3, run);

				preparedStatement2.execute();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}