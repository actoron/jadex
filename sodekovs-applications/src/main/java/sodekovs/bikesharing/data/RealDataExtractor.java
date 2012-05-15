package sodekovs.bikesharing.data;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;

import sodekovs.bikesharing.model.DestinationProbability;
import sodekovs.bikesharing.model.ObjectFactory;
import sodekovs.bikesharing.model.ProbabilitiesForStation;
import sodekovs.bikesharing.model.ProbabilitiesForStation.DestinationProbabilities;
import sodekovs.bikesharing.model.SimulationDescription;
import sodekovs.bikesharing.model.SimulationDescription.Stations;
import sodekovs.bikesharing.model.SimulationDescription.TimeSlices;
import sodekovs.bikesharing.model.Station;
import sodekovs.bikesharing.model.TimeSlice;
import sodekovs.bikesharing.model.TimeSlice.ProbabilitiesForStations;
import deco4mas.distributed.util.xml.XmlUtil;

/**
 * @author Thomas Preisler
 */
public class RealDataExtractor {

	/*
	 * The weekdays
	 */
	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	public static final int SUNDAY = 7;

	/*
	 * The cities
	 */
	public static final String LONDON = "london";
	public static final String WASHINGTON = "washington";

	/*
	 * The links
	 */
	public static final String BY_BIKE = "BY_BIKE";
	public static final String BY_TRUCK = "BY_TRUCK";

	/**
	 * Database Connection
	 */
	private Connection connection = null;

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RealDataExtractor rde = new RealDataExtractor();
		System.out.println("RealDataExtractor started.");
		Connection conn = DatabaseConnection.getConnection();
		rde.setConnection(conn);

		System.out.println("Fetching rentals.");
		List<Rental> rentals = rde.getRentals(new int[] { MONDAY }, WASHINGTON, BY_BIKE, null, null);
		if (!rentals.isEmpty()) {
			System.out.println("Fetched rentals.");

			System.out.println("Partioning rentals");
			TreeMap<Integer, List<Rental>> rentalsByHour = rde.partitionRentals(rentals);
			if (!rentalsByHour.isEmpty()) {
				System.out.println("Partioned rentals");

				System.out.println("Building SimulationDescription.");
				SimulationDescription sd = rde.buildSimulationDescription(rentalsByHour);
				System.out.println("Builded SimulationDescription.");

				Set<String> stations = rde.getStations(rentals);

				System.out.println("Adding GPS Coordinates.");
				sd = rde.addGPSCoordinates(sd, stations);
				System.out.println("Added GPS Coordinates.");

				try {
					System.out.println("Writing XML File.");
					String xmlFile = "monday_test2.xml";
					XmlUtil.saveAsXML(sd, xmlFile);
					System.out.println(xmlFile + " written.");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * @param connection
	 *            the connection to set
	 */
	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	/**
	 * Transforms a {@link List} of {@link Rental}s into a {@link Set} of {@link Rental}.
	 * 
	 * @param rentals
	 *            the given {@link List} of {@link Rental}s
	 * @return {@link Set} of {@link Rental}
	 */
	private Set<String> getStations(List<Rental> rentals) {
		Set<String> stations = new HashSet<String>();

		for (Rental rental : rentals) {
			stations.add(rental.getStartStation());
			stations.add(rental.getEndStation());
		}

		return stations;
	}

	/**
	 * Returns a {@link List} of {@link Rental}s for the given input parameters. Parameters that should not be included in the querying process have to be <code>null</code>.
	 * 
	 * @param weekdays
	 *            the weekdays given as an int array
	 * @param city
	 *            the name of the city
	 * @param link
	 *            the link {@link RealDataExtractor#BY_BIKE} or {@link RealDataExtractor#BY_TRUCK}
	 * @param from
	 *            {@link Timestamp} of the oldest data that should be fetched
	 * @param to
	 *            {@link Timestamp} of the youngest data that should be fetched
	 * @return
	 */
	public List<Rental> getRentals(int[] weekdays, String city, String link, Timestamp from, Timestamp to) {
		List<Rental> rentals = new ArrayList<Rental>();

		try {
			String sql = "SELECT bikeId, start, end, startStation, endStation, weekday FROM rental WHERE";
			if (city != null) {
				sql += " city LIKE ?";
			}
			if (link != null) {
				sql += " AND link LIKE ?";
			}
			if (weekdays != null && weekdays.length > 0) {
				sql += " AND (weekday = ?";
				for (int i = 1; i < weekdays.length; i++) {
					sql += " OR weekday = ?";
				}
				sql += ")";
			}
			if (from != null) {
				sql += " AND start >= ?";
			}
			if (to != null) {
				sql += " AND to <= ?";
			}

			PreparedStatement stmt = getConnection().prepareStatement(sql);
			int index = 1;
			if (city != null) {
				stmt.setString(index, city);
				index++;
			}
			if (link != null) {
				stmt.setString(index, link);
				index++;
			}
			if (weekdays != null && weekdays.length > 0) {
				for (int i = 0; i < weekdays.length; i++) {
					stmt.setInt(index, weekdays[i]);
					index++;
				}
			}
			if (from != null) {
				stmt.setTimestamp(index, from);
				index++;
			}
			if (to != null) {
				stmt.setTimestamp(index, to);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String bikeId = rs.getString("bikeId");
				Timestamp start = rs.getTimestamp("start");
				Timestamp end = rs.getTimestamp("end");
				String startStation = rs.getString("startStation");
				String endStation = rs.getString("endStation");
				int weekday = rs.getInt("weekday");

				Rental rental = new Rental(bikeId, start, end, startStation, endStation, weekday, link, city);
				rentals.add(rental);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rentals;
	}

	/**
	 * Partitions the given {@link List} of {@link Rental}s by the hour of their start.
	 * 
	 * @param rentals
	 *            given {@link List} of {@link Rental}s
	 * @return a {@link TreeMap} containing all the {@link Rental}s as values and the hour of their start as keys.
	 */
	public TreeMap<Integer, List<Rental>> partitionRentals(List<Rental> rentals) {
		TreeMap<Integer, List<Rental>> rentalsByHour = new TreeMap<Integer, List<Rental>>();

		for (Rental rental : rentals) {
			Calendar start = Calendar.getInstance();
			start.setTime(rental.getStart());
			int hour = start.get(Calendar.HOUR_OF_DAY);

			if (rentalsByHour.containsKey(hour)) {
				rentalsByHour.get(hour).add(rental);
			} else {
				List<Rental> tmp = new ArrayList<Rental>();
				tmp.add(rental);
				rentalsByHour.put(hour, tmp);
			}
		}

		return rentalsByHour;
	}

	/**
	 * Builds a {@link SimulationDescription} based on a given {@link TreeMap} containing all the {@link Rental}s as values and the hour of their start as keys.
	 * 
	 * @param rentalsByHour
	 *            given {@link TreeMap} containing all the {@link Rental}s as values and the hour of their start as keys.
	 * @return {@link SimulationDescription}
	 */
	public SimulationDescription buildSimulationDescription(TreeMap<Integer, List<Rental>> rentalsByHour) {
		ObjectFactory of = new ObjectFactory();
		SimulationDescription sd = of.createSimulationDescription();
		TimeSlices timeSlices = of.createSimulationDescriptionTimeSlices();

		for (Integer hour : rentalsByHour.keySet()) {
			List<Rental> rentals = rentalsByHour.get(hour);
			Map<String, Double> depatureFrequencies = new HashMap<String, Double>();
			Map<String, Map<String, Double>> destinationFrequencies = new HashMap<String, Map<String, Double>>();

			for (Rental rental : rentals) {
				String startStation = rental.getStartStation();
				String endStation = rental.getEndStation();

				if (depatureFrequencies.containsKey(startStation)) {
					depatureFrequencies.put(startStation, depatureFrequencies.get(startStation) + 1);
				} else {
					depatureFrequencies.put(startStation, 1.0);
				}

				if (destinationFrequencies.containsKey(startStation)) {
					Map<String, Double> destinations = destinationFrequencies.get(startStation);
					if (destinations.containsKey(endStation)) {
						destinations.put(endStation, destinations.get(endStation) + 1);
					} else {
						destinations.put(endStation, 1.0);
					}
				} else {
					Map<String, Double> destinations = new HashMap<String, Double>();
					destinations.put(endStation, 1.0);
					destinationFrequencies.put(startStation, destinations);
				}
			}

			Map<String, Double> departurePropabilities = new HashMap<String, Double>();
			for (String station : depatureFrequencies.keySet()) {
				departurePropabilities.put(station, depatureFrequencies.get(station) / new Double(rentals.size()));
			}

			Map<String, Map<String, Double>> destinationProbabilities = new HashMap<String, Map<String, Double>>();
			for (String station : destinationFrequencies.keySet()) {
				Map<String, Double> frequencies = destinationFrequencies.get(station);

				Double overall = 0.0;
				for (Double value : frequencies.values()) {
					overall += value;
				}

				Map<String, Double> destinations = new HashMap<String, Double>();
				for (String endStation : frequencies.keySet()) {
					destinations.put(endStation, frequencies.get(endStation) / overall);
				}

				destinationProbabilities.put(station, destinations);
			}

			TimeSlice timeSlice = of.createTimeSlice();
			timeSlice.setStartTime(hour * 60);
			timeSlice.setOffset(60);

			ProbabilitiesForStations probabilitiesForStations = of.createTimeSliceProbabilitiesForStations();
			timeSlice.setProbabilitiesForStations(probabilitiesForStations);

			for (String startStation : departurePropabilities.keySet()) {
				Double departurePropability = departurePropabilities.get(startStation);

				ProbabilitiesForStation probabilitiesForStation = of.createProbabilitiesForStation();
				probabilitiesForStation.setStationID(startStation);
				probabilitiesForStation.setDepartureProbability(departurePropability);

				DestinationProbabilities dp = of.createProbabilitiesForStationDestinationProbabilities();
				for (String endStation : destinationProbabilities.get(startStation).keySet()) {
					Double destinationPropabilityValue = destinationProbabilities.get(startStation).get(endStation);

					DestinationProbability destinationProbability = of.createDestinationProbability();
					destinationProbability.setDestination(endStation);
					destinationProbability.setProbability(destinationPropabilityValue);

					dp.getDestinationProbability().add(destinationProbability);
				}

				probabilitiesForStation.setDestinationProbabilities(dp);
				probabilitiesForStations.getProbabilitiesForStation().add(probabilitiesForStation);
			}

			timeSlices.getTimeSlice().add(timeSlice);
		}

		sd.setTimeSlices(timeSlices);

		return sd;
	}

	/**
	 * Adds the GPS coordinates of the stations given by the {@link Set} to the given {@link SimulationDescription}
	 * 
	 * @param sd
	 *            given {@link SimulationDescription}
	 * @param stationSet
	 * @return {@link SimulationDescription} enriched which the GPS coordinates of the stations
	 */
	private SimulationDescription addGPSCoordinates(SimulationDescription sd, Set<String> stationSet) {
		ObjectFactory of = new ObjectFactory();
		Stations stations = of.createSimulationDescriptionStations();

		for (String stationId : stationSet) {
			PreparedStatement stmt;
			try {
				stmt = getConnection().prepareStatement("SELECT lat, lon FROM stations WHERE name LIKE ?");
				stmt.setString(1, stationId);
				ResultSet rs = stmt.executeQuery();
				Double lat = 0.0;
				Double lon = 0.0;
				while (rs.next()) {
					lat = rs.getDouble("lat");
					lon = rs.getDouble("lon");
				}

				Station station = of.createStation();
				station.setStationID(stationId);
				station.setLatitude(lat);
				station.setLongitude(lon);

				// TODO Vernünftige Werte überlegen oder aus den Realdaten auswerten
				station.setNumberOfBikes(10);
				station.setNumberOfDocks(20);

				stations.getStation().add(station);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		sd.setStations(stations);
		return sd;
	}
}
