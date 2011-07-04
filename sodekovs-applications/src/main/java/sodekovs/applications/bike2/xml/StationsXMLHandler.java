package sodekovs.applications.bike2.xml;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Helper class with static methods for retrieving objects from XML files and vice versa.
 * 
 * @author Thomas Preisler
 */
public class StationsXMLHandler {

	/**
	 * Main method with some two simple test imports from XML.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("London:");
			Stations london = retrieveFromXML("test/london.xml");
			System.out.println("\t" + london + "\n");

			System.out.println("Washington:");
			Stations washington = retrieveFromXML("test/washington.xml");
			System.out.println("\t" + washington);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves a {@link Stations} object from the given XML file.
	 * 
	 * @param fileName
	 *            the given XML file
	 * @return the imported {@link Stations}
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static Stations retrieveFromXML(String fileName) throws JAXBException, FileNotFoundException {
		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(Stations.class);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return (Stations) u.unmarshal(new FileInputStream(fileName));
	}

	/**
	 * Retrieves a {@link Stations} object from the given {@link InputStream}.
	 * 
	 * @param url
	 *            the given {@link InputStream}
	 * @return the imported {@link Stations}
	 * @throws JAXBException
	 */
	public static Stations retrieveFromXML(InputStream is) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(Stations.class);
		Unmarshaller u = ctx.createUnmarshaller();

		return (Stations) u.unmarshal(is);
	}

	/**
	 * Saves the given {@link Stations} as the given XML file name.
	 * 
	 * @param stations
	 *            the given {@link Stations}
	 * @param outpoutName
	 *            the given XML file name
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static void saveAsXML(Stations stations, String outpoutName) throws JAXBException, FileNotFoundException {
		// setup the context of the classes to serialize:
		JAXBContext ctx = JAXBContext.newInstance(Stations.class);

		// configure the output:
		Marshaller m = ctx.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);

		// output:
		m.marshal(stations, new FileOutputStream(outpoutName));
	}
}
