/**
 * 
 */
package sodekovs.applications.bikes.datafetcher.brisbane;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bikes.datafetcher.brisbane.xml.Carto;
import sodekovs.applications.bikes.datafetcher.brisbane.xml.Marker;
import sodekovs.applications.bikes.datafetcher.brisbane.xml.Station;
import sodekovs.applications.bikes.datafetcher.database.DatabaseConnection;
import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;
import sodekovs.applications.bikes.datafetcher.xml.stations.Stations;

/**
 * Download Task fetches the XML data from the Brisbane REST API and inserts it into the database.
 * 
 * @author Thomas Preisler
 */
public class DownloadBrisbaneTask extends TimerTask {

	private static final String STATIONS_URL = "https://abo-brisbane.cyclocity.fr/service/carto";

	private static final String DETAILS_URL = "https://abo-brisbane.cyclocity.fr/service/stationdetails/";

	private Logger logger = null;
	
	private Long fetchTime = System.currentTimeMillis();

	/** The database connection */
	private Connection connection = null;

	/** Prepared SQL statement for the stations table */
	private PreparedStatement insertStationsStmt = null;

	/** Prepared SQL statement for the station table */
	private PreparedStatement insertStationStmt = null;

	public DownloadBrisbaneTask(Logger logger) {
		this.logger = logger;

		// get the database connection
		this.connection = DatabaseConnection.getConnection();
		try {
			// prepare the SQL statements
			this.insertStationsStmt = this.connection.prepareStatement("INSERT INTO STATIONS(city, lastUpdate, version) VALUES(?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
			this.insertStationStmt = this.connection
					.prepareStatement("INSERT INTO STATION(id, name, terminalName, lat, lon, installed, locked, installDate, removalDate, temp, nbBikes, nbEmptyDocks, nbDocks, stationsId) VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

			logger.log(Level.INFO, "Started Download Task for Brisbane");
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.TimerTask#run()
	 */
	@Override
	public void run() {
		try {
			URL url = new URL(STATIONS_URL);

			// open the connection
			URLConnection connection = url.openConnection();
			InputStream input = connection.getInputStream();

			// fetch the data
			ByteArrayOutputStream baos = getBytes(input);
			String xml = baos.toString();

			// transform the XML data from the url
			Carto carto = (Carto) XMLHandler.retrieveFromXML(Carto.class, xml.getBytes());

			fetchTime = System.currentTimeMillis();
			logger.log(Level.INFO, "Fetched data from Brisbane at " + fetchTime);

			int stationsId = insertStations();
			
			for (Marker marker : carto.getMarkers()) {
				URL stationURL = new URL(DETAILS_URL + marker.getNumber());

				// open the connection
				connection = stationURL.openConnection();
				input = connection.getInputStream();

				// fetch the data
				baos = getBytes(input);
				xml = baos.toString();

				// transform the XML data from the url
				Station station = (Station) XMLHandler.retrieveFromXML(Station.class, xml.getBytes());

				insertStation(marker, station, stationsId);
			}
			
			logger.log(Level.INFO, "Inserted data for Brisbane from " + fetchTime);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}
	
	/**
	 * Inserts the given {@link Station} to the database referencing the given stationsId.
	 * 
	 * @param station
	 *            the given {@link Station}
	 * @param stationsId
	 *            the given stationsId
	 * @return <code>true</code> if the insert was successful else <code>false</code>
	 */
	private boolean insertStation(Marker marker, Station station, int stationsId) {
		try {
			insertStationStmt.setInt(1, marker.getNumber());
			insertStationStmt.setString(2, marker.getAddress());
			insertStationStmt.setString(3, marker.getName());
			insertStationStmt.setDouble(4, marker.getLat());
			insertStationStmt.setDouble(5, marker.getLng());
			insertStationStmt.setBoolean(6, Boolean.FALSE);
			insertStationStmt.setBoolean(7, Boolean.FALSE);
			insertStationStmt.setLong(8, 0);
			insertStationStmt.setLong(9, 0);
			insertStationStmt.setBoolean(10, Boolean.FALSE);
			insertStationStmt.setInt(11, station.getAvailable());
			insertStationStmt.setInt(12, station.getFree());
			insertStationStmt.setInt(13, station.getTotal());
			insertStationStmt.setInt(14, stationsId);

			if (insertStationStmt.executeUpdate() != 0) {
				return true;
			}
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}

		return false;
	}
	
	/**
	 * Inserts the given {@link Stations} to the database.
	 * 
	 * @param stations
	 *            the given {@link Stations}
	 * @return the auto generated primary key
	 */
	private int insertStations() {
		int id = -1;

		try {
			insertStationsStmt.setString(1, "Brisbane");
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

	/**
	 * Fetches the XML data from the given {@link URLConnection} {@link InputStream} and stores it into a {@link ByteArrayOutputStream}.
	 * 
	 * @param input
	 *            the given {@link URLConnection} {@link InputStream}
	 * @return a {@link ByteArrayOutputStream} containing the XML data
	 */
	private ByteArrayOutputStream getBytes(InputStream input) {
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