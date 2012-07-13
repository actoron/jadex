package sodekovs.old.bikesharing.datenkonvertierung;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sodekovs.bikesharing.simulation.StartSimulationProzess;
import sodekovs.old.bikesharing.fahrrad.FahrradVerleihStationen;
import sodekovs.old.bikesharing.verkehrsteilnehmer.FahreRad;
import sodekovs.old.bikesharing.zeit.Zeitverwaltung;


/**
 * Konvertiert die Daten von StadtRad zu einer Fahrtendatei, die das selbe Format hat wie die Fahrtendatei der Simulation
 * @author dagere
 *
 */
public class StadtRadFahrtenKonverter
{
	
	public static void initialisiereRadStationen()
	{
		try {
			Zeitverwaltung.createInstance(0);
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db;
			db = dbf.newDocumentBuilder();
			Document doc = db.parse("Stadtrad.xml");
			
			NodeList nl = doc.getElementsByTagName("fahrradverleihstationen");
			Node stationsVaterNode = nl.item(0);
			
			StartSimulationProzess.erstellFahrradVerleihStationen(stationsVaterNode);
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
		
		
	}
	
	public static void main(String args[]) throws IOException, XMLStreamException, ParserConfigurationException, SAXException
	{
		String eingabedatei = "StadtRAD_Daten.xml";
		String ausgabedatei = "realeFahrten_std.csv";
		
		if ( args.length > 0 && args[0] != null )
		{
			System.out.println("Eingabedatei: " + args[0]);
			eingabedatei = args[0];
		}
		
		if ( args.length > 1 && args[1] != null )
		{
			System.out.println("Ausgabedatei: " + args[1]);
			ausgabedatei = args[1];
		}
		
		initialisiereRadStationen();
		
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader evRd = inputFactory.createXMLEventReader(new StreamSource( eingabedatei ));
		int fahrten = 0;
		while (evRd.hasNext())
		{
			XMLEvent xmle = evRd.nextEvent();

//			System.out.println("XMLE: " + xmle);
			if (xmle.isStartElement() && !xmle.isEndElement())
			{
				StartElement se = xmle.asStartElement();
//				System.out.println("Name: " + se.getName().getLocalPart() );
				if ( se.getName().getLocalPart().equals("TR") )
				{
					XMLEvent stationsEvent = evRd.nextEvent();
					stationsEvent = evRd.nextEvent();
					stationsEvent = evRd.nextEvent();
					String fahrtId = stationsEvent.asCharacters().getData();
					for ( int i = 0; i < 3; i++ )
					{
						XMLEvent e = evRd.nextEvent(); 
//						System.out.println("Start: " + e);
					}
					XMLEvent startEvent = evRd.nextEvent();
					String start = startEvent.asCharacters().getData();
					
					for ( int i = 0; i < 3; i++ )
					{
						XMLEvent e = evRd.nextEvent(); 
					}
					XMLEvent endeEvent = evRd.nextEvent();
					String ende = endeEvent.asCharacters().getData();

					for ( int i = 0; i < 6; i++ )
					{
						XMLEvent e = evRd.nextEvent(); 
					}
					
					IVector2 startpos = liesPosAus(evRd);
					
					for ( int i = 0; i < 4; i++ )
					{
						XMLEvent e = evRd.nextEvent(); 
					}
					
					IVector2 endepos = liesPosAus(evRd);
					
					if ( startpos != null && endepos != null )
					{
						System.out.println("FahrtID: " + fahrtId + " Start: " + start + " Ende: " + ende + " Start: " + startpos + " endepos: " + endepos);
						
						String startStation = FahrradVerleihStationen.gibInstanz().gibNaechsteFreieStation( startpos ).gibName();
						String endStation = FahrradVerleihStationen.gibInstanz().gibNaechsteFreieStation( endepos ).gibName();
						
						FahreRad.speichereFahrt(ausgabedatei, fahrtId, start.substring( start.indexOf(" ")), startStation, endStation);
						
						fahrten++;
					}
//					
				}
			}
		}
		System.out.println("Fahrten: " + fahrten);
	}
	
	private static IVector2 liesPosAus( XMLEventReader evRd ) throws XMLStreamException
	{
		XMLEvent e = evRd.nextEvent(); //Übergeht öffnendes TD-Event
		
		XMLEvent lonStartEvent = evRd.nextEvent();
		
//		System.out.println("LonStart: " + lonStartEvent);
		
		String lonStart = lonStartEvent.asCharacters().getData();
		
		for ( int i = 0; i < 3; i++ )
		{
			e = evRd.nextEvent(); 
		}
		XMLEvent latStartEvent = evRd.nextEvent();
		String latStart = latStartEvent.asCharacters().getData();
		
		e = evRd.nextEvent(); //Übergeht schließendes TD-Event
		e = evRd.nextEvent(); //Übergeht Zeilenumbruch dannach
		
		if ( lonStart.equals("NULL") || latStart.equals("NULL") )
		{
			return null;
		}
		
		double lon = DatenZuSchnellbahn.rechneLonInX( DatenZuStadtrad.gibKoordinate( lonStart ) );
		double lat = DatenZuSchnellbahn.rechneLatInY( DatenZuStadtrad.gibKoordinate( latStart ) );
		
		Vector2Double ret = new Vector2Double( lon, lat );
		return ret;
	}
}
