package sodekovs.old.bikesharing.datenkonvertierung;

import jadex.extension.envsupport.math.Vector2Double;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.stream.StreamSource;

/**
 * Wandelt die gesammelten Ausgabedaten aus OSM in Schnellbahndaten um
 * (wobei nur Stationspositionen und deren Typ, nicht die Linien bestimmt werden)
 * @author dagere
 *
 */
public class DatenZuSchnellbahn
{

	private static double nordgrenze = 53.7;
	private static double suedgrenze = 53.4;
	private static double westgrenze = 9.6;
	private static double ostgrenze = 10.5;

	public static void main(String args[]) throws IOException, XMLStreamException
	{
		class Station
		{
			String name;
			Vector2Double position;
			String typ;

			public Station()
			{
			}

			public boolean equals(Object o)
			{
				if (o instanceof Station)
				{
					Station s = (Station) o;
					if (s.name.equals(name))
					{
						if ((typ.equals("S") && s.typ.equals("U")) || (typ.equals("U") && s.typ.equals("S")))
						{
							typ = "SU";
						}
						return true;
					}
					else
					{
						return false;
					}
				}
				else
				{
					return false;
				}
			}
		}

		Set<Station> stationen = new HashSet<Station>();

		FileWriter ausgabeStrom = new FileWriter("Schnellbahn.xml");
		BufferedWriter ausgabe = new BufferedWriter(ausgabeStrom);
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader evRd = inputFactory.createXMLEventReader(new StreamSource("ausgabe3.xml"));

		while (evRd.hasNext())
		{
			XMLEvent xmle = evRd.nextEvent();

			// System.out.println("XMLE: " + xmle);
			if (xmle.isStartElement() && !xmle.isEndElement())
			{

				Attribute lon = xmle.asStartElement().getAttributeByName(QName.valueOf("lon"));
				Attribute lat = xmle.asStartElement().getAttributeByName(QName.valueOf("lat"));
				Attribute id = xmle.asStartElement().getAttributeByName(QName.valueOf("id"));

				if (lon != null && lat != null)
				{
					Double lonVal = new Double(lon.getValue());

					Double latVal = new Double(lat.getValue());

					if (lonVal > westgrenze && lonVal < ostgrenze && latVal > suedgrenze && latVal < nordgrenze)
					{
						// System.out.println("Im Bereich");
						// System.out.println("Name: " +
						// xmle.asStartElement().getName() + " Lon: " + lon +
						// " Lat" + lat);

						XMLEvent zwischen = evRd.nextEvent();
						Map<String, String> attribute = new HashMap<String, String>();

						while (!zwischen.isEndElement())
						{
							if (zwischen.isStartElement())
							{
								String k = zwischen.asStartElement().getAttributeByName(QName.valueOf("k")).getValue();
								String v = zwischen.asStartElement().getAttributeByName(QName.valueOf("v")).getValue();

								// System.out.println("Kind!" +
								// zwischen.asStartElement().getName() + " " + k
								// + " " + v );

								attribute.put(k, v);
							}

							zwischen = evRd.nextEvent();

							if (zwischen.isEndElement())
							{
								if (zwischen.asEndElement().getName().toString().equals("tag"))
								{
									zwischen = evRd.nextEvent();
								}
							}
						}

						if (!attribute.isEmpty() && attribute.containsKey("name"))
						{

							double x = rechneLonInX( lonVal );
							double y = rechneLatInY( latVal );
//							double y = 100 - ( (latVal - 53.45) * 500 );
							
							if ( x < 0 || y < 0 ||  x > 100 || y > 100 ) //Könnte man auch mit besserer Ost/..-Grenze abfangen, müsste man aber rumrechnen
							{
								System.out.println("Außerhalb des Bereichs: " + attribute.get("name"));
							}
							else
							{
								if (attribute.get("name").matches("[SU] [A-Za-z. ]*"))
								{
									Station s = new Station();
									
									if ( attribute.get("name").matches("[SU] [SU] [A-Za-z. ]*") ) //Für den Fall, dass eine Station das Format U S Bla hat
									{
										s.name = attribute.get("name").substring(4);
									}
									else
									{
										s.name = attribute.get("name").substring(2).replace(" ", "");
									}
									s.name = pruefName( s.name );
									s.typ = attribute.get("name").substring(0, 1);
									s.position = new Vector2Double(x, y);
									stationen.add(s);
									
								}
								else if (attribute.containsKey("railway") && attribute.containsValue("station"))
								{

									Station s = new Station();
									s.name = attribute.get("name").matches("[S] [A-Za-z. ]*") ? attribute.get("name")
											.substring(2).replace(" ", "") : attribute.get("name").replace(" ", "");
									s.name = pruefName( s.name );

									s.typ = "S";
									s.position = new Vector2Double(x, y);
									stationen.add(s);

								}

							}
						}
							}


							
				}
			}
		}

		Set<String> geschrieben = new HashSet<String>();

		ausgabe.write("<stationen>\n");
		for (Station s : stationen)
		{
			// System.out.println("Station: " + s.name + " " + s.typ + " " +
			// s.position);
			if (!geschrieben.contains(s.name))
			{
				ausgabe.write("<station name='" + s.name + "' x='" + ((double)((int) (s.position.getXAsDouble() * 100)) / 100)
						+ "' y='" + ((double)((int) (s.position.getYAsDouble() * 100)) / 100) + "'/>\n");
				geschrieben.add(s.name);
				System.out.println("Station: " + s.name + " " + s.typ + " " + s.position);
			}
		}
		ausgabe.write("</stationen>");
		System.out.println("Ende");

		ausgabe.flush();
	}
	
	private static String pruefName( String namePruef )
	{
		String name = namePruef;
		if (name.matches("[A-Za-zäüöß]+[(][A-Za-z/äüöß]*[)]"))
		{
//			System.out.println("Name: " + s.name);
			name = name.substring(0, name.indexOf( '(' ) );
		}
		
		if ( name.matches("Hamburg-[A-Za-zäüöß]+") )
		{
			name = name.substring( new String("Hamburg-").length() );
		}
		
//		name = name.replace("-", "");
		
		return name;
	}
	
	public static double rechneLonInX( double lonVal )
	{
		return (lonVal - 9.8) * 200;
	}
	
	public static double rechneLatInY( double latVal )
	{
		return 100 - ( (latVal - 53.45) * 500 );
	}
}
