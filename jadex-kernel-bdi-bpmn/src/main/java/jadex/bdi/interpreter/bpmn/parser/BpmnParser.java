package jadex.bdi.interpreter.bpmn.parser;

import jadex.bdi.interpreter.bpmn.parser.impl.daimler.BpmnPlanParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

//import aem.compiler.parser.AEM_Compiler;

/**
 * Abstract class to select and create the appropriate parser 
 * for provided content. 
 * <p>
 * Partial based on class provided by Daimler
 * <p>
 * @author claas altschaffel
 * 
 */
public abstract class BpmnParser extends DefaultHandler
{
	
	// TODO: change if/else statements to map based implementation
	// Map: net-version -> parser class
	
	// ---- constants ----

	/** private constant to indicate a content validation failure */
	private static final int VALIDATION_FAILED = -1;
	
	/** constant to indicate BPMN-Plan content */
	public static final int VALID_BPMN_v1_PLAN = 0;
	
	///** constant to indicate goal hierarchy content */
	//public static final int HIERARCHY_FILE = 1;
	
	/** DTD for BPMN plans */
	public static final URL bpmnPlanDTD = BpmnParser.class.getResource("/jadex/bdi/interpreter/bpmn/model/bpmn_plan.xsd");
	
	// ---- abstract methods ----
	
	/**
	 * Parse the provided content / file with parser instance.
	 * 
	 * @return the parse result as Object
	 */
	public abstract Object parseFile(); 
	
	// ---- methods ----
	
	/**
	 * Validate and create parser instance for url.
	 * 
	 * @param url to create parser for
	 * @return parser instance for url content
	 * @throws BpmnParserException if the validation failed
	 */
	public static BpmnParser getInstance(URL url) throws BpmnParserException
	{
		int validationResult = validateURL(url);
		switch (validationResult)
		{
			case VALID_BPMN_v1_PLAN: 
				return new BpmnPlanParser(url);
				
			default: 
				throw new BpmnParserException("Unable to find a suitable parser for file " + url);
		}
	}
	
	/**
	 * Validate and create parser instance for url.
	 * 
	 * @param url to create parser for
	 * @return parser instance for url content
	 * @throws BpmnParserException if the validation failed
	 */
	public static BpmnParser getInstance(InputStream is) throws BpmnParserException
	{
		try {
			// HACK, validation closes the stream
			byte[] xmlContent = new byte[is.available()];
			is.read(xmlContent, 0, is.available());
			is.close();
			
			ByteArrayInputStream validationIS = new ByteArrayInputStream(xmlContent);
			ByteArrayInputStream parseIS = new ByteArrayInputStream(xmlContent);
			
			int validationResult = validateXml(validationIS);
			switch (validationResult)
			{
				case VALID_BPMN_v1_PLAN: 
					return new BpmnPlanParser(parseIS);
					
				default: 
					throw new BpmnParserException("Unable to find a suitable parser for file from InputStream: " + is);
			}
		}
		catch (IOException ioe) 
		{
			throw new BpmnParserException("Caught IO Exception during ");
		}
		// finally always close the stream
		finally 
		{
			try 
			{
				is.close();
			}
			catch(Exception e) {}
		}
	}
	
	// ---- helper ----
	
	/**
	 * Validate a url / file against known DTDs.
	 * 
	 * @return int to indicate file type
	 */
	private static int validateURL(URL url) throws BpmnParserException
	{
		StringBuffer errors = new StringBuffer();
		
		// validate against a bpmn plan dtd
		try
		{
			if (XmlValidator.validateURLByDtd(url, bpmnPlanDTD))
			{
				return VALID_BPMN_v1_PLAN;
			}
				
		}
		catch (SAXException err)
		{
			errors.append("Exception for moduleDTD (" + bpmnPlanDTD.toString() + "):\n");
			errors.append(err.getMessage()+"\n");
			//err.printStackTrace();
		}
		
		// maybe try more dtd ...

		System.err.println("Unable to validate file '"+url+"' - No DTD matched");
		System.err.println(errors.toString());
		return VALIDATION_FAILED;
	}
	
	/**
	 * Validate the content from an InputStream against known DTDs.
	 * 
	 * @return int to indicate file type
	 */
	private static int validateXml(InputStream is) throws BpmnParserException
	{
		StringBuffer errors = new StringBuffer();
		
		// validate against a bpmn plan dtd
		try
		{
			if (XmlValidator.validateXmlByDtd(is, bpmnPlanDTD))
			{
				return VALID_BPMN_v1_PLAN;
			}
				
		}
		catch (SAXException err)
		{
			errors.append("Exception for moduleDTD (" + bpmnPlanDTD.toString() + "):\n");
			errors.append(err.getMessage()+"\n");
			//err.printStackTrace();
		}
		
		// maybe try more dtd ...

		System.err.println("Unable to validate file from InputStream '"+is+"' - No DTD matched");
		System.err.println(errors.toString());
		return VALIDATION_FAILED;
	}
}
