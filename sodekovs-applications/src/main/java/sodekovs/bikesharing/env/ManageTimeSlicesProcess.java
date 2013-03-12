package sodekovs.bikesharing.env;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.ContinuousSpace2D;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import sodekovs.util.bikesharing.model.DestinationProbability;
import sodekovs.util.bikesharing.model.ProbabilitiesForStation;
import sodekovs.util.bikesharing.model.SimulationDescription;
import sodekovs.util.bikesharing.model.Station;
import sodekovs.util.bikesharing.model.TimeSlice;
import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to manage the time slices from the simulation-setup xml-file that describe the probability of different events.
 */
public class ManageTimeSlicesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------
	// public static String simulationSetup = "E:/Workspaces/Jadex/Jadex mit altem Maven 2/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/HamburgSimulation.xml";
	private SimulationDescription scenario;
	private List<TimeSlice> timeSlicesList;
	private HashMap<String, Station> stationsMap;
	// the difference between the time the start of the simulation time according to the plattform and when this process is executed for the first time
	// required in order to synchronize these to "times"
	private double tickDelta = -1;
	private Random rand = new java.util.Random();
	private IClockService clockservice;
	private ISimulationService simulationservice;
	private int totalDepartures = 0;
	// Limit nr of created pedestrian. required for better debugging.
	private int limitPedNr = 0;

	// int counterTmp = 0;

	// -------- constructors --------

	/**
	 * Create a new create process.
	 */
	public ManageTimeSlicesProcess() {
		// System.out.println("Created Initial Settings Process!");
	}

	// -------- ISpaceProcess interface --------

	/**
	 * This method will be executed by the object before the process gets added to the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void start(IClockService clock, final IEnvironmentSpace space) {

		// create
		try {
			createBikeStations((String) getProperty("simDataSetupFilePath"), (String) getProperty("clusterSetupFilePath"), space);
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		init(space);

		// *******************************************************************************
		// If time manipulations are required

		// initSimService(space);
		// initClockService(space);
		// printClockAndSimSettings();
		// //Change Sim and Clock settings
		// setClockAndSimSettings(50);
		// printClockAndSimSettings();

		// *******************************************************************************
	}

	/**
	 * This method will be executed by the object before the process is removed from the execution queue.
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space) {
		// System.out.println("create package process shutdowned.");
	}

	/**
	 * Executes the environment process
	 * 
	 * @param clock
	 *            The clock.
	 * @param space
	 *            The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space) {

		if (tickDelta == -1) {
			tickDelta = clock.getTick();
		}

//		if (limitPedNr < 30) {
//			limitPedNr++;

			// if (counterTmp < 6) {
			for (int i = 0; i < timeSlicesList.size(); i++) {
				// avoid index out of bounds exception
				if (i + 1 < timeSlicesList.size()) {
					if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta)) && (timeSlicesList.get(i + 1).getStartTime() > (clock.getTick() - tickDelta))) {
						executeTimeSlice(i, space);
						// System.out.println("Time Slice executed:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
						// counterTmp++;
						break;
					}
				} else if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta))) {
					executeTimeSlice(i, space);
					// System.out.println("Time Slice executed:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
				}
			}

//		}

		// printClockAndSimSettings();
	}

	// }

	/**
	 * Parse the xml setup file.
	 * 
	 * @param path
	 *            path of the xml file
	 * @param grid
	 *            the used space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void init(IEnvironmentSpace space) {
		scenario = (SimulationDescription) space.getProperty("SimulationDescription");

		// make stations available via HashMap
		stationsMap = new HashMap<String, Station>();
		for (Station station : scenario.getStations().getStation()) {
			stationsMap.put(station.getStationID(), station);
		}

		// order time slices ascending by start time
		timeSlicesList = scenario.getTimeSlices().getTimeSlice();
		createOrderedTimeSlicesList(timeSlicesList);

		// compute number of total departures -> required for "Andrang"
		for (int i = 0; i < timeSlicesList.size(); i++) {
			totalDepartures += timeSlicesList.get(i).getRunTotal();
		}

		// for(TimeSlice slice : timeSlicesList){
		// System.out.println("Time SLice start time: " + slice.getStartTime());
		// }
	}

	/**
	 * Returns the list of time slices ascendingly ordered by relative start time.
	 */
	private void createOrderedTimeSlicesList(List<TimeSlice> timeSlicesList) {
		Collections.sort(timeSlicesList, new Comparator() {
			public int compare(Object arg0, Object arg1) {
				return Long.valueOf(((TimeSlice) arg0).getStartTime()).compareTo(Long.valueOf(((TimeSlice) arg1).getStartTime()));
			}
		});
	}

	/**
	 * Execute the simulation according to the specified settings for this, i.e. current, time slice.
	 * 
	 * @param currentTimeSlice
	 */
	private void executeTimeSlice(int currentTimeSlice, final IEnvironmentSpace space) {

		// compute departure station for this tick.
		List<ProbabilitiesForStation> stationList = timeSlicesList.get(currentTimeSlice).getProbabilitiesForStations().getProbabilitiesForStation();

		// Create number of pedestrians according to the "Andrang"
		long congestion = Math.round((timeSlicesList.get(currentTimeSlice).getRunRelative() * totalDepartures) / 60);
		// System.out.println("#Congestion: # " + cong);
		for (int i = 0; i < congestion; i++) {
			int departureStation = computeDeparture(stationList);

			// compute destination probabilities of this departure
			int destination = computeDestination(stationList.get(departureStation));
			// System.out.println("##Start Event from: " + stationList.get(departureStation).getStationID() + "  to : " +
			// stationList.get(departureStation).getDestinationProbabilities().getDestinationProbability().get(destination));
			createPedestrianAsISpaceObject(space, stationList.get(departureStation).getStationID(),
					stationList.get(departureStation).getDestinationProbabilities().getDestinationProbability().get(destination).getDestination());
		}
	}

	/**
	 * Compute the destination station of this departure according to the probabilities defined in the xml.
	 * 
	 * @param station
	 * @return
	 */
	private int computeDestination(ProbabilitiesForStation station) {
		double destProb = rand.nextDouble();
		List<DestinationProbability> destProbabilites = station.getDestinationProbabilities().getDestinationProbability();

		// System.out.println("rand is: +" + destProb);

		double sum = 0.0;
		for (int i = 0; i < destProbabilites.size(); i++) {

			sum += destProbabilites.get(i).getProbability();

			// avoid index out of bounds exception and additionally avoid error that the sum of all probabilities is not exactly 1.0
			if (i + 1 < destProbabilites.size()) {
				if (sum >= destProb) {
					// System.out.println("prob pos: " + i + " has won");
					return i;
				}
			} else {
				// System.out.println("#last bucket# prob pos: " + i + " has won");
				return i;
			}
		}
		return -1;
	}

	/**
	 * Compute the departure station for this tick and TimeSlice according to the probabilities defined in the xml.
	 * 
	 * @param station
	 * @return
	 */
	private int computeDeparture(List<ProbabilitiesForStation> stationList) {

		double deptartureProb = rand.nextDouble();
		// System.out.println("rand is: +" + deptartureProb);

		double sum = 0.0;
		for (int i = 0; i < stationList.size(); i++) {

			sum += stationList.get(i).getDepartureProbability();

			// avoid index out of bounds exception and additionally avoid error that the sum of all probabilities is not exactly 1.0
			if (i + 1 < stationList.size()) {
				if (sum >= deptartureProb) {
					// System.out.println("prob pos: " + i + " has won");
					return i;
				}
			} else {
				// System.out.println("#last bucket# prob pos: " + i + " has won");
				return i;
			}
		}
		return -1;
	}

	/**
	 * Create pedestrian
	 * 
	 * @param path
	 *            path
	 * @param space
	 *            space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void createPedestrianAsBDIAgent(final IEnvironmentSpace space, final String departureStation, final String destinationStation) {

		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				final IComponentManagementService cms = (IComponentManagementService) result;
				// System.out.println("Got CMS.");
				// createPedestrians(cms, space);

				// longi = x-pos
				Station depStation = stationsMap.get(departureStation);
				Station destStation = stationsMap.get(destinationStation);
				final Vector2Double depPos = new Vector2Double(depStation.getLongitude(), depStation.getLatitude());
				Vector2Double destPos = new Vector2Double(destStation.getLongitude(), destStation.getLatitude());

				// TODO: Hack: Add more properties here?
				HashMap<String, Object> properties = new HashMap<String, Object>();
				properties.put("destination_station_pos", destPos);

				cms.createComponent("Pedestrian-" + GetRandom.getRandom(100000), "sodekovs/bikesharing/pedestrian/Pedestrian.agent.xml",
				// cms.createComponent("Truck-" + GetRandom.getRandom(100000), "sodekovs/bikesharing/truck/Truck.agent.xml",
						new CreationInfo(null, properties, space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
					public void resultAvailable(Object result) {
						final IComponentIdentifier cid = (IComponentIdentifier) result;
						cms.getComponentDescription(cid).addResultListener(new DefaultResultListener() {
							public void resultAvailable(Object result) {
								// add the start position to the agent/avatar
								space.getAvatar((IComponentDescription) result).setProperty(ContinuousSpace2D.PROPERTY_POSITION, depPos);

							}
						});
					}
				});
			}
		});
	}

	/**
	 * Create pedestrian
	 * 
	 * @param path
	 *            path
	 * @param space
	 *            space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void createPedestrianAsISpaceObject(final IEnvironmentSpace space, final String departureStation, final String destinationStation) {

		Station depStation = stationsMap.get(departureStation);
		Station destStation = stationsMap.get(destinationStation);
		final Vector2Double depPos = new Vector2Double(depStation.getLongitude(), depStation.getLatitude());
		Vector2Double destPos = new Vector2Double(destStation.getLongitude(), destStation.getLatitude());

		HashMap<String, Object> props = new HashMap<String, Object>();
		props.put(ContinuousSpace2D.PROPERTY_POSITION, depPos);
		props.put("destination_station_pos", destPos);
		space.createSpaceObject("pedestrian", props, null);

	}

	private void initSimService(final IEnvironmentSpace space) {
		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), ISimulationService.class).addResultListener(new DefaultResultListener<ISimulationService>() {
			public void resultAvailable(ISimulationService result) {
				simulationservice = result;
				// System.out.println("Got SIM Serv!: " + simulationservice.toString());
			}
		});
	}

	private void initClockService(final IEnvironmentSpace space) {
		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IClockService.class).addResultListener(new DefaultResultListener<IClockService>() {
			public void resultAvailable(IClockService result) {
				clockservice = result;
				// System.out.println("Got Clock Serv!: " + clockservice.getState());
			}
		});
	}

	private void printClockAndSimSettings() {
		simulationservice.getMode().addResultListener(new DefaultResultListener<String>() {
			public void resultAvailable(String result) {
				String clockState = result;
				System.out.println("#ManageTimeSlicesProcess# + Current Tick: " + clockservice.getTick() + " - Delta: " + clockservice.getDelta() + " - SimServState: " + clockState);
			}
		});
	}

	private void setClockAndSimSettings(long tickSize) {

		// change clock type
		simulationservice.setClockType(IClock.TYPE_TIME_DRIVEN);

		// change tick size
		clockservice.setDelta(tickSize);
	}

	/**
	 * 
	 * Create bikestations according to the xml setup file.
	 * 
	 * @param path
	 *            path of the xml file
	 * @param clusterPath
	 *            path of the cluster xml file
	 * @param grid
	 *            the used space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void createBikeStations(String path, String clusterPath, IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {
		SimulationDescription scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(path, SimulationDescription.class);
		SuperCluster superCluster = (SuperCluster) XMLHandler.parseXMLFromXMLFile(clusterPath, SuperCluster.class);

		// get all super station names (ids)
		List<String> superStations = new ArrayList<String>();
		for (Cluster cluster : superCluster.getCluster()) {
			superStations.add(cluster.getSuperStation().getName());
		}

		for (Station station : scenario.getStations().getStation()) {

			HashMap<String, Object> props = new HashMap<String, Object>();
			double x = new Double(station.getLongitude());
			double y = new Double(station.getLatitude());
			props.put("position", new Vector2Double(x, y));
			props.put("stationID", station.getStationID());
			props.put("capacity", station.getNumberOfDocks());
			props.put("stock", station.getNumberOfBikes());
//			if ("23rd and Crystal Dr".equals(station.getStationID())) {
//				props.put("proposed_departure_station", "16th and U St NW");
//			} else {
				props.put("proposed_arrival_station", null);
//			}

//			if ("17th and K St NW [formerly 17th and L St NW]".equals(station.getStationID())) {
//				props.put("proposed_arrival_station", "10th and U St NW");
//			} else {
				props.put("proposed_arrival_station", null);
//			}

			// check if station is a super station
			if (superStations.contains(station.getStationID())) {
				props.put("isSuperStation", true);
			} else {
				props.put("isSuperStation", false);
			}

			space.createSpaceObject("bikestation", props, null);
		}

		// put it here, so it can be reused within the application without the need to parse again
		space.setProperty("SimulationDescription", scenario);
		space.setProperty("StationCluster", superCluster);
	}

}
