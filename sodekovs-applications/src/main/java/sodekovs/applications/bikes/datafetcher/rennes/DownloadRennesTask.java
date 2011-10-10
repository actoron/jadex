/**
 * 
 */
package sodekovs.applications.bikes.datafetcher.rennes;

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
import sodekovs.applications.bikes.datafetcher.rennes.xml.OpenData;
import sodekovs.applications.bikes.datafetcher.rennes.xml.Station;
import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;

/**
 * Download Task fetches the XML data from the Rennes API and inserts it into the database.
 * 
 * @author Thomas Preisler
 */
public class DownloadRennesTask extends DownloadCityTask {

	private static final String URL = "http://data.keolis-rennes.com/xml/?version=2.0&key=FEW4AAJH6OG2O3C&cmd=getbikestations";

	public DownloadRennesTask(Logger logger, String city) {
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
			URL url = new URL(URL);

			// open the connection
			URLConnection connection = url.openConnection();
			InputStream input = connection.getInputStream();

			// fetch the data
			ByteArrayOutputStream baos = getBytes(input);
			String xml = baos.toString();

			// transform the XML data from the url
			OpenData openData = (OpenData) XMLHandler.retrieveFromXML(OpenData.class, xml.getBytes());

			fetchTime = System.currentTimeMillis();
			logger.log(Level.INFO, "Fetched data from Rennes at " + fetchTime);

			int stationsId = insertStations();

			for (Station station : openData.getAnswer().getData()) {
				insertStation(station, stationsId);
			}

			logger.log(Level.INFO, "Inserted data for Rennes from " + fetchTime);

			baos.close();
			input.close();
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
	private boolean insertStation(Station station, int stationsId) {
		try {
			insertStationStmt.setInt(1, station.getNumber());
			insertStationStmt.setString(2, station.getName());
			insertStationStmt.setString(3, station.getAddress());
			insertStationStmt.setDouble(4, station.getLatitude());
			insertStationStmt.setDouble(5, station.getLongitude());
			insertStationStmt.setBoolean(6, Boolean.FALSE);
			insertStationStmt.setBoolean(7, Boolean.FALSE);
			insertStationStmt.setLong(8, 0);
			insertStationStmt.setLong(9, 0);
			insertStationStmt.setBoolean(10, Boolean.FALSE);
			insertStationStmt.setInt(11, station.getBikesAvailable());
			insertStationStmt.setInt(12, station.getSlotsAvailable());
			insertStationStmt.setInt(13, station.getBikesAvailable() + station.getSlotsAvailable());
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