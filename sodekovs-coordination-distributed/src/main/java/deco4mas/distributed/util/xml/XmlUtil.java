package deco4mas.distributed.util.xml;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

/**
 * Utility functions to facilitate the processing of XML files via the JAXB framework.
 * 
 * @author Jan Sudeikat
 * 
 */
public class XmlUtil {

	// ----------methods----------

	/**
	 * Save an Object as an XML file. In-line (JAXB) annotations will be considered...
	 * 
	 * @param obj
	 *            what to save
	 * @param output_name
	 *            where to save
	 * @throws JAXBException
	 *             may happen
	 * @throws FileNotFoundException
	 *             may happen
	 */
	public static void saveAsXML(Object obj, String output_name) throws JAXBException, FileNotFoundException {

		// setup the context of the classes to serialize:
		JAXBContext ctx = JAXBContext.newInstance(obj.getClass());

		// configure the output:
		Marshaller m = ctx.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);

		// output:
		m.marshal(obj, new FileOutputStream(output_name));
	}

	/**
	 * Fetch the XML description (String) of an Object. In-line (JAXB) annotations will be considered...
	 * 
	 * @param obj
	 *            what to save
	 * @param outpout_name
	 *            where to save
	 * @throws JAXBException
	 *             may happen
	 * @throws FileNotFoundException
	 *             may happen
	 */
	public static String retrieveXML(Object obj) throws JAXBException {

		// setup the context of the classes to serialize:
		JAXBContext ctx = JAXBContext.newInstance(obj.getClass());

		// configure the output:
		Marshaller m = ctx.createMarshaller();
		m.setProperty("jaxb.formatted.output", true);

		// output:
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		m.marshal(obj, baos);
		return baos.toString();
	}

	/**
	 * Read objects from XML files. Requires the contained class and the file to read from.
	 * 
	 * @param cl
	 *            The class to be extracted from the file
	 * @param file_name
	 *            The file name.
	 * @return An instance of cl
	 * @throws JAXBException
	 *             may happen
	 * @throws FileNotFoundException
	 *             may happen
	 */
	public static Object retrieveFromXML(Class<?> cl, File file) throws JAXBException, FileNotFoundException {

		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(cl);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return u.unmarshal(new FileInputStream(file));
	}

	/**
	 * Read objects from XML files. Requires the contained class and the file to read from.
	 * 
	 * @param cl
	 *            The class to be extracted from the file
	 * @param file_name
	 *            The file name.
	 * @return An instance of cl
	 * @throws JAXBException
	 *             may happen
	 * @throws FileNotFoundException
	 *             may happen
	 */
	public static Object retrieveFromXML(Class<?> cl, String file_name) throws JAXBException, FileNotFoundException {

		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(cl);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return u.unmarshal(new FileInputStream(file_name));
	}

	/**
	 * Read objects from XML files. Requires the contained class and the file to read from.
	 * 
	 * @param cl
	 *            The class to be extracted from the file
	 * @param file_name
	 *            The file name.
	 * @return An instance of cl
	 * @throws JAXBException
	 *             may happen
	 * @throws FileNotFoundException
	 *             may happen
	 */
	@SuppressWarnings("unchecked")
	public static <T> T retrieveFromXML(String file_name, T cl) throws JAXBException, FileNotFoundException {

		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(cl.getClass());
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return (T) u.unmarshal(new FileInputStream(file_name));
	}

	/**
	 * Read objects from XML string. Requires the the contained class and the xml content.
	 * 
	 * @param cl
	 *            The class to be extracted from the string
	 * @param file_name
	 *            The string content (in xml format)
	 * @return An instance of cl
	 * @throws JAXBException
	 *             May happen
	 * @throws FileNotFoundException
	 *             May happen
	 */
	public static Object retrieveFromXMLContent(Class<?> cl, String content) throws JAXBException {

		// configure input:
		JAXBContext ctx = JAXBContext.newInstance(cl);
		Unmarshaller u = ctx.createUnmarshaller();

		// read and return:
		return u.unmarshal(new StringReader(content));
	}

	/**
	 * Generate a XML-Schema definition for a (possibly) JAXB-annotated Java class.
	 * 
	 * @param cl
	 *            The type to be "schematized"
	 * @throws JAXBException
	 *             may happen
	 * @throws IOException
	 *             may happen
	 */
	public static void generateSchema(Class<?> cl) throws JAXBException, IOException {

		// configure input:
		JAXBContext context = JAXBContext.newInstance(cl);

		// use delegate the file generation:
		context.generateSchema(new MySchemaOutputResolver());
	}

}
