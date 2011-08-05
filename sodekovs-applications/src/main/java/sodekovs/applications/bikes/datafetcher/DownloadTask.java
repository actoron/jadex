/**
 * 
 */
package sodekovs.applications.bikes.datafetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import sodekovs.applications.bikes.datafetcher.database.DatabaseConnection;

/**
 * Abstract super class for all download tasks.
 * 
 * @author Thomas Preisler
 */
public abstract class DownloadTask extends TimerTask {

	/** The city */
	protected String city = null;

	protected Logger logger = null;

	/** The database connection */
	protected Connection connection = null;

	/** Prepared SQL statement for the stations table */
	protected PreparedStatement insertStationsStmt = null;

	/** Prepared SQL statement for the station table */
	protected PreparedStatement insertStationStmt = null;

	public DownloadTask(Logger logger, String city) {
		this.city = city;
		this.logger = logger;

		// get the database connection
		this.connection = DatabaseConnection.getConnection();
		try {
			// prepare the SQL statements
			this.insertStationsStmt = this.connection.prepareStatement("INSERT INTO STATIONS(city, lastUpdate, version) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			this.insertStationStmt = this.connection
					.prepareStatement("INSERT INTO STATION(id, name, terminalName, lat, lon, installed, locked, installDate, removalDate, temp, nbBikes, nbEmptyDocks, nbDocks, stationsId) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Fetches the XML data from the given {@link URLConnection} {@link InputStream} and stores it into a {@link ByteArrayOutputStream}.
	 * 
	 * @param input
	 *            the given {@link URLConnection} {@link InputStream}
	 * @return a {@link ByteArrayOutputStream} containing the XML data
	 */
	protected ByteArrayOutputStream getBytes(InputStream input) {
		ByteArrayOutputStream result = new ByteArrayOutputStream();

		try {
			// XML Daten einlesen
			result = new ByteArrayOutputStream();
			byte[] buffer = new byte[1000];
			int amount = 0;

			// Inhalt lesen
			while (amount != -1) {
				result.write(buffer, 0, amount);
				amount = input.read(buffer);
			}
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return result;
	}
}
