package sodekovs.bikesharing.data.washington.stations;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import sodekovs.applications.bikes.datafetcher.xml.stations.Stations;

/**
 * Helper class with static methods for retrieving objects from XML files and vice versa.
 * 
 * @author Thomas Preisler
 */
public class XMLHandler {

	/**
	 * Main method with some two simple test imports from XML.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			System.out.println("London:");
			Stations london = (Stations) retrieveFromXML(Stations.class, "test/london.xml");
			System.out.println("\t" + london + "\n");

			System.out.println("Washington:");
			Stations washington = (Stations) retrieveFromXML(Stations.class, "test/washington.xml");
			System.out.println("\t" + washington);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (JAXBException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Retrieves an object from the given XML file.
	 * 
	 * @param fileName
	 *            the given XML file
	 * @return the imported object
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static Object retrieveFromXML(Class<?> clazz, String fileName) throws JAXBException, FileNotFoundException {
		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(clazz);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return u.unmarshal(new FileInputStream(fileName));
	}

	/**
	 * Retrieves an object from the given byte array.
	 * 
	 * @param url
	 *            the given byte array
	 * @return the imported object
	 * @throws JAXBException
	 */
	public static Object retrieveFromXML(Class<?> clazz, byte[] xml) throws JAXBException {
		JAXBContext ctx = JAXBContext.newInstance(clazz);
		Unmarshaller u = ctx.createUnmarshaller();

		return u.unmarshal(new ByteArrayInputStream(xml));
	}

	/**
	 * Saves the given object as the given XML file name.
	 * 
	 * @param stations
	 *            the given object
	 * @param outpoutName
	 *            the given XML file name
	 * @throws JAXBException
	 * @throws FileNotFoundException
	 */
	public static void saveAsXML(Class<?> clazz, Object obj, String outpoutName) throws JAXBException, FileNotFoundException {
		// setup the context of the classes to serialize:
		JAXBContext ctx = JAXBContext.newInstance(clazz);

		// configure the output:
		Marshaller m = ctx.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);

		// output:
		m.marshal(obj, new FileOutputStream(outpoutName));
	}
}
