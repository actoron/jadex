package sodekovs.old.bikesharing.datenkonvertierung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

/**
 * Konvertiert Linien-Rohdaten (aus den HVV-Seitendaten) in die 
 * nutzbaren Liniendaten
 * @author dagere
 *
 */
public class BahnLinienKonverter
{

	public static void main(String args[]) throws IOException, XMLStreamException
	{
		class Station
		{
			String name;
			int abstand;
		}

		FileWriter ausgabeStrom = new FileWriter("linienFein.xml");
		BufferedWriter ausgabe = new BufferedWriter(ausgabeStrom);

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader evRd = inputFactory.createXMLEventReader(new StreamSource("linienRoh.xml"));

		Map<String, List<Station>> linien = new HashMap<String, List<Station>>();
		String aktuelleLinie = "";

		while (evRd.hasNext())
		{
			XMLEvent xmle = evRd.nextEvent();

			if (xmle.isStartElement() && xmle.asStartElement().getName().getLocalPart().equals("root"))
			{
				xmle = evRd.nextEvent();
			}
			// System.out.println("XMLE: " + xmle);
			if (xmle.isStartElement() && !xmle.isEndElement() && !xmle.isStartDocument())
			{
				XMLEvent startElement = xmle.asStartElement();

				Attribute linie = xmle.asStartElement().getAttributeByName(QName.valueOf("name"));
				System.out.println("Linie: " + linie.getValue());
				aktuelleLinie = linie.getValue();

				xmle = evRd.nextEvent();

				String daten = xmle.asCharacters().getData();
				String erste = daten.substring(daten.indexOf("Haltestellen ab") + 16, daten.indexOf("Fahrzeit") - 1);

				Station station = new Station();
				station.abstand = 0;
				station.name = erste.replace(" ", "");

				List<Station> stationsliste = new LinkedList<Station>();

				stationsliste.add(station);

				linien.put(aktuelleLinie, stationsliste);
				System.out.println("Station: " + erste);

				xmle = evRd.nextEvent();
			}

			if (xmle.isCharacters())
			{
				String daten = xmle.asCharacters().getData();
				System.out.println("Data: " + xmle.asCharacters().getData());
				if (!aktuelleLinie.equals(""))
				{
					String temp = daten.replace(" ", "");
//					temp = temp.replace("	", "");
					while (!temp.isEmpty())
					{
						String zeile;
						if (temp.contains("\n"))
						{
							zeile = temp.substring(0, temp.indexOf("\n"));
							temp = temp.substring(temp.indexOf("\n") + 1);
						}
						else
						{
							zeile = temp;
							temp = "";
						}
						
						String[] teile = zeile.split("	");
						if ( teile.length > 1 )
						{
							System.out.println("Teile: " + teile[0] + " " + teile[1] + " " + teile[2] );
							String station = teile[1];
							int abstand = new Integer( teile[2] );
							
							if ( station.matches(".*[(].*[)].*" ) )
							{
								station = station.substring(0, station.indexOf("(") ) + station.substring( station.indexOf(")") + 1 );
							}
							
							Station s = new Station();
							s.abstand = abstand;
							s.name = station.replace(" ", "");
							
							linien.get(aktuelleLinie).add( s );
						}
					}
				}
			}
		}
		
		ausgabe.write("<linien>\n");
		for ( Map.Entry<String, List<Station>> eintrag : linien.entrySet() )
		{
			System.out.println("Schreibe: " + eintrag.getKey());
			ausgabe.write("  <linie name='" + eintrag.getKey() + "' fahrzeuge='10' takt='10' start='300' ende='1400'>\n");
			
			for ( Station s : eintrag.getValue() )
			{
				ausgabe.write("    <station name='" + s.name + "' abstand='" + s.abstand + "'/>\n");
			}
			
			ausgabe.write("  </linie>\n");
		}
		ausgabe.write("</linien>");
		
		ausgabe.flush();
	}

}
