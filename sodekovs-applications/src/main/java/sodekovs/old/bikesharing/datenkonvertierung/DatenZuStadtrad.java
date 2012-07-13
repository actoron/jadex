package sodekovs.old.bikesharing.datenkonvertierung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

/**
 * Liest Daten im StadtRad-Exel-Format aus und generiert daraus für die
 * Simulation nutzbare Fahrradverleihstationsdaten
 * @author dagere
 *
 */
public class DatenZuStadtrad
{

	private static double nordgrenze = 53.7;
	private static double suedgrenze = 53.4;
	private static double westgrenze = 9.6;
	private static double ostgrenze = 10.5;

	static class Station
	{
		double x, y;
		String name;
		int kapazitaet, initialeRaeder;
	}
	
	public static void main(String args[]) throws IOException, XMLStreamException
	{
		
		
		List<Station> stationsliste = new LinkedList<Station>();
		
		FileWriter ausgabeStrom = new FileWriter("Stadtrad.xml");
		BufferedWriter ausgabe = new BufferedWriter(ausgabeStrom);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader evRd = inputFactory.createXMLEventReader(new StreamSource("StartstationsDaten0707.xml"));
		
		while (evRd.hasNext())
		{
			XMLEvent xmle = evRd.nextEvent();

//			System.out.println("XMLE: " + xmle);
			if (xmle.isStartElement() && !xmle.isEndElement())
			{
				StartElement se = xmle.asStartElement();
//				System.out.println("Name: " + se.getName().getLocalPart() );
				if ( se.getName().getLocalPart().equals("TD") )
				{
					XMLEvent stationsEvent = evRd.nextEvent();
					String station = stationsEvent.asCharacters().getData();
//					System.out.println("Station: " + station);
					for ( int i = 0; i < 7; i++ )
					{
						XMLEvent e = evRd.nextEvent(); 
//						System.out.println("E: " + e);
						// 5 Elemente übergehen: station zu, Zeilenumbruch, Datum auf, Datum, Datum zu, Zeilenumbruch Anzahl auf,
					}
					XMLEvent anzahlEvent = evRd.nextEvent();
					String anzahl = anzahlEvent.asCharacters().getData();
					System.out.println("Station: " + station + " Anzahl: " + anzahl);
					
					Station s = new Station();
					s.name = station;
					s.initialeRaeder = new Integer( anzahl );
					s.kapazitaet = 20;
					
					stationsliste.add( s );
				}
			}
		}
		
		evRd = inputFactory.createXMLEventReader(new StreamSource("StadtRAD_Daten.xml"));
		
		while ( evRd.hasNext() )
		{
			XMLEvent xmle = evRd.nextEvent();
//			System.out.println("HasNext: " + xmle);
			if ( xmle.isStartElement() && xmle.asStartElement().getName().toString().equals("TR") )
			{
				verwalteTR(stationsliste, xmle, evRd);
			}
		}
		
		ausgabe.write("<fahrradverleihstationen>\n");
		for ( Station s : stationsliste )
		{
			ausgabe.write("  <station name='" + s.name.replace("/", "") + "' x='" + s.x + "' y='" + s.y + "' initialeRaeder='"+s.initialeRaeder+ "' kapazitaet='"+ s.kapazitaet +"'/>\n" );
		}
		ausgabe.write("</fahrradverleihstationen>");

		ausgabe.flush();
	}
	
	private static void verwalteTR( List<Station> stationsliste, XMLEvent xmle, XMLEventReader evRd  ) throws XMLStreamException
	{
		for ( int i = 0; i < 17; i++ )
		{
			xmle = evRd.nextEvent();
			if ( xmle.isCharacters() && xmle.asCharacters().getData().equals("NULL") )
			{
				return;
			}
//			System.out.println("Bis 17: " + xmle);
		}
		xmle = evRd.nextEvent();
//		System.out.println("Event: " + xmle);
		xmle = evRd.nextEvent();
		double lon = gibKoordinate( xmle.asCharacters().getData() );
		while ( !xmle.isStartElement() )
		{
//			System.out.println("XMLE: " + xmle);
			xmle = evRd.nextEvent();
		}
		xmle = evRd.nextEvent();
		double lat = gibKoordinate( xmle.asCharacters().getData() );
		for ( int i = 0; i < 16; i++ )
		{
			xmle = evRd.nextEvent();
//			System.out.println("XMLE vor Name: " + xmle);
		}
//		System.out.println("Name: " + xmle);
		String name = xmle.asCharacters().getData();
		
		System.out.println("Name: " + name + " " + lon  + " " + lat);
		
		for ( Station s : stationsliste )
		{
			if ( s.name.equals( name ) )
			{
				s.x = DatenZuSchnellbahn.rechneLonInX( lon );
				s.y = DatenZuSchnellbahn.rechneLatInY( lat );
			}
		}
		System.out.println("Name: " + name + " " + lon  + " " + lat);
		
	}
	
	public static Double gibKoordinate( String koord )
	{
//		System.out.println("Start: " + koord);
		
		String richtigerString = koord;
		//koord.substring(0, koord.lastIndexOf(".") ) + koord.substring(koord.lastIndexOf(".")+1, koord.length() );
		
		while ( richtigerString.matches("([0-9]+\\.){2,}[0-9]+" ) )
		{
//			System.out.println("String: " + richtigerString);
			richtigerString = richtigerString.substring(0, richtigerString.lastIndexOf(".") ) + richtigerString.substring(richtigerString.lastIndexOf(".")+1, richtigerString.length() );
		}
//		System.out.println("Ret: " + richtigerString);
		return new Double( richtigerString );
	}
}
