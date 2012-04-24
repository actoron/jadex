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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sodekovs.bikesharing.model.SimulationDescription;
import sodekovs.bikesharing.model.Station;
import sodekovs.bikesharing.model.TimeSlice;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to manage the time slices from the simulation-setup xml-file that describe the probability of different events.
 */
public class ManageTimeSlicesProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------
	public static String simulationSetup = "E:/Workspaces/Jadex/Jadex mit altem Maven 2/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/HamburgSimulation.xml";
	private SimulationDescription scenario;
	private List<TimeSlice> timeSlicesList;
	private HashMap<String,Station> stationsMap;
	//Position of currently executed time slice within the ordered list
	private int currentTimeSlice = 0;

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
			init(simulationSetup, space);
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
		System.out.println(clock.getTick());
		
//		if((currentTimeSlice+1)timeSlicesList.get(index)){
//			
//		}
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
		scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(simulationSetup, SimulationDescription.class);
		
		//make stations available via HashMap
		stationsMap = new HashMap<String,Station>();
		for(Station station : scenario.getStations().getStation()){
			stationsMap.put(station.getStationID(), station);
		}
		
		//order time slices ascending by start time
		timeSlicesList = scenario.getTimeSlices().getTimeSlice();
		createOrderedTimeSlicesList(timeSlicesList);
		
//		for(TimeSlice slice : timeSlicesList){
//			System.out.println("Time SLice start time: " + slice.getStartTime());
//		}
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
	
}
