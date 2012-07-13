package sodekovs.old.bikesharing.datenkonvertierung;

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

import java.awt.Color;
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

import sodekovs.bikesharing.simulation.BahnStationen;
import sodekovs.old.bikesharing.bahnverwaltung.BahnStation;
import sodekovs.old.bikesharing.bahnverwaltung.LinienInformation;
import sodekovs.old.bikesharing.bahnverwaltung.LinienInformationen;
import sodekovs.old.bikesharing.container.Weg;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStation;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;

//TODO: Startorte Verkehrsteilnehmer, Startzeitenverteilung

/**
 * Startet die nur-daten-visualisier-simulation
 * 
 * @author David Georg Reichelt
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
	
	public static String fahrgastdaten = "sodekovs/bikesharing/setting/FahrgastDaten.xml";
	public static String fahrplandaten = "sodekovs/bikesharing/setting/FahrplanDaten.xml";
	
	public static void setzFahrgastDaten( String fahrgastdaten )
	{
		StartSimulationProzess.fahrgastdaten = fahrgastdaten;
	}
	
	@Override
	public void start(IClockService clock, IEnvironmentSpace space) {

		Zeitverwaltung.createInstance(100);
		// System.out.println("Zeit: " + clock.getTime() );
		// System.out.println( Zeitverwaltung.gibInstanz().gibTageszeit());
		Space2D grid = (Space2D) space;
		System.out.println("Start");

		try {
			macheVerkehrsmittel(fahrplandaten, grid);
			System.out.println("Verkehrsmittel erstellt");

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

		System.out.println("Alles erstellt");

		space.removeSpaceProcess(getProperty(ISpaceProcess.ID));
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
		Node stationsNode = nl.item(0);

		List<ISpaceObject> bahnStationsVisualisierungen = new LinkedList<ISpaceObject>();

		erstellBahnstationen(grid, bahnStationsVisualisierungen, stationsNode);

		nl = doc.getElementsByTagName("linien");
		Node linienVaterNode = nl.item(0);

		erstellLinien(grid, bahnStationsVisualisierungen, linienVaterNode);

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
			double abweich = 1;
			for ( Map.Entry<FahrradVerleihStation, Integer> f2 : AehnlichkeitsAnalysator.abweichungen.entrySet() )
			{
//				System.out.println("Entry: " + f2.getKey() + " " + f2.getValue());
				if ( f2.getKey().equals( f ))
				{
					
					abweich = (double) f2.getValue() / AehnlichkeitsAnalysator.maxAbweich;
					System.out.println("Gefunden!!" + abweich + " " + f2.getValue() + " " + AehnlichkeitsAnalysator.maxAbweich);
				}
			}
//			System.out.println("Station: " + f.gibName() + "Abweichung: " + AehmlichkeitsAnalysator.abweichungen.get( f ));
//			double abweich = (AehmlichkeitsAnalysator.abweichungen.get( f ) != null) ? 
//					AehmlichkeitsAnalysator.abweichungen.get( f ) / AehmlichkeitsAnalysator.maxAbweich : 1;
			abweich *= 255;
			Color auslastung = new Color( (int)abweich, (int) (255 - abweich), 0);
			props.put("auslastung", auslastung);
			props.put(FahrradVerleihStation.FAHRRADANZAHL,
					new Integer(f.gibFahrradAnzahl()));
			ISpaceObject iso = grid.createSpaceObject(FAHRRADVERLEIHSTATION,
					props, null);
			f.setzObjekt(iso); // Zyklische Referenzen... aber was will man
								// machen?
		}

//		Map<String, Object> props = new HashMap<String, Object>();
//		props.put(Space2D.PROPERTY_POSITION, new Vector2Double(10, 10));
//		props.put(FahrradVerleihStation.FAHRRADANZAHL, new Integer(0));
//		grid.createSpaceObject(DispositionsAgent.DISPOSITIONSAGENT, props, null);
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

	@Override
	public void execute(IClockService arg0, IEnvironmentSpace arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown(IEnvironmentSpace arg0) {
		// TODO Auto-generated method stub

	}

}
