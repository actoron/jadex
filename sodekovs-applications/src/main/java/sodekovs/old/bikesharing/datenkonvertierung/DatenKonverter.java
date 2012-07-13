package sodekovs.old.bikesharing.datenkonvertierung;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

/**
 * Liest OpenStreetMaps-Daten ein und gibt die nutzbaren Daten in einem
 * neuen OSM-ähnlichen Format aus
 * @author dagere
 *
 */
public class DatenKonverter
{
	private static double nordgrenze = 53.7;
	private static double suedgrenze = 53.4;
	private static double westgrenze = 9.6;
	private static double ostgrenze = 10.5;

	public static void main(String args[]) throws IOException,	XMLStreamException
	{
		FileWriter ausgabeStrom = new FileWriter("ausgabe3.xml");
        BufferedWriter ausgabe = new BufferedWriter(ausgabeStrom);
        

		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader evRd = inputFactory.createXMLEventReader(new StreamSource("germany.osm"));

		while (evRd.hasNext())
		{
			XMLEvent xmle = evRd.nextEvent();

			// System.out.println("XMLE: " + xmle);
			if (xmle.isStartElement() && ! xmle.isEndElement() )
			{

				Attribute lon = xmle.asStartElement().getAttributeByName(QName.valueOf("lon"));
				Attribute lat = xmle.asStartElement().getAttributeByName(QName.valueOf("lat"));
				Attribute id  = xmle.asStartElement().getAttributeByName(QName.valueOf("id"));
				
				if (lon != null && lat != null)
				{
					Double lonVal = new Double(lon.getValue());

					Double latVal = new Double(lat.getValue());
					
					if (lonVal > westgrenze && lonVal < ostgrenze && latVal > suedgrenze && latVal < nordgrenze)
					{
//						System.out.println("Im Bereich");
//						System.out.println("Name: " + xmle.asStartElement().getName() + " Lon: " + lon + " Lat" + lat);
						
						XMLEvent zwischen = evRd.nextEvent();
						Map<String, String> attribute = new HashMap<String, String>();
						
						while ( ! zwischen.isEndElement() )
						{
							if ( zwischen.isStartElement() )
							{
								String k = zwischen.asStartElement().getAttributeByName( QName.valueOf("k") ).getValue();
								String v = zwischen.asStartElement().getAttributeByName( QName.valueOf("v") ).getValue();
								
								if ( v.contains("'") )
								{
									v = v.replace("'", "");
								}
								
//								System.out.println("Kind!" + zwischen.asStartElement().getName() + " " + k + " " + v );
								
								attribute.put(k, v);
							}						
							
							zwischen = evRd.nextEvent();
							
							if ( zwischen.isEndElement() )
							{
								if ( zwischen.asEndElement().getName().toString().equals("tag") )
								{
									zwischen = evRd.nextEvent();
								}
							}
						}
						
						if ( !attribute.isEmpty() )
						{
//							
							
//							System.out.println("Position: " + lonVal + " | " + latVal );
//							String atts = "";
//							for ( Map.Entry<String, String> entry : attribute.entrySet() )
//							{
//								atts+=entry.getKey() + " " + entry.getValue() + " | ";
//							}
//							System.out.println("Attribute: " + atts);
							if ( (attribute.containsKey("bus") || attribute.containsKey("highway") || attribute.containsValue("bus_stop") ||
								 attribute.containsKey("railway") || attribute.containsKey("station") || attribute.containsValue("subway") ) &&
								 (!attribute.containsValue("traffic_signals") && !attribute.containsValue("motorway_junction") && !attribute.containsValue("crossing") &&
								  !attribute.containsValue("turning_circle") && !attribute.containsValue("level_crossing") ) )
							{
								ausgabe.write("<node " + id + " " + lat + " " + lon + ">");// "' lat= '" + latVal + "' lon='" + lonVal +"'>");
								for ( Map.Entry<String, String> entry : attribute.entrySet() )
								{
									ausgabe.write("<tag k = '" + entry.getKey() + "' v='" + entry.getValue() + "'/>\n");
								}
								ausgabe.write("</node>\n");
							}
							
						}
					}
				}
			}
		}
	}
}
