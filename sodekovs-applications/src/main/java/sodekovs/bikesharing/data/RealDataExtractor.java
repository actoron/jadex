package sodekovs.bikesharing.data;

import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationException;

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
import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import deco4mas.distributed.util.xml.XmlUtil;

/**
 * Utility class for the generation of {@link SimulationDescription}s based on database stored live and real data.
 * 
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

	/*
	 * Space height and width in pixel
	 */
	public static final double SPACE_HEIGHT = 100.0;
	public static final double SPACE_WIDTH = 100.0;

	/*
	 * Geo coordinates
	 */
	public static final double NORTH = 38.9713;
	public static final double SOUTH = 38.8113;
	public static final double WEST = -77.1191;
	public static final double EAST = -76.9135;

	/*
	 * The limit
	 */
	public static final Integer LIMIT = null;

	/**
	 * Database Connection
	 */
	private Connection connection = null;

	/**
	 * @param args
	 * @throws ValidationException
	 */
	public static void main(String[] args) throws ValidationException {
		RealDataExtractor rde = new RealDataExtractor();
		System.out.println("RealDataExtractor started.");
		Connection conn = DatabaseConnection.getConnection();
		rde.setConnection(conn);

		System.out.println("Fetching rentals.");
		List<Rental> rentals = rde.getRentals(new int[] { MONDAY }, WASHINGTON, BY_BIKE, null, null, LIMIT);
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

				System.out.println("Initializing Allocations.");
				rde.initializeAllocation(sd, MONDAY, WASHINGTON, LIMIT);
				System.out.println("Initialized Allocations.");

				System.out.println("Fetching Allocations for evalution.");
				SimulationDescription evalSd = rde.getAllocations(WASHINGTON, 0, 24, MONDAY, LIMIT);
				System.out.println("Fetched Allocations for evalution.");

				System.out.println("Postprocessing Station IDs.");
				rde.postProcessStatioNames(sd);
				rde.postProcessStatioNames(evalSd);
				System.out.println("Postprocessed Station IDs.");

				System.out.println("Validating SimulationDescription.");
				rde.validate(sd);
				System.out.println("Validated SimulationDescription");

				try {
					System.out.println("Writing XML File for Simulation.");
					String xmlFile = "WashingtonSimulation_Monday.xml";
					XmlUtil.saveAsXML(sd, xmlFile);
					System.out.println(xmlFile + " written.");

					System.out.println("Writing XML File for Evaluation");
					String xmlEvalFile = "WashingtonEvaluation_Monday.xml";
					XmlUtil.saveAsXML(evalSd, xmlEvalFile);
					System.out.println(xmlEvalFile + " written.");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (JAXBException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Validates the given {@link SimulationDescription} by checking if the departure and destination probabilities for all stations and {@link TimeSlice}s are 1.0. Also it is checked if the sum of
	 * all relative run values is 1.0.
	 * 
	 * @param sd
	 *            the given {@link SimulationDescription}
	 * @throws ValidationException
	 *             if one the checks is violated
	 */
	private void validate(SimulationDescription sd) throws ValidationException {
		List<TimeSlice> timeSlices = sd.getTimeSlices().getTimeSlice();

		// double run = 0.0;
		for (TimeSlice timeSlice : timeSlices) {
			// run += timeSlice.getRunRelative();

			List<ProbabilitiesForStation> probabilitiesForStations = timeSlice.getProbabilitiesForStations().getProbabilitiesForStation();
			double departureProbs = 0.0;

			for (ProbabilitiesForStation probabilitiesForStation : probabilitiesForStations) {
				List<DestinationProbability> destinationProbabilities = probabilitiesForStation.getDestinationProbabilities().getDestinationProbability();
				double destinationProbs = 0.0;

				for (DestinationProbability destinationProbability : destinationProbabilities) {
					destinationProbs += destinationProbability.getProbability();
				}
				if (destinationProbs < 0.9999)
					throw new ValidationException("Destination probabilities sum for station " + probabilitiesForStation.getStationID() + " is " + destinationProbs);

				departureProbs += probabilitiesForStation.getDepartureProbability();
			}
			if (departureProbs < 0.9999)
				throw new ValidationException("Departure probabilities sum for TimeSlice " + timeSlice.getStartTime() + " is " + departureProbs);
		}
		// if (run < 0.9999)
		// throw new ValidationException("Relative run sum for all TimeSlices is " + run);
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
	public Set<String> getStations(List<Rental> rentals) {
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
	 * @param limit
	 *            Limit the result data
	 * @return
	 */
	public List<Rental> getRentals(int[] weekdays, String city, String link, Timestamp from, Timestamp to, Integer limit) {
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
			if (limit != null) {
				sql += " LIMIT ?";
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
				index++;
			}
			if (limit != null) {
				stmt.setInt(index, limit);
			}

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String bikeId = rs.getString("bikeId");
				Timestamp start = rs.getTimestamp("start");
				Timestamp end = rs.getTimestamp("end");
				String startStation = rs.getString("startStation").trim();
				String endStation = rs.getString("endStation").trim();
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

		// int overallRun = 0;
		// for (List<Rental> rentals : rentalsByHour.values()) {
		// overallRun += rentals.size();
		// }

		int maxRun = 0;
		for (List<Rental> rentals : rentalsByHour.values()) {
			if (maxRun < rentals.size())
				maxRun = rentals.size();
		}

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
			timeSlice.setRunTotal(rentals.size());
			// double runRelative = (double) rentals.size() / (double) overallRun;
			double runRelative = (double) rentals.size() / (double) maxRun;
			timeSlice.setRunRelative(runRelative);

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
	public SimulationDescription addGPSCoordinates(SimulationDescription sd, Set<String> stationSet) {
		ObjectFactory of = new ObjectFactory();
		Stations stations = of.createSimulationDescriptionStations();

		for (String stationId : stationSet) {
			PreparedStatement stmt;
			try {
				stmt = getConnection().prepareStatement("SELECT lat, lon FROM station_coordinates WHERE name LIKE ?");
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

				transformGeoPosition(station);

				stations.getStation().add(station);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

		sd.setStations(stations);
		return sd;
	}

	/**
	 * Fetches the average initial allocation of number of bikes and number of docks from the live feed data in the database for the given weekday and city.
	 * 
	 * @param sd
	 *            the given {@link SimulationDescription}
	 * @param weekday
	 *            the given weekday
	 * @param city
	 *            the given city
	 */
	public void initializeAllocation(SimulationDescription sd, Integer weekday, String city, Integer limit) {
		HolidayManager hm = HolidayManager.getInstance(HolidayCalendar.UNITED_STATES);
		Set<Holiday> holidays = hm.getHolidays(2011);
		holidays.addAll(hm.getHolidays(2012));

		String sql = "SELECT s.name, s.nbBikes, s.nbEmptyDocks, FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)) as date, WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)))"
				+ " as weekday FROM station s JOIN stations ss ON s.stationsId = ss.id WHERE ss.city LIKE ? AND s.installed = 1 AND s.locked = 0 AND"
				+ " WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) = ?";
		if (limit != null) {
			sql += " LIMIT " + limit;
		}
		try {
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, city);
			stmt.setInt(2, weekday - 1);

			Map<String, List<Object[]>> stationData = new HashMap<String, List<Object[]>>();
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				String stationId = rs.getString("name");
				int nbBikes = rs.getInt("nbBikes");
				int nbEmptyDocks = rs.getInt("nbEmptyDocks");
				Date date = rs.getDate("date");

				Object[] data = new Object[3];
				data[0] = nbBikes;
				data[1] = nbEmptyDocks;
				data[2] = date;

				List<Object[]> dataList;
				if (!stationData.containsKey(stationId)) {
					dataList = new ArrayList<Object[]>();
					dataList.add(data);
					stationData.put(stationId, dataList);
				} else {
					dataList = stationData.get(stationId);
					dataList.add(data);
				}
			}

			for (Station station : sd.getStations().getStation()) {
				int bikes = 0;
				int docks = 0;

				List<Object[]> dataList = stationData.get(station.getStationID());
				if (dataList != null) {
					for (Object[] data : dataList) {
						int nbBikes = (Integer) data[0];
						int nbEmptyDocks = (Integer) data[1];
						Date date = (Date) data[2];

						if (!isHoliday(holidays, date)) {
							bikes += nbBikes;
							docks += nbBikes + nbEmptyDocks;
						}
					}

					bikes = (int) Math.round((double) bikes / (double) dataList.size());
					docks = (int) Math.round((double) docks / (double) dataList.size());
				}

				if (bikes == 0)
					bikes = 10;

				if (docks < bikes)
					docks = bikes;

				station.setNumberOfBikes(bikes);
				station.setNumberOfDocks(docks);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fetches the average initial allocation of number of bikes and number of docks from the live feed data in the database for the given weekday and city.
	 * 
	 * @param sd
	 *            the given {@link SimulationDescription}
	 * @param weekday
	 *            the given weekday
	 * @param city
	 *            the given city
	 */
	@Deprecated
	public void initializeAllocationOLD(SimulationDescription sd, int weekday, String city) {
		HolidayManager hm = HolidayManager.getInstance(HolidayCalendar.UNITED_STATES);
		Set<Holiday> holidays = hm.getHolidays(2011);
		holidays.addAll(hm.getHolidays(2012));

		for (Station station : sd.getStations().getStation()) {
			try {
				String sql = "SELECT s.nbBikes, s.nbEmptyDocks, FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)) as date, WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)))"
						+ " as weekday FROM station s JOIN stations ss ON s.stationsId = ss.id WHERE ss.city LIKE ? AND s.installed = 1 AND s.locked = 0 AND"
						+ " WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) = ? AND s.name LIKE ? LIMIT 10000";
				PreparedStatement stmt = getConnection().prepareStatement(sql);
				stmt.setString(1, city);
				stmt.setInt(2, weekday - 1);
				stmt.setString(3, station.getStationID());

				ResultSet rs = stmt.executeQuery();
				int bikes = 0;
				int docks = 0;
				int size = 0;

				while (rs.next()) {
					int nbBikes = rs.getInt("nbBikes");
					int nbEmptyDocks = rs.getInt("nbEmptyDocks");
					Date date = rs.getDate("date");

					if (!isHoliday(holidays, date)) {
						bikes += nbBikes;
						docks += nbBikes + nbEmptyDocks;

						++size;
					}
				}

				bikes = (int) Math.round((double) bikes / (double) size);
				docks = (int) Math.round((double) docks / (double) size);

				if (bikes == 0)
					bikes = 10;

				if (docks < bikes)
					docks = bikes;

				station.setNumberOfBikes(bikes);
				station.setNumberOfDocks(docks);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Checks if the given {@link Date} is contained in the given {@link Set} of {@link Holiday}s and thereby is a Holiday.
	 * 
	 * @param holidays
	 *            the given {@link Set} of {@link Holiday}s
	 * @param date
	 *            the given {@link Date}
	 * @return <code>true</code> if the given {@link Date} is a {@link Holiday}, else <code>false</code>
	 */
	private boolean isHoliday(Set<Holiday> holidays, Date date) {
		for (Holiday holiday : holidays) {
			Date holidayDate = holiday.getDate().toDate();

			if (holidayDate.equals(date)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * Transforms the GeoPositions (Latitude and Longitude) into a Jadex Space compatible format.
	 * 
	 * @param station
	 *            - the given {@link Station}
	 */
	private void transformGeoPosition(Station station) {
		double xDistance = EAST - WEST;
		double yDistance = NORTH - SOUTH;

		double xFactor = 100.0 / xDistance;
		double yFactor = 100.0 / yDistance;

		double xPercentage = (station.getLongitude() - WEST) * xFactor;
		double yPercentage = (station.getLatitude() - NORTH) * yFactor;

		double x = SPACE_WIDTH * (xPercentage / 100.0);
		double y = Math.abs(SPACE_HEIGHT * (yPercentage / 100.0));

		station.setLatitude(y);
		station.setLongitude(x);
	}

	/**
	 * Postprocesses the given {@link SimulationDescription} by replacing all "&" in the station ids by the actual word "and".
	 * 
	 * @param sd
	 *            the given {@link SimulationDescription}
	 */
	public void postProcessStatioNames(SimulationDescription sd) {
		if (sd.getStations() != null) {
			List<Station> stations = sd.getStations().getStation();
			for (Station station : stations) {
				String stationId = station.getStationID();
				station.setStationID(stationId.replace("&", "and"));
			}
		}

		List<TimeSlice> timeSlices = sd.getTimeSlices().getTimeSlice();
		for (TimeSlice timeSlice : timeSlices) {
			if (timeSlice.getProbabilitiesForStations() != null) {
				List<ProbabilitiesForStation> probabilitiesForStations = timeSlice.getProbabilitiesForStations().getProbabilitiesForStation();
				for (ProbabilitiesForStation probabilitiesForStation : probabilitiesForStations) {
					String stationId = probabilitiesForStation.getStationID();
					probabilitiesForStation.setStationID(stationId.replace("&", "and"));

					List<DestinationProbability> destinationProbabilities = probabilitiesForStation.getDestinationProbabilities().getDestinationProbability();
					for (DestinationProbability destinationProbability : destinationProbabilities) {
						String destination = destinationProbability.getDestination();
						destinationProbability.setDestination(destination.replace("&", "and"));
					}
				}
			}

			if (timeSlice.getStations() != null) {
				List<Station> stations = timeSlice.getStations().getStation();
				for (Station station : stations) {
					String stationId = station.getStationID();
					station.setStationID(stationId.replace("&", "and"));
				}
			}
		}
	}

	/**
	 * Fetches the average allocations for all bike stations in the given time interval for the given city and weekday.
	 * 
	 * @param city
	 *            the given city
	 * @param startHour
	 *            the start of the time interval (inclusive)
	 * @param endHour
	 *            the end of the time interval (exclusive)
	 * @param weekday
	 *            the given weekday
	 * @return a {@link SimulationDescription} with the average allocations for all bike stations in the given time interval for the given city and weekday or <code>null</code> if an error occurs
	 */
	public SimulationDescription getAllocations(String city, Integer startHour, Integer endHour, Integer weekday, Integer limit) {
		HolidayManager hm = HolidayManager.getInstance(HolidayCalendar.UNITED_STATES);
		Set<Holiday> holidays = hm.getHolidays(2011);
		holidays.addAll(hm.getHolidays(2012));

		String sql = "SELECT s.name, s.nbBikes, s.nbEmptyDocks, FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)) as date, WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10)))"
				+ " as weekday, HOUR(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) as hour FROM station s JOIN stations ss ON s.stationsId = ss.id WHERE ss.city LIKE ? AND"
				+ " s.installed = 1 AND s.locked = 0 AND WEEKDAY(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) = ? AND HOUR(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) >= ?"
				+ " AND HOUR(FROM_UNIXTIME(SUBSTRING(ss.lastUpdate, 1, 10))) < ?";
		if (limit != null) {
			sql += " LIMIT " + limit;
		}
		try {
			PreparedStatement stmt = getConnection().prepareStatement(sql);
			stmt.setString(1, city);
			stmt.setInt(2, weekday);
			stmt.setInt(3, startHour);
			stmt.setInt(4, endHour);

			// Process the results from the database
			ResultSet rs = stmt.executeQuery();
			Map<Integer, List<StationTimeData>> dataByHour = new HashMap<Integer, List<StationTimeData>>();
			while (rs.next()) {
				String stationId = rs.getString("name");
				int nbBikes = rs.getInt("nbBikes");
				int nbEmptyDocks = rs.getInt("nbEmptyDocks");
				Date date = rs.getDate("date");
				int hour = rs.getInt("hour");

				if (!isHoliday(holidays, date)) {
					StationTimeData std = new StationTimeData();
					std.setStationId(stationId);
					std.setNbBikes(nbBikes);
					std.setNbEmptyDocks(nbEmptyDocks);
					std.setDate(date);
					std.setHour(hour);

					if (dataByHour.containsKey(hour)) {
						dataByHour.get(hour).add(std);
					} else {
						List<StationTimeData> data = new ArrayList<StationTimeData>();
						data.add(std);
						dataByHour.put(hour, data);
					}
				}
			}

			// Create the SimulationDescription
			ObjectFactory of = new ObjectFactory();
			SimulationDescription sd = of.createSimulationDescription();
			TimeSlices timeSlices = of.createSimulationDescriptionTimeSlices();

			// Average the data...
			// ...first by hour...
			for (Integer hour : dataByHour.keySet()) {
				// ...then by stations
				Map<String, List<StationTimeData>> dataByHourAndStation = partitionByStation(dataByHour.get(hour));
				sodekovs.bikesharing.model.TimeSlice.Stations stations = of.createTimeSliceStations();

				for (String stationId : dataByHourAndStation.keySet()) {
					List<StationTimeData> stationTimeDatas = dataByHourAndStation.get(stationId);

					int bikes = 0;
					int docks = 0;
					for (StationTimeData stationTimeData : stationTimeDatas) {
						bikes += stationTimeData.getNbBikes();
						docks += stationTimeData.getNbBikes() + stationTimeData.getNbEmptyDocks();
					}

					bikes = (int) Math.round((double) bikes / (double) stationTimeDatas.size());
					docks = (int) Math.round((double) docks / (double) stationTimeDatas.size());

					Station station = of.createStation();
					station.setStationID(stationId);
					station.setNumberOfBikes(bikes);
					station.setNumberOfDocks(docks);
					stations.getStation().add(station);
				}

				TimeSlice timeSlice = of.createTimeSlice();
				timeSlice.setStartTime(hour * 60);
				timeSlice.setOffset(60);
				timeSlice.setStations(stations);
				timeSlices.getTimeSlice().add(timeSlice);
			}

			sd.setTimeSlices(timeSlices);
			return sd;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * Partitions the given {@link List} of {@link StationTimeData} into a {@link Map} by their station ids.
	 * 
	 * @param list
	 *            the given list
	 * @return the partitioned map
	 */
	private Map<String, List<StationTimeData>> partitionByStation(List<StationTimeData> list) {
		Map<String, List<StationTimeData>> dataByHourAndStation = new HashMap<String, List<StationTimeData>>();

		for (StationTimeData stationTimeData : list) {
			if (dataByHourAndStation.containsKey(stationTimeData.getStationId())) {
				dataByHourAndStation.get(stationTimeData.getStationId()).add(stationTimeData);
			} else {
				List<StationTimeData> data = new ArrayList<StationTimeData>();
				data.add(stationTimeData);
				dataByHourAndStation.put(stationTimeData.getStationId(), data);
			}
		}

		return dataByHourAndStation;
	}
}