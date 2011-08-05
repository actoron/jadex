/**
 * 
 */
package sodekovs.applications.bikes.datafetcher;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import sodekovs.applications.bikes.datafetcher.xml.stations.Stations;

/**
 * Abstract super class for a cities with a specialized API.
 * 
 * @author Thomas Preisler
 */
public abstract class DownloadCityTask extends DownloadTask {

	protected Long fetchTime = System.currentTimeMillis();

	public DownloadCityTask(Logger logger, String city) {
		super(logger, city);

		logger.log(Level.INFO, "Started Download Task for " + city);
	}

	/**
	 * Inserts the given {@link Stations} to the database.
	 * 
	 * @param stations
	 *            the given {@link Stations}
	 * @return the auto generated primary key
	 */
	protected int insertStations() {
		int id = -1;

		try {
			insertStationsStmt.setString(1, city);
			insertStationsStmt.setLong(2, fetchTime);
			insertStationsStmt.setString(3, "none");

			int affectedRows = insertStationsStmt.executeUpdate();
			if (affectedRows != 0) {
				ResultSet key = insertStationsStmt.getGeneratedKeys();
				if (key != null && key.next()) {
					id = key.getInt(1);
				}
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return id;
	}
}