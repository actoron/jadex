package sodekovs.applications.bikes.datafetcher;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;
import sodekovs.applications.bikes.datafetcher.xml.stations.Station;
import sodekovs.applications.bikes.datafetcher.xml.stations.Stations;

/**
 * Download Task fetches the XML data from the given URL for the given city and inserts it into the database.
 * 
 * @author Thomas Preisler
 */
public class DownloadURLTask extends DownloadTask {

	/** The given URL */
	private URL url = null;

	/** Prepared SQL statement for the stationsxml table */
	private PreparedStatement insertStationsXMLStmt = null;

	/**
	 * Constructor.
	 * 
	 * @param city
	 *            the given city
	 * @param url
	 *            the given {@link URL}
	 */
	public DownloadURLTask(String city, URL url, Logger logger) {
		super(logger, city);
		this.url = url;

		try {
			this.insertStationsXMLStmt = this.connection.prepareStatement("INSERT INTO STATIONSXML(xml, stationsId) VALUES(?, ?)");

			logger.log(Level.INFO, "Started Download Task for " + city);
		} catch (SQLException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	@Override
	public void run() {
		try {
			// open the connection
			URLConnection connection = url.openConnection();
			InputStream input = connection.getInputStream();

			// fetch the data
			ByteArrayOutputStream baos = getBytes(input);
			String xml = baos.toString();

			// transform the XML data from the url
			Stations stations = (Stations) XMLHandler.retrieveFromXML(Stations.class, xml.getBytes());

			logger.log(Level.INFO, "Fetched data from " + city + " at " + stations.getLastUpate());

			// write all the data to the database
			writeToDatabase(stations, xml);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Writes the given {@link Stations} and the XML String to the database.
	 * 
	 * @param stations
	 *            the given {@link Stations}
	 * @param xml
	 *            the given XML String
	 */
	private void writeToDatabase(Stations stations, String xml) {
		boolean result = false;
		int stationsId = insertStations(stations);

		if (stationsId >= 0) {
			result = insertStationsXML(xml, stationsId);

			for (Station station : stations.getStations()) {
				result = insertStation(station, stationsId);
			}
		}

		if (result) {
			logger.log(Level.INFO, "Inserted data for " + city + " from " + stations.getLastUpate());
		} else {
			logger.log(Level.WARNING, "Error could not insert data for " + city + " from " + stations.getLastUpate());
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
	private boolean insertStation(Station station, int stationsId) {
		try {
			insertStationStmt.setInt(1, station.getId());
			insertStationStmt.setString(2, station.getName());
			insertStationStmt.setString(3, station.getTerminalName());
			insertStationStmt.setDouble(4, station.getLat());
			insertStationStmt.setDouble(5, station.getLon());
			insertStationStmt.setBoolean(6, station.getInstalled());
			insertStationStmt.setBoolean(7, station.getLocked());
			insertStationStmt.setLong(8, station.getInstallDate());
			insertStationStmt.setLong(9, station.getRemovalDate());
			insertStationStmt.setBoolean(10, station.getTemporary());
			insertStationStmt.setInt(11, station.getNbBikes());
			insertStationStmt.setInt(12, station.getNbEmptyDocks());
			insertStationStmt.setInt(13, station.getNbDocks());
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
	 * Insert the given XML String to the database referencing the given stationsId.
	 * 
	 * @param xml
	 *            the given XML String
	 * @param stationsId
	 *            the given stationsId
	 * @return <code>true</code> if the insert was successful else <code>false</code>
	 */
	private boolean insertStationsXML(String xml, int stationsId) {
		ByteArrayInputStream bais = new ByteArrayInputStream(xml.getBytes());

		try {
			insertStationsXMLStmt.setAsciiStream(1, bais, xml.length());
			insertStationsXMLStmt.setInt(2, stationsId);

			if (insertStationsXMLStmt.executeUpdate() != 0) {
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
	private int insertStations(Stations stations) {
		int id = -1;

		try {
			insertStationsStmt.setString(1, city);
			insertStationsStmt.setLong(2, stations.getLastUpate());
			insertStationsStmt.setString(3, stations.getVersion());

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