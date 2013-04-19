package sodekovs.bikesharing.env;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.math.Vector2Double;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import sodekovs.bikesharing.data.clustering.Cluster;
import sodekovs.bikesharing.data.clustering.SuperCluster;
import sodekovs.util.bikesharing.model.SimulationDescription;
import sodekovs.util.bikesharing.model.Station;
import sodekovs.util.misc.GlobalConstants;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to create the init setting of pedestrians.
 */
public class CreateInitialSettingsProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------
	private String simDataSetupFilePath = null;
	private String clusterSetupFilePath = null;
	private SuperCluster superCluster = null;
	private List<String> superStations = new ArrayList<String>();
	private SimulationDescription scenario = null;
	private int stationIterationCounter = 0;

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

		clusterSetupFilePath = (String) getProperty("clusterSetupFilePath");
		simDataSetupFilePath = (String) getProperty("simDataSetupFilePath");
		superCluster = (SuperCluster) XMLHandler.parseXMLFromXMLFile(clusterSetupFilePath, SuperCluster.class);
		scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(simDataSetupFilePath, SimulationDescription.class);

		// get all super station names (ids)
		for (Cluster cluster : superCluster.getCluster()) {
			superStations.add(cluster.getSuperStation().getName());
		}

		// try {
		// createBikeStations((String) getProperty("simDataSetupFilePath"), (String) getProperty("clusterSetupFilePath"), space);
		// createBikeStationsAsBDIAgent((String) getProperty("simDataSetupFilePath"), (String) getProperty("clusterSetupFilePath"), space);
		// } catch (ParserConfigurationException e) {
		// e.printStackTrace();
		// } catch (SAXException e) {
		// e.printStackTrace();
		// } catch (IOException e) {
		// e.printStackTrace();
		// }

		// HashMap<String, Object> props = new HashMap<String, Object>();
		// props.put("simDataSetupFilePath", (String) getProperty("simDataSetupFilePath"));
		// props.put("clusterSetupFilePath", (String) getProperty("clusterSetupFilePath"));

		// space.createSpaceProcess("manageTimeSlices", props);
		// space.removeSpaceProcess(getProperty(ISpaceProcess.ID));

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
		// System.out.println("Executed ME: " + clock.getTick());
		// put it here, so it can be reused within the application without the need to parse again
		space.setProperty("SimulationDescription", scenario);
		space.setProperty("StationCluster", superCluster);
		
		// Check if all stations have been created
		if (stationIterationCounter < scenario.getStations().getStation().size()) {
			createBikeStations(space);
		} else {
			HashMap<String, Object> props = new HashMap<String, Object>();
			props.put("simDataSetupFilePath", simDataSetupFilePath);
			props.put("clusterSetupFilePath", clusterSetupFilePath);

			// HACK: Required for Simulation-Control!!! Make sure right start time is used for later evaluation
			space.setProperty("REAL_START_TIME_OF_SIMULATION", clock.getTime());
			space.setProperty("REAL_START_TICKTIME_OF_SIMULATION", (Long) space.getProperty(GlobalConstants.TICK_COUNTER_4_EVENT_BASED_SIMULATION));
//			System.out.println("Setting new Start Tick Size: " + space.getProperty("REAL_START_TICKTIME_OF_SIMULATION"));
			// Hack: Inform Client Sim to update both times above
			space.setProperty("UpdateTimeAtClientSimulator", true);

			space.createSpaceProcess("manageTimeSlices", props);
			space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
		}
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
	private void createBikeStations(IEnvironmentSpace space) {
		// SimulationDescription scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(path, SimulationDescription.class);
		// SuperCluster superCluster = (SuperCluster) XMLHandler.parseXMLFromXMLFile(clusterPath, SuperCluster.class);
		//
		// // get all super station names (ids)
		// List<String> superStations = new ArrayList<String>();
		// for (Cluster cluster : superCluster.getCluster()) {
		// superStations.add(cluster.getSuperStation().getName());
		// }

		// for (Station station : scenario.getStations().getStation()) {
		Station station = scenario.getStations().getStation().get(stationIterationCounter);

		HashMap<String, Object> props = new HashMap<String, Object>();
		double x = new Double(station.getLongitude());
		double y = new Double(station.getLatitude());
		props.put("position", new Vector2Double(x, y));
		props.put("stationID", station.getStationID());
		props.put("capacity", station.getNumberOfDocks());
		props.put("stock", station.getNumberOfBikes());
		// if ("23rd and Crystal Dr".equals(station.getStationID())) {
		// props.put("proposed_departure_station", "16th and U St NW");
		// } else {
		props.put("proposed_departure_station", null);
		// }
		//
		// if ("17th and K St NW [formerly 17th and L St NW]".equals(station.getStationID())) {
		// props.put("proposed_arrival_station", "10th and U St NW");
		// } else {
		props.put("proposed_arrival_station", null);
		// }

		// check if station is a super station
		if (superStations.contains(station.getStationID())) {
			props.put("isSuperStation", true);
		} else {
			props.put("isSuperStation", false);
		}

		space.createSpaceObject("bikestation", props, null);
		// }

		stationIterationCounter++;
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
	private void createBikeStationsAsBDIAgent(final String path, final String clusterPath, final IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {

		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener() {
			public void resultAvailable(final Object result) {
				space.getExternalAccess().scheduleStep(new IComponentStep<Void>() {
					public IFuture<Void> execute(IInternalAccess ia) {
						final IComponentManagementService cms = (IComponentManagementService) result;

						SimulationDescription scenario = (SimulationDescription) XMLHandler.parseXMLFromXMLFile(path, SimulationDescription.class);
						SuperCluster superCluster = (SuperCluster) XMLHandler.parseXMLFromXMLFile(clusterPath, SuperCluster.class);

						// get all super station names (ids)
						List<String> superStations = new ArrayList<String>();
						for (Cluster cluster : superCluster.getCluster()) {
							superStations.add(cluster.getSuperStation().getName());
						}

						final ArrayList<HashMap<String, Object>> masterProp = new ArrayList<HashMap<String, Object>>();
						for (Station station : scenario.getStations().getStation()) {

							HashMap<String, Object> props = new HashMap<String, Object>();
							double x = new Double(station.getLongitude());
							double y = new Double(station.getLatitude());
							props.put("position", new Vector2Double(x, y));
							props.put("stationID", station.getStationID());
							props.put("capacity", station.getNumberOfDocks());
							props.put("stock", station.getNumberOfBikes());
							props.put("testBelief", new String("wow-1"));
							// if ("23rd and Crystal Dr".equals(station.getStationID())) {
							// props.put("proposed_departure_station", "16th and U St NW");
							// } else {
							props.put("proposed_departure_station", null);
							// }
							//
							// if ("17th and K St NW [formerly 17th and L St NW]".equals(station.getStationID())) {
							// props.put("proposed_arrival_station", "10th and U St NW");
							// } else {
							props.put("proposed_arrival_station", null);
							// }

							// check if station is a super station
							if (superStations.contains(station.getStationID())) {
								props.put("isSuperStation", true);
							} else {
								props.put("isSuperStation", false);
							}

							masterProp.add(props);

							// cms.createComponent("Bikestation" , "sodekovs/bikesharing/bikestation/Bikestation.agent.xml",
							// HACK: Has to be started "suspended" in order to force start from DeparturePos. Otherwise componten will start from random pos that is computed by the avatar when it is
							// intialized.
							// new CreationInfo(null, null, space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
							// public void resultAvailable(Object result) {
							// System.out.println("Started: " + result);
							// final IComponentIdentifier cid = (IComponentIdentifier) result;
							// cms.getComponentDescription(cid).addResultListener(new DefaultResultListener() {
							// public void resultAvailable(Object result) {
							// add the start position to the agent/avatar
							// space.getAvatar((IComponentDescription) result).setProperty(ContinuousSpace2D.PROPERTY_POSITION, depPos);
							// cms.resumeComponent(cid).addResultListener(new DefaultResultListener() {
							// public void resultAvailable(Object result) {
							//
							// }
							// });
							// }
							// });
							// }
							// });
						}

						CounterResultListener<IComponentIdentifier> creslist = new CounterResultListener(masterProp.size(), new DefaultResultListener<IComponentIdentifier>() {
							public void resultAvailable(IComponentIdentifier result) {

								final IComponentIdentifier cid = (IComponentIdentifier) result;
								cms.getComponentDescription(cid).addResultListener(new DefaultResultListener() {
									public void resultAvailable(Object result) {
										ISpaceObject obj = space.getAvatar((IComponentDescription) result);
										System.out.println("Obj: " + obj);
									}

									public void exceptionOccurred(Exception exception) {
										System.out.println("2" + exception.toString());
									};
								});

							};

							public void exceptionOccurred(Exception exception) {
								System.out.println("1" + exception.toString());
							};
						}) {
							public void resultAvailable(Object result) {

								super.resultAvailable(result);
								if (!notified) {
									// bla[cnt+1]
									cms.createComponent("Bikestation", "sodekovs/bikesharing/bikestation/Bikestation.agent.xml",
									// HACK: Has to be started "suspended" in order to force start from DeparturePos. Otherwise componten will start from random pos that is computed by the avatar when
									// it is
									// intialized.
											new CreationInfo(null, masterProp.get(cnt), space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(this);
								}
							}
						};
						// bla[0]
						cms.createComponent("Bikestation", "sodekovs/bikesharing/bikestation/Bikestation.agent.xml",
						// HACK: Has to be started "suspended" in order to force start from DeparturePos. Otherwise componten will start from random pos that is computed by the avatar when it is
						// intialized.
								new CreationInfo(null, masterProp.get(0), space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(creslist);

						// put it here, so it can be reused within the application without the need to parse again
						space.setProperty("SimulationDescription", scenario);
						space.setProperty("StationCluster", superCluster);
						return IFuture.DONE;
					}
				});
			}
		});
	}
}
