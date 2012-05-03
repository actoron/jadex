/**
 * 
 */
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

	public static final int MONDAY = 1;
	public static final int TUESDAY = 2;
	public static final int WEDNESDAY = 3;
	public static final int THURSDAY = 4;
	public static final int FRIDAY = 5;
	public static final int SATURDAY = 6;
	public static final int SUNDAY = 7;

	public static final String LONDON = "london";
	public static final String WASHINGTON = "washington";

	public static final String BY_BIKE = "BY_BIKE";
	public static final String BY_TRUCK = "BY_TRUCK";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		RealDataExtractor rde = new RealDataExtractor();
		System.out.println("RealDataExtractor started.");
		Connection conn = DatabaseConnection.getConnection();

		System.out.println("Fetching rentals.");
		List<Rental> rentals = rde.getRentals(MONDAY, WASHINGTON, BY_BIKE, conn);
		if (!rentals.isEmpty()) {
			System.out.println("Fetched rentals.");

			System.out.println("Partioning rentals");
			TreeMap<Integer, List<Rental>> rentalsByHour = rde.partitionateRentals(rentals);
			if (!rentalsByHour.isEmpty()) {
				System.out.println("Partioned rentals");

				System.out.println("Building SimulationDescription.");
				SimulationDescription sd = rde.buildSimulationDescription(rentalsByHour);
				System.out.println("Builded SimulationDescription.");

				Set<String> stations = rde.getStations(rentals);

				System.out.println("Adding GPS Coordinates.");
				sd = rde.addGPSCoordinates(sd, stations, conn);
				System.out.println("Added GPS Coordinates.");

				try {
					System.out.println("Writing XML File.");
					String xmlFile = "monday_test.xml";
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

	private Set<String> getStations(List<Rental> rentals) {
		Set<String> stations = new HashSet<String>();

		for (Rental rental : rentals) {
			stations.add(rental.getStartStation());
			stations.add(rental.getEndStation());
		}

		return stations;
	}

	public List<Rental> getRentals(int weekday, String city, String link, Connection conn) {
		List<Rental> rentals = new ArrayList<Rental>();

		try {
			PreparedStatement stmt = conn.prepareStatement("SELECT bikeId, start, end, startStation, endStation FROM rental WHERE city LIKE ? AND link LIKE ? and weekday = ?");
			stmt.setString(1, city);
			stmt.setString(2, link);
			stmt.setInt(3, weekday);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String bikeId = rs.getString("bikeId");
				Timestamp start = rs.getTimestamp("start");
				Timestamp end = rs.getTimestamp("end");
				String startStation = rs.getString("startStation");
				String endStation = rs.getString("endStation");

				Rental rental = new Rental(bikeId, start, end, startStation, endStation, weekday, link, city);
				rentals.add(rental);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return rentals;
	}

	public TreeMap<Integer, List<Rental>> partitionateRentals(List<Rental> rentals) {
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

	public SimulationDescription addGPSCoordinates(SimulationDescription sd, Set<String> stationSet, Connection conn) {
		ObjectFactory of = new ObjectFactory();
		Stations stations = of.createSimulationDescriptionStations();

		for (String stationId : stationSet) {
			PreparedStatement stmt;
			try {
				stmt = conn.prepareStatement("SELECT lat, lon FROM stations WHERE name LIKE ?");
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
