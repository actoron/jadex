/**
 * 
 */
package sodekovs.applications.bikes.datafetcher;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bikes.datafetcher.xml.XMLHandler;
import sodekovs.applications.bikes.datafetcher.xml.stations.Station;
import sodekovs.applications.bikes.datafetcher.xml.stations.Stations;
import sodekovs.graphanalysis.database.DatabaseConnection;

/**
 * @author thomas
 * 
 */
public class CoordinatesFetcher {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("Start");

		// String urlStr = "http://www.tfl.gov.uk/tfl/syndication/feeds/cycle-hire/livecyclehireupdates.xml";
		// String city = "London";

		String urlStr = "http://www.capitalbikeshare.com/stations/bikeStations.xml";
		String city = "Washington";

		try {
			// open the connection
			URL url = new URL(urlStr);
			URLConnection connection = url.openConnection();
			InputStream input = connection.getInputStream();

			// fetch the data
			ByteArrayOutputStream baos = getBytes(input);
			String xml = baos.toString();

			// transform the XML data from the url
			Stations stations = (Stations) XMLHandler.retrieveFromXML(Stations.class, xml.getBytes());

			for (Station station : stations.getStations()) {
				insertStation(station, city);
			}

			baos.close();
			input.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println("Finish");
	}

	private static boolean insertStation(Station station, String city) {
		try {
			PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement("INSERT INTO stations(name, lat, lon, city) VALUES(?,?,?,?)");
			stmt.setString(1, station.getName());
			stmt.setDouble(2, station.getLat());
			stmt.setDouble(3, station.getLon());
			stmt.setString(4, city);

			return stmt.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * Fetches the XML data from the given {@link URLConnection} {@link InputStream} and stores it into a {@link ByteArrayOutputStream}.
	 * 
	 * @param input
	 *            the given {@link URLConnection} {@link InputStream}
	 * @return a {@link ByteArrayOutputStream} containing the XML data
	 */
	private static ByteArrayOutputStream getBytes(InputStream input) {
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
			e.printStackTrace();
		}

		return result;
	}
}
