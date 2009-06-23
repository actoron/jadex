package jadex.bdi.interpreter.bpmn.parser;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.xml.sax.SAXException;

/**
 * <p>
 * This file is property of DaimlerCrysler.
 * </p>
 * 
 * Validator provides the possibility to validate an xmlFile with
 * a dtd-File.
 * 
 * 
 * @author cwiech8, claas altschaffel
 *
 */
public class XmlValidator {

	// TODO: c.wiech: add possibility to check with common DTD-Files 
	// as soon as their support is added to JDK
	
	/**
	 * Validates a XML-file with a given DTD. Currently only DTDs in XML-Schema
	 * are supported. (Usually .xsd)
	 * 
	 * @param xmlFile
	 * 					the XML-file to be validated
	 * @param dtd
	 * 					URL to the dtd that the XML-file should match
	 * @return 
	 * 					<code>true</code> if the validation was successful
	 * 					<code>false</code> else
	 * @throws org.xml.sax.SAXException in case the DTD couldn't be parsed 
	 * 
	 */
	public static boolean validateURLByDtd(URL xmlURL, URL dtd) throws SAXException
	{
		try
		{
			return validateXmlByDtd(xmlURL.openStream(), dtd);
		}
		catch (IOException err)
		{
			//in case the source was invalid
			System.err.println("IOException for File: " + xmlURL);
			err.printStackTrace();
			return false;
		}

	}
	
	/**
	 * Validates a XML-file with a given DTD. Currently only DTDs in XML-Schema
	 * are supported. (Usually .xsd)
	 * 
	 * @param input stream
	 * 					to read the the XML-file to be validated
	 * @param dtd
	 * 					URL to the dtd that the XML-file should match
	 * @return 
	 * 					<code>true</code> if the validation was successful
	 * 					<code>false</code> else
	 * @throws org.xml.sax.SAXException in case the DTD couldn't be parsed 
	 * 
	 */
	public static boolean validateXmlByDtd(InputStream is, URL dtd) throws SAXException
	{
		//create source from xmlFile
		StreamSource source = null;
		try
		{
			source = new StreamSource(is);
			
			// get Schemas for XML-Schema (*.xsd) 
			SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
			
			// parse dtd and get schema (might throw SAXException)
			Schema schemaSubProcess = sf.newSchema(dtd);
			Validator validator = schemaSubProcess.newValidator();
			
			// validate content
			validator.validate(source);
			return true;
		}
		catch (IOException err)
		{
			//in case the source was invalid
			System.err.println("IOException for InputStream: " + is);
			err.printStackTrace();
			return false;
		}

	}
}
