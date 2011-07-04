/**
 * 
 */
package sodekovs.applications.bike2;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TimerTask;

import javax.xml.bind.JAXBException;

import sodekovs.applications.bike2.database.DatabaseConnection;
import sodekovs.applications.bike2.xml.Station;
import sodekovs.applications.bike2.xml.Stations;
import sodekovs.applications.bike2.xml.StationsXMLHandler;

/**
 * @author thomas
 * 
 */
public class DownloadFileTask extends TimerTask {

	private String city = null;

	private URL url = null;

	private Connection connection = null;

	private PreparedStatement insertStationsStmt = null;

	private PreparedStatement insertStationStmt = null;

	public DownloadFileTask(String city, URL url) {
		this.city = city;
		this.url = url;

		this.connection = DatabaseConnection.getConnection();
		try {
			this.insertStationsStmt = this.connection.prepareStatement("INSERT INTO STATIONS(city, lastUpdate, version) VALUES(?, ?, ?)");
			this.insertStationStmt = this.connection
					.prepareStatement("INSERT INTO STATION(id, name, terminalName, lat, long, installed, locked, installDate, removalDate, temporary, nbBikes, nbEmptyBikes, nbDocks, stationsId) VALUES()");
		} catch (SQLException e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	@Override
	public void run() {
		try {
			// open the connection
			URLConnection connection = url.openConnection();
			// transform the XML data from the url
			Stations stations = StationsXMLHandler.retrieveFromXML(connection.getInputStream());

			System.out.println("Fetched data from " + city + " at " + stations.getLastUpate());

			writeToDatabase(stations);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	private void writeToDatabase(Stations stations) {
		boolean result = false;
		int stationsId = insertStations(stations);

		if (stationsId >= 0) {
			for (Station station : stations.getStations()) {
				result = insertStation(station, stationsId);
			}
		}

		if (result) {
			System.out.println("Inserted data for " + city + " from " + stations.getLastUpate());
		} else {
			System.err.println("Error could not insert data for " + city + " from " + stations.getLastUpate());
		}
	}

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
			insertStationsStmt.setInt(13, station.getNbDocks());
			insertStationStmt.setInt(14, stationsId);

			if (insertStationStmt.executeUpdate() != 0) {
				return true;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}

	private int insertStations(Stations stations) {
		int id = -1;

		try {
			insertStationsStmt.setString(1, city);
			insertStationsStmt.setLong(2, stations.getLastUpate());
			insertStationsStmt.setString(3, stations.getVersion());

			int rs = insertStationsStmt.executeUpdate("INSERT", Statement.RETURN_GENERATED_KEYS);
			if (rs != 0) {
				ResultSet key = insertStationsStmt.getGeneratedKeys();
				if (key != null && key.next()) {
					id = key.getInt(1);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return id;
	}
}
