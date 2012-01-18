/**
 * 
 */
package haw.mmlab.production_line.math;

import haw.mmlab.production_line.logging.database.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
			List<ValueSet> valueSets = new ArrayList<ValueSet>();

			PreparedStatement stmt = connection.prepareStatement("SELECT redRate, workload, robotCount, hopCount FROM reconfqualityview ORDER BY redRate, workload");
			ResultSet result = stmt.executeQuery();

			double oldRedRate = 0.1;
			double oldWorkload = 0.1;

			double sumHopCount = 0.0;
			double sum2HopCount = 0.0;

			int robotCount = 0;

			int n = 0;

			List<Integer> hops = new ArrayList<Integer>();

			while (result.next()) {

				double redRate = result.getDouble("redRate");
				double workload = result.getDouble("workload");
				int hopCount = result.getInt("hopCount");
				robotCount = result.getInt("robotCount");

				hops.add(hopCount);

				if (oldRedRate != redRate || oldWorkload != workload) {
					double avgHopCount = sumHopCount / n;
					double avgHopCount2 = sum2HopCount / n;

					double quotient = new Integer(n).doubleValue() / (n - 1);
					double sigma = quotient * (avgHopCount2 - Math.pow(avgHopCount, 2));

					ValueSet vs = new Sigma2DB.ValueSet();
					vs.redRate = oldRedRate;
					vs.workload = oldWorkload;
					vs.avgHopCount = avgHopCount;
					vs.avgHopCount2 = avgHopCount2;
					vs.sigma = sigma;
					vs.hops.addAll(hops);

					valueSets.add(vs);

					oldRedRate = redRate;
					oldWorkload = workload;

					sumHopCount = 0.0;
					sum2HopCount = 0.0;

					n = 0;

					hops = new ArrayList<Integer>();
				}

				n++;

				sumHopCount += hopCount;
				sum2HopCount += Math.pow(hopCount, 2.0);
			}

			System.out.println("Finished first processing step.");
			System.out.println("RedRate\tWorkload\tAvgHopCount\tAvgHopCount2\tSigma\tSigma2\tSigma2Total");

			for (ValueSet vs : valueSets) {
				double sum = 0.0;

				for (Integer hop : vs.hops) {
					sum += Math.pow(hop - vs.avgHopCount, 2);
				}

				double sigma2 = 1 / (new Integer(vs.hops.size()).doubleValue() - 1) * sum;

				int c = robotCount;
				double kappa = MathmaticModel.kappa(vs.workload);
				long k = Math.round((vs.redRate * c) - 1);
				double delta = MathmaticModel.delta(vs.redRate);
				long t = Math.round(delta * ((vs.redRate * c) - 1));

				double expValue = MathmaticModel.expectationValue(robotCount, vs.redRate, vs.workload, kappa, c, k, t);

				double sigma2Tot = sigma2 + ((new Integer(vs.hops.size()) / (vs.hops.size() - 1)) * Math.pow(vs.avgHopCount - expValue, 2));

				System.out.println(vs.redRate + "\t" + vs.workload + "\t" + vs.avgHopCount + "\t" + vs.avgHopCount2 + "\t" + vs.sigma + "\t" + sigma2 + "\t" + sigma2Tot);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static class ValueSet {

		private double redRate = 0, workload = 0, avgHopCount = 0, avgHopCount2, sigma = 0;

		private List<Integer> hops = new ArrayList<Integer>();
	}
}
