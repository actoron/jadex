package sodekovs.bikesharing.env;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.DefaultResultListener;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.Vector2Double;

import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sodekovs.bikesharing.model.DestinationProbability;
import sodekovs.bikesharing.model.ProbabilitiesForStation;
import sodekovs.bikesharing.model.SimulationDescription;
import sodekovs.bikesharing.model.Station;
import sodekovs.bikesharing.model.TimeSlice;
import sodekovs.util.math.GetRandom;

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

//	int counterTmp = 0;

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

//			init((String) getProperty("simDataSetupFilePath"), space);
			init(space);		
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

//		if (counterTmp < 6) {
			for (int i = 0; i < timeSlicesList.size(); i++) {
				// avoid index out of bounds exception
				if (i + 1 < timeSlicesList.size()) {
					if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta)) && (timeSlicesList.get(i + 1).getStartTime() > (clock.getTick() - tickDelta))) {
						executeTimeSlice(i, space);
//						System.out.println("Time Slice exetuced:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
//						counterTmp++;
						break;
					}
				} else if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta))) {
					executeTimeSlice(i, space);
//					System.out.println("Time Slice exetuced:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
				}
			}
		}
//	}

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
	private void init(IEnvironmentSpace space){
		scenario = (SimulationDescription) space.getProperty("SimulationDescription");

		// make stations available via HashMap
		stationsMap = new HashMap<String, Station>();
		for (Station station : scenario.getStations().getStation()) {
			stationsMap.put(station.getStationID(), station);
		}

		// order time slices ascending by start time
		timeSlicesList = scenario.getTimeSlices().getTimeSlice();
		createOrderedTimeSlicesList(timeSlicesList);

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
		// iterate through stations list
		for (ProbabilitiesForStation station : timeSlicesList.get(currentTimeSlice).getProbabilitiesForStations().getProbabilitiesForStation()) {
			// compute probability of a departure
			if (station.getDepartureProbability() >= rand.nextDouble()) {
				// compute destination probabilities of this departure
				int destination = computeDestination(station);
				System.out.println("Start Event from: " + station.getStationID() + "  to : " + station.getDestinationProbabilities().getDestinationProbability().get(destination).getDestination());
				createPedestrian(space, station.getStationID(), station.getDestinationProbabilities().getDestinationProbability().get(destination).getDestination());
			}

		}
	}

	/**
	 * Compute the destination station of this departure according to the probabilities defined in the xml.
	 * @param station
	 * @return
	 */
	private int computeDestination(ProbabilitiesForStation station) {
		double destProb = rand.nextDouble();
		List<DestinationProbability> destProbabilites = station.getDestinationProbabilities().getDestinationProbability();

//		System.out.println("rand is: +" + destProb);

		double sum = 0.0;
		for (int i = 0; i < destProbabilites.size(); i++) {

			sum += destProbabilites.get(i).getProbability();

			// avoid index out of bounds exception and additionally avoid error that the sum of all probabilities is not exactly 1.0
			if (i + 1 < destProbabilites.size()) {
				if (sum >= destProb) {
//					System.out.println("prob pos: " + i + " has won");
					return i;
				}
			} else {
//				System.out.println("#last bucket# prob pos: " + i + " has won");
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
	private void createPedestrian(final IEnvironmentSpace space, final String departureStation, final String destinationStation) {

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
						new CreationInfo(null, properties, space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
					public void resultAvailable(Object result) {
						final IComponentIdentifier cid = (IComponentIdentifier) result;
						cms.getComponentDescription(cid).addResultListener(new DefaultResultListener() {
							public void resultAvailable(Object result) {
								//add the start position to the agent/avatar
								space.getAvatar((IComponentDescription) result).setProperty(Space2D.PROPERTY_POSITION, depPos);

							}
						});
					}
				});
			}
		});

		// System.out.println("#CreateInitialSettingsProcess# pedestrians in Space?: "
		// + space.getSpaceObjectsByType("pedestrian").length);
	}
}
