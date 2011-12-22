/**
 * 
 */
package haw.mmlab.production_line.math;

import haw.mmlab.production_line.logging.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author thomas
 * 
 */
public class Sigma2DB {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Connection connection = DatabaseConnection.getConnection();

		try {
			PreparedStatement stmt = connection.prepareStatement("SELECT redRate, workload, hopCount FROM reconfqualityview ORDER BY redRate, workload");
			ResultSet result = stmt.executeQuery();

			double oldRedRate = 0.1;
			double oldWorkload = 0.1;

			double sumHopCount = 0;
			double sum2HopCount = 0;

			int n = 0;

			System.out.println("redRate\tworkload\thopCount\thopCount2");

			while (result.next()) {

				double redRate = result.getDouble("redRate");
				double workload = result.getDouble("workload");
				int hopCount = result.getInt("hopCount");

				if (oldRedRate != redRate || oldWorkload != workload) {
					System.out.println(oldRedRate + "\t" + oldWorkload + "\t" + sumHopCount / n + "\t" + sum2HopCount / n);

					oldRedRate = redRate;
					oldWorkload = workload;

					sumHopCount = 0;
					sum2HopCount = 0;

					n = 0;
				}

				n++;

				sumHopCount += hopCount;
				sum2HopCount += Math.pow(hopCount, 2.0);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
