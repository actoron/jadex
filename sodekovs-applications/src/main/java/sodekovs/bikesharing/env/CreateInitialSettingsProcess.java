package sodekovs.bikesharing.env;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.math.Vector2Double;

import java.io.IOException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sodekovs.util.bikesharing.model.SimulationDescription;
import sodekovs.util.bikesharing.model.Station;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to create the init setting of pedestrians.
 */
public class CreateInitialSettingsProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------

	// -------- constructors --------

	/**
	 * Create a new create package process.
	 */
	public CreateInitialSettingsProcess() {
		System.out.println("Created Initial Settings Process!");
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
			createBikeStations((String) getProperty("simDataSetupFilePath"), space);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
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
	}

	/**
	 * 
	 * Create bikestations according to the xml setup file.
	 * 
	 * @param path
	 *            path of the xml file
	 * @param grid
	 *            the used space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void createBikeStations(String path, IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {
		SimulationDescription scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(path, SimulationDescription.class);

		for (Station station : scenario.getStations().getStation()) {

			HashMap<String, Object> props = new HashMap<String, Object>();
			double x = new Double(station.getLongitude());
			double y = new Double(station.getLatitude());
			props.put("position", new Vector2Double(x, y));
			props.put("stationID", station.getStationID());
			props.put("capacity", station.getNumberOfDocks());
			props.put("stock", station.getNumberOfBikes());
			props.put("type", "Bikestation");

			space.createSpaceObject("bikestation", props, null);
		}

		// put it here, so it can be reused within the application without the need to parse again
		space.setProperty("SimulationDescription", scenario);
	}
}
