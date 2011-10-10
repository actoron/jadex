/**
 * 
 */
package sodekovs.applications.bikes.datafetcher.brisbane;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bikes.datafetcher.DownloadCityTask;
import sodekovs.applications.bikes.datafetcher.brisbane.xml.Carto;
import sodekovs.applications.bikes.datafetcher.brisbane.xml.Marker;
import sodekovs.applications.bikes.datafetcher.brisbane.xml.Station;
import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;

/**
 * Download Task fetches the XML data from the Brisbane REST API and inserts it into the database.
 * 
 * @author Thomas Preisler
 */
public class DownloadBrisbaneTask extends DownloadCityTask {

	private static final String STATIONS_URL = "https://abo-brisbane.cyclocity.fr/service/carto";

	private static final String DETAILS_URL = "https://abo-brisbane.cyclocity.fr/service/stationdetails/";

	public DownloadBrisbaneTask(Logger logger, String city) {
		super(logger, city);
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

			baos.close();
			input.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.getMessage());
		} catch (JAXBException e) {
			logger.log(Level.SEVERE, e.getMessage());
		}
	}

	/**
	 * Inserts the given {@link Marker} and {@link Station} to the database referencing the given stationsId.
	 * 
	 * @param marker
	 *            the given {@link Marker}
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
}