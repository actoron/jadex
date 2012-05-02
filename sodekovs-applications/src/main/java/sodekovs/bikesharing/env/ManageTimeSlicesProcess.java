package sodekovs.bikesharing.env;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;

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
import sodekovs.bikesharing.model.TimeSlice.ProbabilitiesForStations;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to manage the time slices from the simulation-setup xml-file that describe the probability of different events.
 */
public class ManageTimeSlicesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------
//	public static String simulationSetup = "E:/Workspaces/Jadex/Jadex mit altem Maven 2/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/HamburgSimulation.xml";
	private SimulationDescription scenario;
	private List<TimeSlice> timeSlicesList;
	private HashMap<String, Station> stationsMap;
	// the difference between the time the start of the simulation time according to the plattform and when this process is executed for the first time
	// required in order to synchronize these to "times"
	private double tickDelta = -1;
	private Random rand = new java.util.Random();	

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

		try {
			init((String)getProperty("simDataSetupFilePath"), space);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
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

		for (int i = 0; i < timeSlicesList.size(); i++) {
			// avoid index out of bounds exception
			if (i + 1 < timeSlicesList.size()) {
				if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta)) && (timeSlicesList.get(i + 1).getStartTime() > (clock.getTick() - tickDelta))) {
					executeTimeSlice(i);
					System.out.println("Time Slice exetuced:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
					break;
				}
			} else if ((timeSlicesList.get(i).getStartTime() <= (clock.getTick() - tickDelta))) {
				executeTimeSlice(i);
				System.out.println("Time Slice exetuced:  " + timeSlicesList.get(i).getStartTime() + " tickTime: " + (clock.getTick() - tickDelta));
			}
		}
	}

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
	private void init(String path, IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {
		scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(path, SimulationDescription.class);

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
	private void executeTimeSlice(int currentTimeSlice) {
		//iterate through stations list
		for(ProbabilitiesForStation station : timeSlicesList.get(currentTimeSlice).getProbabilitiesForStations().getProbabilitiesForStation()){
			//compute probability of a departure
			if(station.getDepartureProbability()<=rand.nextDouble()){
				//compute destination probability of this departure
				//HACK: use probabilities defined in xml instead of random
				rand.nextInt(station.getDestinationProbabilities().getDestinationProbability().size());
				DestinationProbability dest = station.getDestinationProbabilities().getDestinationProbability().get(rand.nextInt(station.getDestinationProbabilities().getDestinationProbability().size()));
				System.out.println("Start Event from: " + station.getStationID() +"  to : " + dest.getDestination());				
			}
			
		}
		

	}

}
