/**
 * 
 */
package sodekovs.bikesharing.data.washington.stations;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.xml.bind.JAXBException;

import sodekovs.bikesharing.data.DatabaseConnection;

/**
 * @author thomas
 * 
 */
public class StationCoordinates {

	private static final String FILE_PATH = "/Users/thomas/Documents/SodekoVS/SodekoVS-SVN/Bikesharing/bikeStations.xml";
	private static final String INSERT = "INSERT INTO station_coordinates(name, lat, lon, city) VALUES(?,?,?,?)";
	private static final String SELECT = "SELECT name FROM station_coordinates WHERE name LIKE ?";
	private static final String CITY = "Washington";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("StationCoordinates started");
			Connection conn = DatabaseConnection.getConnection();
			System.out.println("Database Connection opened");
			Stations stations = (Stations) XMLHandler.retrieveFromXML(Stations.class, FILE_PATH);
			System.out.println(FILE_PATH + " parsed");
			for (Station station : stations.getStations()) {
				String name = station.getName().trim();
				
				PreparedStatement selectStmt = conn.prepareStatement(SELECT);
				selectStmt.setString(1, name);
				ResultSet rs = selectStmt.executeQuery();
				if (!rs.next()) {
					PreparedStatement stmt = conn.prepareStatement(INSERT);
					stmt.setString(1, name);
					stmt.setDouble(2, station.getLat());
					stmt.setDouble(3, station.getLon());
					stmt.setString(4, CITY);

					if (stmt.executeUpdate() > 0) {
						System.out.println("Inserted " + name + " " + station.getLat() + "," + station.getLon());
					}
				} else {
					System.out.println(name + " was already present");
				}
			}

			System.out.println("StationCoordinates finished");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}