/**
 * 
 */
package sodekovs.bikesharing.data.clustering;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import sodekovs.bikesharing.data.DatabaseConnection;
import sodekovs.bikesharing.data.RealDataExtractor;
import sodekovs.bikesharing.data.washington.stations.XMLHandler;

/**
 * @author thomas
 * 
 */
public class FourBaseClusterer {

	private Connection dbConn = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		FourBaseClusterer clusterer = new FourBaseClusterer();
		List<Station> stations = clusterer.fetchStations();
		System.out.println("Fetchted " + stations.size() + " Stations");
		
		Quadrant rootQuadrant = clusterer.createInitialQuadrant();
		
		for (Station station : stations) {
			if (rootQuadrant.isInQuadrant(station))
				rootQuadrant.addStation(station);
			else
				System.out.println("Station was out of quadrant" + station);
		}
		
		System.out.println("Quadrant size " + rootQuadrant.getSize());
		
		Quadrant[] subQuadrants = rootQuadrant.createSubQuadrants();
		Quadrant nw = subQuadrants[0];
		Quadrant ne = subQuadrants[1];
		Quadrant sw = subQuadrants[2];
		Quadrant se = subQuadrants[3];
		
		System.out.println("Quadrant NW size " + nw.getSize());
		System.out.println("Quadrant NE size " + ne.getSize());
		System.out.println("Quadrant SW size " + sw.getSize());
		System.out.println("Quadrant SE size " + se.getSize());
		System.out.println("--------------------------------");
		
		List<Quadrant> subQuadrants2 = rootQuadrant.createSubQuadrants(1);
		Quadrant nw2 = subQuadrants2.get(0);
		Quadrant ne2 = subQuadrants2.get(1);
		Quadrant sw2 = subQuadrants2.get(2);
		Quadrant se2 = subQuadrants2.get(3);
		
		System.out.println("Quadrant NW size " + nw2.getSize());
		System.out.println("Quadrant NE size " + ne2.getSize());
		System.out.println("Quadrant SW size " + sw2.getSize());
		System.out.println("Quadrant SE size " + se2.getSize());
		System.out.println("--------------------------------");
		
		List<Quadrant> recursiveQuadrants = rootQuadrant.createSubQuadrants(5);
		int noQuadrants = 0, noStations = 0;
		for (Quadrant quadrant : recursiveQuadrants) {
			noQuadrants++;
			noStations += quadrant.getStations().size();
		}
		
		System.out.println("Recursive #quadrants " + noQuadrants + " #stations " + noStations);
		
		List<Cluster> cluster = new ArrayList<Cluster>();
		for (Quadrant quadrant : recursiveQuadrants) {
			cluster.add(new Cluster(quadrant));
		}
		
		SuperCluster superCluster = new SuperCluster(cluster);
		try {
			XMLHandler.saveAsXML(SuperCluster.class, superCluster, "cluster.xml");

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	public FourBaseClusterer() {
		this.dbConn = DatabaseConnection.getConnection();
	}

	public Quadrant createInitialQuadrant() {
		Quadrant quadrant = new Quadrant(RealDataExtractor.NORTH, RealDataExtractor.SOUTH, RealDataExtractor.WEST, RealDataExtractor.EAST);
		return quadrant;
	}

	public List<Station> fetchStations() {
		List<Station> data = new ArrayList<Station>();

		String sql = "SELECT name, lat, lon FROM station_coordinates";
		try {
			PreparedStatement statement = dbConn.prepareStatement(sql);
			ResultSet rs = statement.executeQuery();

			while (rs.next()) {
				String name = rs.getString("name");
				Double lat = rs.getDouble("lat");
				Double lon = rs.getDouble("lon");

				Station station = new Station(lat, lon, name);
				data.add(station);

				System.out.println("fetchStations() - added " + station);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}
}
