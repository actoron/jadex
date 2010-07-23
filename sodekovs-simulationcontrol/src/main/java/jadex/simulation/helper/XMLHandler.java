package jadex.simulation.helper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public class XMLHandler {

	/**
	 * Parse and get object from XML-File
	 * 
	 * @param filename
	 * @param classname
	 * @return
	 */
	public static Object parseXMLFromXMLFile(String filename, Class classname) {
		Object obj;
		try {
			// configure input:
			JAXBContext ctx = JAXBContext.newInstance(classname);
			Unmarshaller u = ctx.createUnmarshaller();

			// read and return:
			obj = u.unmarshal(new FileInputStream(filename));
			// System.out.println("res of XML: ") ;
			return obj;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Parse and get object from serialized XML-File, i.e. XML-File represented
	 * as String.
	 * 
	 * @param serializedString
	 * @param classname
	 * @return
	 */
	public static Object parseXMLFromString(String serializedXMLString, Class classname) {
		Object obj;

		try {
			ByteArrayInputStream input = new ByteArrayInputStream(serializedXMLString.getBytes());

			// configure input:
			JAXBContext ctx = JAXBContext.newInstance(classname);
			Unmarshaller u = ctx.createUnmarshaller();

			// read and return:
			obj = u.unmarshal(input);
			return obj;
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Writes object to an XML-File.
	 * 
	 * @param obj
	 * @param filename
	 * @param classname
	 */
	public static void writeXMLToFile(Object obj, String filename, Class classname) {
		// Write
		Writer w = null;
		try {
			JAXBContext context = JAXBContext.newInstance(classname);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			// m.marshal(output, System.out);
			m.marshal(obj, System.out);

			w = new FileWriter(filename);
			m.marshal(obj, w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				w.close();
			} catch (Exception e) {
			}
		}
	}

	/**
	 * Writes object to an String.
	 * @param obj
	 * @param classname
	 * @return
	 */
	public static String writeXMLToString(Object obj, Class classname) {

		ByteArrayOutputStream sos = new ByteArrayOutputStream();

		try {
			JAXBContext context = JAXBContext.newInstance(classname);
			Marshaller m = context.createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			m.marshal(obj, sos);
			
			return sos.toString();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
