package sodekovs.bikesharing.data.clustering;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBException;

import sodekovs.bikesharing.data.DatabaseConnection;
import sodekovs.bikesharing.data.RealDataExtractor;
import sodekovs.bikesharing.data.washington.stations.XMLHandler;

/**
 * Clusters the stations into four quadrants (NW, NE, SW, SE) and allows a recursively sub-clustering of these clusters.
 * 
 * @author Thomas Preisler
 */
public class FourBaseClusterer {

	/**
	 * The level of recursive clustering, results in 4^LEVEL #quadrants
	 */
	private static final int LEVEL = 1;

	/**
	 * The database connection
	 */
	private Connection dbConn = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		System.out.println("FourBaseClusterer started");
		FourBaseClusterer clusterer = new FourBaseClusterer();

		System.out.println("Fetching stations from databse");
		List<Station> stations = clusterer.fetchStations();
		System.out.println("Fetched " + stations.size() + " stations");

		System.out.println("Building initial quadrant");
		Quadrant rootQuadrant = clusterer.createInitialQuadrant();
		for (Station station : stations) {
			if (rootQuadrant.isInQuadrant(station))
				rootQuadrant.addStation(station);
			else
				System.out.println("Station was out of quadrant" + station);
		}
		System.out.println("Quadrant size " + rootQuadrant.getSize());

		System.out.println("Recursive clustering with level " + LEVEL);
		List<Quadrant> recursiveQuadrants = rootQuadrant.createSubQuadrants(LEVEL);
		int noQuadrants = 0, noStations = 0;
		for (Quadrant quadrant : recursiveQuadrants) {
			noQuadrants++;
			noStations += quadrant.getStations().size();
		}
		System.out.println("Recursive #quadrants " + noQuadrants + " #stations " + noStations);

		System.out.println("Creating clusters from quadrants");
		List<Cluster> cluster = new ArrayList<Cluster>();
		for (Quadrant quadrant : recursiveQuadrants) {
			cluster.add(new Cluster(quadrant));
		}

		System.out.println("Saving cluster configuration file");
		SuperCluster superCluster = new SuperCluster(cluster);
		try {
			int noCluster = (int) Math.pow(4.0, LEVEL);
			String fileName = "stationCluster-" + noCluster + ".xml";
			XMLHandler.saveAsXML(SuperCluster.class, superCluster, fileName);
			System.out.println(fileName + " created");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}

		System.out.println("FourBaseClusterer finished");
	}

	/**
	 * Default constructor
	 */
	public FourBaseClusterer() {
		this.dbConn = DatabaseConnection.getConnection();
	}

	/**
	 * Creates the initial cluster with just one quadrant.
	 * 
	 * @return and returns it
	 */
	public Quadrant createInitialQuadrant() {
		Quadrant quadrant = new Quadrant(RealDataExtractor.NORTH, RealDataExtractor.SOUTH, RealDataExtractor.WEST, RealDataExtractor.EAST);
		return quadrant;
	}

	/**
	 * Fetches all the stations from the database.
	 * 
	 * @return the stations as a list
	 */
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
				List<String> excludedStations = Arrays.asList(RealDataExtractor.EXCLUDE_STATIONS);
				if (!excludedStations.contains(name)) {
					station.setName(station.getName().replace("&", "and"));
					data.add(station);
					System.out.println("fetchStations() - added " + station);
				} else {
					System.out.println("fetchStations() - ignored exluded station " + station);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return data;
	}
}