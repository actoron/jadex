package sodekovs.bikesharing.simulation;

import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;
import jadex.extension.envsupport.environment.ISpaceProcess;
import jadex.extension.envsupport.environment.space2d.Space2D;
import jadex.extension.envsupport.math.IVector1;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.math.Vector3Double;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sodekovs.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.bikesharing.bahnverwaltung.LinienInformation;
import sodekovs.bikesharing.bahnverwaltung.LinienInformationen;
import sodekovs.bikesharing.container.Weg;
import sodekovs.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.bikesharing.fahrrad.FahrradVerleihStationen;
import sodekovs.bikesharing.standard.VerkehrsmittelAgent;
import sodekovs.bikesharing.verkehrsteilnehmer.SucheNeuenWegPlan;
import sodekovs.bikesharing.verkehrsteilnehmer.ZielWaehlPlan;
import sodekovs.bikesharing.zeit.Zeitverwaltung;

//TODO: Startorte Verkehrsteilnehmer, Startzeitenverteilung

/**
 * Startet die Simulation
 * 
 * @author David Georg Reichelt & Ante Vilenica
 * 
 */
public class StartSimulationProzess extends SimplePropertyObject implements
		ISpaceProcess {

	public static String SPACE = "simulationsspace";
	public static String STATION = "station";
	public static String FAHRRADVERLEIHSTATION = "fahrradverleihstation";
	public static String UBAHN = "ubahn";
	public static String SBAHN = "sbahn";
	public static String SUBAHN = "subahn";
	public static String TYP = "typ";
	
	public static String fahrgastdaten = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/FahrgastDaten.xml";
	public static String fahrplandaten = "E:/Workspaces/Jadex/Jadex Test Instanz/jadex/sodekovs-applications/src/main/java/sodekovs/bikesharing/setting/FahrplanDaten.xml";
	
	public static void setzFahrgastDaten( String fahrgastdaten )
	{
		StartSimulationProzess.fahrgastdaten = fahrgastdaten;
	}
	
	@Override
	public void start(IClockService clock, IEnvironmentSpace space) {
	}
	
	boolean gestartet = false;
	public void execute( IClockService clock, IEnvironmentSpace space) {
		if ( gestartet ){
			System.out.println("Called init process.");
			return;
		}
		gestartet = true;
		System.out.println("Start init process.");
		Zeitverwaltung.createInstance(100);
		// System.out.println("Zeit: " + clock.getTime() );
		// System.out.println( Zeitverwaltung.gibInstanz().gibTageszeit());
		Space2D grid = (Space2D) space;

		try {
			macheVerkehrsmittel(fahrplandaten, grid);
			System.out.println("Verkehrsmittel erstellt");
			macheVerkehrsteilnehmer(fahrgastdaten, grid);
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
		// get
		// SServiceProvider.getService(getServiceProvider(),
		// IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM );
		grid.createSpaceObject("zeitagent", null, null);

		System.out.println("Alles erstellt");

		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
	}

	/**
	 * Erstellt die Verkehrsteilnehmer aus dem übergebenen Pfad
	 * 
	 * @param pfad
	 *            Pfad der Datei
	 * @param grid
	 *            Space2D, in das die Verkehrsmittel gepackt werden sollen
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void macheVerkehrsteilnehmer(String pfad, Space2D grid)
			throws ParserConfigurationException, SAXException, IOException {
		File file = new File(pfad);
		// TODO: nicht hard-coded
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		NodeList nl = doc.getElementsByTagName("fahrgaeste");
		Node fahrgastNode = nl.item(0);
		NamedNodeMap attributes = fahrgastNode.getAttributes();
		Integer anzahl = new Integer(attributes.getNamedItem("anzahl")
				.getNodeValue());
		System.out.println("Anzahl: " + anzahl);

		List<Ort> startorte = new ArrayList<Ort>();
		List<Ort> zielorte = new ArrayList<Ort>();

		nl = doc.getElementsByTagName("startorte").item(0).getChildNodes();
		startorte = machOrtsListe(nl);

		nl = doc.getElementsByTagName("zielorte").item(0).getChildNodes();
		zielorte = machOrtsListe(nl);

//		Map<Integer, Ort> startMap = gibGewichtungsMap( startorte );
//		Map<Integer, Ort> zielMap = gibGewichtungsMap( startorte );
		
		
		for (int i = 0; i < anzahl; i++) {
			int zufallStart = (int) ((Math.random() * startorte.size()));
			int zufallEnde = (int) ((Math.random() * zielorte.size()));
			while (startorte.get(zufallStart).equals(zielorte.get(zufallEnde))) {
				zufallEnde = (int) ((Math.random() * zielorte.size()));
			}
			
			int gesamtgewicht = 0;
			for( Ort o : startorte )
			{
				gesamtgewicht += o.gibGewichtung();
			}

			Vector2Double wohnort = startorte.get(zufallStart).gibAlsVektor();
			Vector2Double arbeitsort = zielorte.get(zufallEnde).gibAlsVektor();
			Vector2Double trinkort = zielorte.get(zufallEnde).gibAlsVektor();
			Vector2Double essort = zielorte.get(zufallEnde).gibAlsVektor();

//			System.out.println("Trinkort: " + trinkort + ZielWaehlPlan.TRINKORT);
			
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put("type", "verkehrsteilnehmer");
			properties.put(Space2D.PROPERTY_POSITION, wohnort);
			properties.put(ZielWaehlPlan.WOHNORT, wohnort);
			properties.put(ZielWaehlPlan.ARBEITSORT, arbeitsort);
			properties.put(ZielWaehlPlan.TRINKORT, trinkort);
			properties.put("essort", essort);
			properties.put(ZielWaehlPlan.MORGENSSTART, new Integer(
					(int) (60 * 8 + (Math.random() * 2 - 1) * 60)));
			properties.put(ZielWaehlPlan.ABENDSENDE, new Integer(
					(int) (60 * 17 + (Math.random() * 2 - 1) * 60)));
			properties.put(SucheNeuenWegPlan.FAHRRADPRAEFERENZ,
					new Double(Math.sqrt(Math.random())));
//					1.0);
			System.out.println("Erstelle Verkehrsteilnehmer");
			grid.createSpaceObject("verkehrsteilnehmer", properties, null);
			 System.out.println("Index: " + i);
		}

	}
	
//	private Ort ziehOrt( Map<Integer, Ort> ortsMap)
//	{
//		ortsMap.keySet().toArray()
////		Map.Entry<Integer, Ort> entry = ortsMap.entrySet().toArray()[ortsMap.size() - 1];
//	}
	
	private Map<Integer, Ort> gibGewichtungsMap( List<Ort> ortsliste )
	{
		Map<Integer, Ort> ortsMap = new HashMap<Integer, Ort>();
		int pos = 0;
		for ( Ort o : ortsliste )
		{
			ortsMap.put( pos, o);
			pos += o.gibGewichtung();
		}
		return ortsMap;
	}

	private List<Ort> machOrtsListe(NodeList nl) {
		List<Ort> ortsliste = new ArrayList<Ort>();
		for (int i = 0; i < nl.getLength(); i++) {
			Node ort = nl.item(i);
			if (ort.hasAttributes()) {
				NamedNodeMap attr = ort.getAttributes();
				Double x = new Double(attr.getNamedItem("x").getNodeValue());
				Double y = new Double(attr.getNamedItem("y").getNodeValue());
				Double gewichtung = new Double(attr.getNamedItem("gewicht")
						.getNodeValue());
				if (ort.getNodeName().equals("ort")) {
					for ( int j = 0; j < gewichtung; j++ )
					{
						ortsliste.add(new Ort(x, y, gewichtung));
					}
					// System.out.println("Ort: " + x + " " + y);
				}
				if (ort.getNodeName().equals("bereich")) {
					Double breite = new Double(attr.getNamedItem("breite").getNodeValue());
					Double hoehe = new Double(attr.getNamedItem("hoehe").getNodeValue());
					for ( int j = 0; j < gewichtung; j++ )
					{
						ortsliste.add( new Bereich(x, y, gewichtung, breite, hoehe) );
					}
				}
			}

		}
		return ortsliste;
	}

	/**
	 * Erstellt die öffentlichen Verkehrsmittel anhand der übergebenen Datei
	 * 
	 * @param pfad
	 *            Pfad der Datei
	 * @param grid
	 *            Space2D, in das die Verkehrsmittel gepackt werden sollen
	 * @throws ParserConfigurationException
	 * @throws IOException
	 * @throws SAXException
	 */
	private void macheVerkehrsmittel(String pfad, Space2D grid)
			throws ParserConfigurationException, SAXException, IOException {
		File file = new File(pfad);
		// TODO: nicht hard-coded
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document doc = db.parse(file);

		NodeList nl = doc.getElementsByTagName("stationen");
//		Node stationsNode = nl.item(0);

//		List<ISpaceObject> bahnStationsVisualisierungen = new LinkedList<ISpaceObject>();
//
//		erstellBahnstationen(grid, bahnStationsVisualisierungen, stationsNode);
//
//		nl = doc.getElementsByTagName("linien");
//		Node linienVaterNode = nl.item(0);
//
//		erstellLinien(grid, bahnStationsVisualisierungen, linienVaterNode);

		nl = doc.getElementsByTagName("fahrradverleihstationen");
		Node fahrradVaterNode = nl.item(0);

		erstellFahrradVerleihStationen(fahrradVaterNode);
		erstellVerleihObjekte(grid);
	}

	/**
	 * Erstellt alle Fahrradstationen, indem es von dem
	 * FahrradVaterNode-ausgehend das Singleton FahrradVerleihStationen füllt
	 * 
	 * @param fahrradVaterNode
	 *            Knoten, unter dem die FahrradVerleihStation-Elemente
	 *            gespeichert sind
	 */
	public static void erstellFahrradVerleihStationen(Node fahrradVaterNode) {
		List<FahrradVerleihStation> verleihStationen = new ArrayList<FahrradVerleihStation>();
		for (int fahrradstationNr = 0; fahrradstationNr < fahrradVaterNode
				.getChildNodes().getLength(); fahrradstationNr++) {
			Node fahrradStation = fahrradVaterNode.getChildNodes().item(
					fahrradstationNr);
			if (fahrradStation.hasAttributes()) {
				NamedNodeMap attributes = fahrradStation.getAttributes();
				double x = new Double(attributes.getNamedItem("x")
						.getNodeValue());
				double y = new Double(attributes.getNamedItem("y")
						.getNodeValue());
				String stationsName = attributes.getNamedItem("name")
						.getNodeValue();
				int kapazitaet = new Integer(attributes.getNamedItem(
						"kapazitaet").getNodeValue());
				int initialeRaeder = new Integer(attributes.getNamedItem(
						"initialeRaeder").getNodeValue());
				Vector2Double position = new Vector2Double(x, y);
				FahrradVerleihStation f = new FahrradVerleihStation(position,
						kapazitaet, initialeRaeder, stationsName);
				verleihStationen.add(f);
				// System.out.println("Erstelle Station: " + position);

			}
		}
		FahrradVerleihStationen.erstellInstanz(verleihStationen);
	}

	/**
	 * Erstellt für jede FahrradVerleihStation aus FahrradVerleihStationen ein
	 * ISpaceObject und fügt es in den Space ein
	 * 
	 * @param grid
	 *            Space, in den das Objekt eingefügt werden soll
	 */
	private void erstellVerleihObjekte(Space2D grid) {
		for (FahrradVerleihStation f : FahrradVerleihStationen.gibInstanz()
				.gibStationen()) {
			Map<String, Object> props = new HashMap<String, Object>();
			props.put(Space2D.PROPERTY_POSITION, f.gibPosition());
			props.put("station", f);
			props.put("stationsname", f.gibName());
			props.put(FahrradVerleihStation.FAHRRADANZAHL,
					new Integer(f.gibFahrradAnzahl()));
			ISpaceObject iso = grid.createSpaceObject(FAHRRADVERLEIHSTATION,
					props, null);
			f.setzObjekt(iso); // Zyklische Referenzen... aber was will man
								// machen?
		}

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(Space2D.PROPERTY_POSITION, new Vector2Double(10, 10));
		props.put(FahrradVerleihStation.FAHRRADANZAHL, new Integer(0));
		grid.createSpaceObject(SimulationStarter.dispositionsAgentenTyp, props, null);
	}

	private void erstellLinien(Space2D grid,
			List<ISpaceObject> bahnStationsVisualisierungen,
			Node linienVaterNode) {
		Map<String, Weg> linien = new HashMap<String, Weg>();
		Map<String, LinienInformation> linieninfos = new HashMap<String, LinienInformation>();
		for (int linienNr = 0; linienNr < linienVaterNode.getChildNodes()
				.getLength(); linienNr++) {
			Node linienNodeIterator = linienVaterNode.getChildNodes().item(
					linienNr);
			Weg aktuellerWeg = new Weg();

			List<Integer> abstaende = new ArrayList<Integer>(); // Mehr
																// Zugriffe,
																// wenig
																// Eintragungen
			for (int stationsNr = 0; stationsNr < linienNodeIterator
					.getChildNodes().getLength(); stationsNr++) {
				Node stationsIterator = linienNodeIterator.getChildNodes()
						.item(stationsNr);
				if (stationsIterator.hasAttributes()) {
					NamedNodeMap attributes = stationsIterator.getAttributes();
					// System.out.println("Attributes: " + attributes);
					// for (int i = 0; i < attributes.getLength(); i++)
					// {
					// System.out.println("Item: " + attributes.item(i));
					// }
					Node stationsLinkNode = attributes.getNamedItem("name");
					String stationsName = stationsLinkNode.getNodeValue();
					BahnStation station = BahnStationen.gibInstanz()
							.gibStation(stationsName);
					// System.out.println("Suche Station: " + stationsName);
					aktuellerWeg.addStation(station.gibPosition());
					// System.out.println("Stationsname: " + stationsName +
					// " Pos: " + station.gibPosition());

					Node abstandsNode = attributes.getNamedItem("abstand");
					Integer abstand = new Integer(abstandsNode.getNodeValue());
					abstaende.add(abstand);
				}
			}

			if (linienNodeIterator.hasAttributes()) {
				String linienname = linienNodeIterator.getAttributes()
						.getNamedItem("name").getNodeValue();
				Integer fahrzeuge = Integer.valueOf(linienNodeIterator
						.getAttributes().getNamedItem("fahrzeuge")
						.getNodeValue());
				Integer takt = Integer.valueOf(linienNodeIterator
						.getAttributes().getNamedItem("takt").getNodeValue());
				Integer start = Integer.valueOf(linienNodeIterator
						.getAttributes().getNamedItem("start").getNodeValue());
				Integer ende = Integer.valueOf(linienNodeIterator
						.getAttributes().getNamedItem("ende").getNodeValue());

				IVector2 vorgaenger = aktuellerWeg.gibStart();

				for (IVector2 station : aktuellerWeg.gibStationen()) {
					BahnStation vorgaengerStation = BahnStationen.gibInstanz()
							.gibNaechsteStation(vorgaenger);
					BahnStation nachfolgerStation = BahnStationen.gibInstanz()
							.gibNaechsteStation(station);

					// System.out.println("Vorgängerstation: " + vorgaenger +
					// " Nachfolgerstation: " + station);
					setzVisualisierungWeg(grid, vorgaenger, station);

					// Zurzeit sind unterschiedliche Entfernungen zwischen den
					// Stationen je nach Richtung unmöglich,
					// es ist darüber nachzudenken, ob das mal geändert werden
					// sollte
					IVector1 distanz = nachfolgerStation.gibPosition()
							.getDistance(vorgaengerStation.gibPosition());
					vorgaengerStation.fuegNachbarHinzu(nachfolgerStation,
							distanz.getAsInteger());
					nachfolgerStation.fuegNachbarHinzu(vorgaengerStation,
							distanz.getAsInteger());

					// vorgaengerStation.fuegNachbarHinzu(nachfolgerStation,
					// abstaende.get(index))

					setzVisualisierungHaltestelle(bahnStationsVisualisierungen,
							station, linienname);

					vorgaenger = station;
				}
				// TODO: lesbare Fehler bei falscher Struktur, Code besser
				// lesbar machen
				linien.put(linienname, aktuellerWeg);
				LinienInformation l = new LinienInformation(linienname, start,
						ende, takt, fahrzeuge, abstaende);
				linieninfos.put(linienname, l);
			}
			// System.out.println("Ende");
		}
		LinienInformationen.erstellInstanz(linieninfos);

		for (Map.Entry<String, LinienInformation> info : linieninfos.entrySet()) {
			erstellEndstationsagent(grid, info.getKey(),
					linien.get(info.getKey()).gibStart());
			erstellEndstationsagent(grid, info.getKey(),
					linien.get(info.getKey()).gibEnde());

			for (int i = 0; i < info.getValue().gibFahrzeuge(); i++) {
				erstelleFahrzeug(grid, info.getKey(),
						linien.get(info.getKey()),
						((i % 2) == 0) ? linien.get(info.getKey()).gibStart()
								: linien.get(info.getKey()).gibEnde(),
						(i % 2) != 0);
			}
		}

	}

	private void erstellBahnstationen(Space2D grid,
			List<ISpaceObject> bahnStationsVisualisierungen, Node stationsNode) {
		List<BahnStation> stationen = new ArrayList<BahnStation>();

		for (int i = 0; i < stationsNode.getChildNodes().getLength(); i++) {
			Node nd = stationsNode.getChildNodes().item(i);
			if (nd.hasAttributes()) {
				NamedNodeMap attributes = nd.getAttributes();
				Node x = attributes.getNamedItem("x");
				Node y = attributes.getNamedItem("y");
				Node name = attributes.getNamedItem("name");
				double xval = new Double(x.getNodeValue()).doubleValue();
				double yval = new Double(y.getNodeValue()).doubleValue();
				String nameString = name.getNodeValue();
				// System.out.println("X: " + xval + " Y: " + yval + "Name: " +
				// nameString);

				stationen.add(new BahnStation(new Vector2Double(xval, yval),
						nameString));

				Map<String, Object> props = new HashMap<String, Object>();
				props.put(Space2D.PROPERTY_POSITION, new Vector2Double(xval,
						yval));
				props.put("stationsname", nameString);
				props.put("typ", "");
				ISpaceObject station = grid.createSpaceObject(STATION, props,
						null);
				bahnStationsVisualisierungen.add(station);
			}
		}
		BahnStationen.erstellInstanz(stationen);
	}

	private void erstellEndstationsagent(Space2D grid, String linie,
			IVector2 position) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "endstationsagent");
		properties.put(Space2D.PROPERTY_POSITION, position);
		properties.put(VerkehrsmittelAgent.LINIE, linie);
		grid.createSpaceObject("endstationsagent", properties, null);
	}

	private void erstelleFahrzeug(Space2D grid, String linienName, Weg linie,
			IVector2 startort, Boolean richtung) {
		Map<String, Object> properties = new HashMap<String, Object>();
		properties.put("type", "sbahn");
		properties.put(VerkehrsmittelAgent.POSITION, startort);
		properties.put(VerkehrsmittelAgent.OFFEN, new Boolean(false));
		// System.out.println("Linie: " + linienName);
		properties.put(VerkehrsmittelAgent.LINIE, linie);
		properties.put(VerkehrsmittelAgent.LINIENNAME, linienName);
		properties.put(VerkehrsmittelAgent.RICHTUNG, richtung);
		ISpaceObject obj = grid.createSpaceObject("sbahn", properties, null);
		// System.out.println("object: " + obj);
	}

	private void setzVisualisierungWeg(Space2D grid, IVector2 start,
			IVector2 ziel) {
		IVector2 diffVektor = start.copy().subtract(ziel);

		IVector2 addVektor = diffVektor.copy();
		addVektor = new Vector2Double(addVektor.getXAsDouble() * 0.5,
				addVektor.getYAsDouble() * 0.5); // Multiplyfehler?
		IVector2 pos = ziel.copy();
		pos.add(addVektor);

		Map<String, Object> props = new HashMap<String, Object>();
		props.put(Space2D.PROPERTY_POSITION, pos);
		props.put("start", start);
		props.put("ziel", ziel);
		Vector3Double vec = new Vector3Double(0.0, 0.0,
				diffVektor.getDirectionAsDouble());
		props.put("winkel", vec);
		Vector2Double vec2 = new Vector2Double(start.getDistance(ziel)
				.getAsDouble(), 0.2);
		props.put("laenge", vec2);

		grid.createSpaceObject("linie", props, null);
	}

	/**
	 * Sorgt für die Visualisierung der Stationen (als U/S/.. Station) und für
	 * das Speichern der haltenden Linien
	 * 
	 * @param bahnStationsVisualisierungen
	 *            List mit allen Bahnstationen
	 * @param station
	 *            Position der betroffenen Station
	 * @param linienname
	 *            Linie, die an der Station hält
	 */
	private void setzVisualisierungHaltestelle(
			List<ISpaceObject> bahnStationsVisualisierungen, IVector2 station,
			String linienname) {
		BahnStationen.gibInstanz().gibStation(station)
				.fuegLinieHinzu(linienname);
		for (ISpaceObject iso : bahnStationsVisualisierungen) {
			if (station.equals(iso.getProperty(Space2D.PROPERTY_POSITION))) {
				String typ = (String) iso.getProperty(TYP);
				String typneu = "";

				if (linienname.matches("U[1-9]*")) {
					typneu = UBAHN;
					iso.setProperty(TYP, UBAHN);
				}
				if (linienname.matches("S[1-9]*")) {
					typneu = SBAHN;
					iso.setProperty(TYP, SBAHN);
				}

				if (!typneu.equals(typ)) {
					if (typ.equals("")) {
						iso.setProperty(TYP, typneu);
					} else {
						iso.setProperty(TYP, SUBAHN);
					}
				}
			}
		}
	}

//	@Override
//	public void execute(IClockService arg0, IEnvironmentSpace arg1) {
//		// TODO Auto-generated method stub
//
//	}

	@Override
	public void shutdown(IEnvironmentSpace arg0) {
		// TODO Auto-generated method stub

	}

}
