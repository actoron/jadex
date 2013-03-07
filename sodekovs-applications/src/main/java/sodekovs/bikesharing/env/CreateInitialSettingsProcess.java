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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sodekovs.bikesharing.model.SimulationDescription;
import sodekovs.bikesharing.model.Station;
import sodekovs.bikesharing.simulation.Bereich;
import sodekovs.bikesharing.simulation.Ort;
import sodekovs.old.bikesharing.verkehrsteilnehmer.SucheNeuenWegPlan;
import sodekovs.old.bikesharing.verkehrsteilnehmer.ZielWaehlPlan;
import sodekovs.util.math.GetRandom;
import sodekovs.util.misc.XMLHandler;

/**
 * Process is responsible to create the init setting of pedestrians.
 */
public class CreateInitialSettingsProcess extends SimplePropertyObject implements ISpaceProcess {
	// -------- attributes --------
	public static String pedestriansdataOLD = "E:/Workspaces/Jadex/Jadex mit altem Maven 2/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/FahrgastDaten.xml";
	public static String bikestationdataOLD = "E:/Workspaces/Jadex/Jadex mit altem Maven 2/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/FahrplanDaten.xml";
//	public static String simulationSetup = "E:/Workspaces/Jadex/Jadex mit altem Maven/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/HamburgSimulation.xml";

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
//			createPedestrians(pedestriansdataOLD, space);
			// createBikeStationsOld(bikestationdataOLD, space);
//			createBikeStations(simulationSetup, space);
			createBikeStations((String)getProperty("simDataSetupFilePath"), space);
						
			// System.out.println("Created bike stations according to xml file.");
			// macheVerkehrsteilnehmer(fahrgastdaten, grid);
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
	 * // * Create and start a pedestrian component. //
	 */
	// private void createPedestrians(IComponentManagementService cms, final
	// IEnvironmentSpace space) {
	// cms.createComponent("Pedestrian-" + GetRandom.getRandom(100000),
	// "sodekovs/bikesharing/pedestrian/Pedestrian.agent.xml",
	// new CreationInfo(null, new HashMap<String, Object>(),
	// space.getExternalAccess().getComponentIdentifier(), false, false),
	// null).addResultListener(new DefaultResultListener() {
	// public void resultAvailable(Object result) {
	// System.out.println("Created Component Pedestrian");
	// System.out.println("pedestrian in Space?: " +
	// space.getSpaceObjectsByType("pedestrian").length);
	// }
	// });
	// }
	/**
	 * Create pedestrians according to the xml setup file.
	 * 
	 * @param path
	 *            path
	 * @param space
	 *            space
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void createPedestrians(String path, final IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {

		File file = new File(path);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		NodeList nl = doc.getElementsByTagName("fahrgaeste");
		Node fahrgastNode = nl.item(0);
		NamedNodeMap attributes = fahrgastNode.getAttributes();
		Integer anzahl = new Integer(attributes.getNamedItem("anzahl").getNodeValue());
		System.out.println("#CreateInitialSettingsProcess# Number od pedestrians im xml-file: " + anzahl);

		List<Ort> startorte = new ArrayList<Ort>();
		List<Ort> zielorte = new ArrayList<Ort>();

		nl = doc.getElementsByTagName("startorte").item(0).getChildNodes();
		startorte = machOrtsListe(nl);

		nl = doc.getElementsByTagName("zielorte").item(0).getChildNodes();
		zielorte = machOrtsListe(nl);

		// Map<Integer, Ort> startMap = gibGewichtungsMap( startorte );
		// Map<Integer, Ort> zielMap = gibGewichtungsMap( startorte );

		final ArrayList<HashMap<String, Object>> pedestrians = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < anzahl; i++) {
			int zufallStart = (int) ((Math.random() * startorte.size()));
			int zufallEnde = (int) ((Math.random() * zielorte.size()));
			while (startorte.get(zufallStart).equals(zielorte.get(zufallEnde))) {
				zufallEnde = (int) ((Math.random() * zielorte.size()));
			}

			// int gesamtgewicht = 0;
			// for (Ort o : startorte) {
			// gesamtgewicht += o.gibGewichtung();
			// }

			Vector2Double wohnort = startorte.get(zufallStart).gibAlsVektor();
			Vector2Double arbeitsort = zielorte.get(zufallEnde).gibAlsVektor();
			Vector2Double trinkort = zielorte.get(zufallEnde).gibAlsVektor();
			Vector2Double essort = zielorte.get(zufallEnde).gibAlsVektor();

			// System.out.println("Trinkort: " + trinkort +
			// ZielWaehlPlan.TRINKORT);

			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put("type", "verkehrsteilnehmer");
			properties.put(Space2D.PROPERTY_POSITION, wohnort);
			properties.put(ZielWaehlPlan.WOHNORT, wohnort);
			properties.put(ZielWaehlPlan.ARBEITSORT, arbeitsort);
			properties.put(ZielWaehlPlan.TRINKORT, trinkort);
			properties.put("essort", essort);
			properties.put(ZielWaehlPlan.MORGENSSTART, new Integer((int) (60 * 8 + (Math.random() * 2 - 1) * 60)));
			properties.put(ZielWaehlPlan.ABENDSENDE, new Integer((int) (60 * 17 + (Math.random() * 2 - 1) * 60)));
			properties.put(SucheNeuenWegPlan.FAHRRADPRAEFERENZ, new Double(Math.sqrt(Math.random()))); // 1.0);

			pedestrians.add(properties);
			// space.createSpaceObject("verkehrsteilnehmer", properties, null);
		}

		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				IComponentManagementService cms = (IComponentManagementService) result;
				// System.out.println("Got CMS.");
				// createPedestrians(cms, space);

				for (HashMap<String, Object> pedestrian : pedestrians) {

					// TODO: Hack: Add more properties here
					HashMap<String, Object> tmp = new HashMap<String, Object>();
					tmp.put(Space2D.PROPERTY_POSITION, pedestrian.get(Space2D.PROPERTY_POSITION));

					cms.createComponent("Pedestrian-" + GetRandom.getRandom(100000), "sodekovs/bikesharing/pedestrian/Pedestrian.agent.xml",
							new CreationInfo(null, tmp, space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
						public void resultAvailable(Object result) {
							// System.out.println("Created Component Pedestrian");
							// System.out.println("pedestrian in Space?: "
							// +
							// space.getSpaceObjectsByType("pedestrian").length);
						}
					});
				}
			}

		});

		// System.out.println("#CreateInitialSettingsProcess# pedestrians in Space?: "
		// + space.getSpaceObjectsByType("pedestrian").length);
	}

	/**
	 * 
	 * @param nl
	 * @return
	 */
	private List<Ort> machOrtsListe(NodeList nl) {
		List<Ort> ortsliste = new ArrayList<Ort>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node ort = nl.item(i);
			if (ort.hasAttributes()) {
				NamedNodeMap attr = ort.getAttributes();
				Double x = new Double(attr.getNamedItem("x").getNodeValue());
				Double y = new Double(attr.getNamedItem("y").getNodeValue());
				Double gewichtung = new Double(attr.getNamedItem("gewicht").getNodeValue());
				if (ort.getNodeName().equals("ort")) {
					for (int j = 0; j < gewichtung; j++) {
						ortsliste.add(new Ort(x, y, gewichtung));
					}
					// System.out.println("Ort: " + x + " " + y);
				}
				if (ort.getNodeName().equals("bereich")) {
					Double breite = new Double(attr.getNamedItem("breite").getNodeValue());
					Double hoehe = new Double(attr.getNamedItem("hoehe").getNodeValue());
					for (int j = 0; j < gewichtung; j++) {
						ortsliste.add(new Bereich(x, y, gewichtung, breite, hoehe));
					}
				}
			}

		}
		return ortsliste;
	}

	/**
	 * *****************DAVIDS VERSION******************
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
	private void createBikeStationsOld(String path, IEnvironmentSpace space) throws ParserConfigurationException, SAXException, IOException {
		File file = new File(path);
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		NodeList nl = doc.getElementsByTagName("stationen");
		nl = doc.getElementsByTagName("fahrradverleihstationen");
		Node fahrradVaterNode = nl.item(0);

		// erstellFahrradVerleihStationen(fahrradVaterNode);
		// List<FahrradVerleihStation> verleihStationen = new
		// ArrayList<FahrradVerleihStation>();
		for (int fahrradstationNr = 0; fahrradstationNr < fahrradVaterNode.getChildNodes().getLength(); fahrradstationNr++) {
			Node fahrradStation = fahrradVaterNode.getChildNodes().item(fahrradstationNr);
			if (fahrradStation.hasAttributes()) {
				HashMap<String, Object> props = new HashMap<String, Object>();
				NamedNodeMap attributes = fahrradStation.getAttributes();
				double x = new Double(attributes.getNamedItem("x").getNodeValue());
				double y = new Double(attributes.getNamedItem("y").getNodeValue());
				props.put("position", new Vector2Double(x, y));
				props.put("name", attributes.getNamedItem("name").getNodeValue());
				props.put("capacity", new Integer(attributes.getNamedItem("kapazitaet").getNodeValue()));
				props.put("stock", new Integer(attributes.getNamedItem("initialeRaeder").getNodeValue()));
				// FahrradVerleihStation f = new FahrradVerleihStation(position,
				// capacity, stock, name);

				space.createSpaceObject("bikestation", props, null);
				// verleihStationen.add(f);				 
			}
		}
		// FahrradVerleihStationen.erstellInstanz(verleihStationen);

		// erstellVerleihObjekte(grid);
	}

	/**
	 * *****************NEWS VERSION FOR SODEKOVS Project******************
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

//		IComponentManagementService cms = cms = (IComponentManagementService) SServiceProvider.getService(getScope().getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
////		IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(getServiceContainer(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(this);
		
		for (Station station : scenario.getStations().getStation()) {

			HashMap<String, Object> props = new HashMap<String, Object>();
			double x = new Double(station.getLongitude());
			double y = new Double(station.getLatitude());
			props.put("position", new Vector2Double(x, y));
			props.put("stationID", station.getStationID());
			props.put("capacity", station.getNumberOfDocks());
			props.put("stock", station.getNumberOfBikes());
			props.put("type", "Bikestation");

			//Old version with ISpaceObjects
			space.createSpaceObject("bikestation", props, null);
//			createBikeStation(space,props);
			
//			IFuture<IComponentManagementService> fut = cms.createComponent("Scheduler" + GetRandom.getRandom(100000), Constants.PATH_OF_SCHEDULER, new CreationInfo(null, props, null, true, false), null);
//			schedulerCID = (IComponentIdentifier) fut.get(this);
			
			
//			System.out.println("Create Station: " + x + "," + y);
		}
		
		//put it here, so it can be reused within the application without the need to parse again
		space.setProperty("SimulationDescription", scenario);
		// }
		// FahrradVerleihStationen.erstellInstanz(verleihStationen);

		// erstellVerleihObjekte(grid);
	}
	
	private void createBikeStation(final IEnvironmentSpace space,HashMap<String, Object> props) {

		SServiceProvider.getServiceUpwards(space.getExternalAccess().getServiceProvider(), IComponentManagementService.class).addResultListener(new DefaultResultListener() {
			public void resultAvailable(Object result) {
				final IComponentManagementService cms = (IComponentManagementService) result;

				// longi = x-pos
//				Station depStation = stationsMap.get(departureStation);
//				Station destStation = stationsMap.get(destinationStation);
//				final Vector2Double depPos = new Vector2Double(depStation.getLongitude(), depStation.getLatitude());
//				Vector2Double destPos = new Vector2Double(destStation.getLongitude(), destStation.getLatitude());

				// TODO: Hack: Add more properties here?
//				HashMap<String, Object> properties = new HashMap<String, Object>();
//				properties.put("destination_station_pos", destPos);

				cms.createComponent("Bikestation-" + GetRandom.getRandom(100000), "sodekovs/bikesharing/bikestation/Bikestation.agent.xml",
						new CreationInfo(null, properties, space.getExternalAccess().getComponentIdentifier(), false, false), null).addResultListener(new DefaultResultListener() {
					public void resultAvailable(Object result) {
						final IComponentIdentifier cid = (IComponentIdentifier) result;
						cms.getComponentDescription(cid).addResultListener(new DefaultResultListener() {
							public void resultAvailable(Object result) {
								// add the start position to the agent/avatar
//								space.getAvatar((IComponentDescription) result).setProperty(Space2D.PROPERTY_POSITION, depPos);
//								space.getAvatar((IComponentDescription) result).setProperty(Space2D.PROPERTY_POSITION, depPos);

							}
						});
					}
				});
			}
		});
}
}
