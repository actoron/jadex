package jadex.bdi.interpreter.bpmn.parser.impl.daimler;

//import graphEdit.xml.UTF8AttributeImpl;

import jadex.bdi.interpreter.bpmn.model.ParsedStateMachine;
import jadex.bdi.interpreter.bpmn.parser.BpmnParser;
import jadex.bdi.interpreter.bpmn.parser.BpmnParserException;
import jadex.bdi.interpreter.bpmn.parser.BpmnPlanParseException;
import jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_UTF8AttributeImpl;
import jadex.bdi.interpreter.bpmn.parser.impl.daimler.xml.AEM_UTF8StringBufferWrapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;


/**
 * The {@link BpmnPlanParser} is a SAX Parser. It parses a 'net' file into an
 * {@link ParsedStateMachine} for execution.
 * <p>
 * Partial based on class provided by Daimler
 * @author cwiech8, claas altschaffel
 */
public class BpmnPlanParser extends BpmnParser
{
	/** The URL to the net file to parse */
	private URL fileURL;
	
	/** The input stream to read the file from */
	private InputStream fileIS;

	/** The handler to parse the content */
	private BpmnHandlerBase handler;

	/** 
	 * Create a parser for specified URL
	 * @param url of the file to parse into a StateMachine
	 */
	public BpmnPlanParser(URL url)
	{
		super();
		this.fileURL = url;
	}
	
	/** 
	 * Create a parser for provided input stream
	 * @param intut stream of the file to parse into a StateMachine
	 */
	public BpmnPlanParser(InputStream is)
	{
		super();
		this.fileIS = is;
	}

	/**
	 * Parses the 'net' file provided by the URL into an {@link ParsedStateMachine}.
	 * The StateMachine contains a list of states in the Plan and the corresponding
	 * transitions between these states.
	 */
	public /*ParsedStateMachine*/ Object parseFile()
	{
		SAXParserFactory sf = SAXParserFactory.newInstance();
		//sf.setValidating(true);
		try
		{
			if (fileIS == null) 
			{
				fileIS = fileURL.openStream();
			}
			
			SAXParser parser = sf.newSAXParser();
			InputStreamReader isr = new InputStreamReader(fileIS);
			InputSource inputSource = new InputSource(isr);
			handler = new BpmnHandlerBase();
			handler.setRootFileURL(fileURL!=null?fileURL:new URL("file:///unknown"));
			
			//System.out.println("Parsing Module " + fileURL + " now... ");
			parser.parse(inputSource, this);
		}
		catch (FileNotFoundException err)
		{
			System.out.println("Unable to find " + fileURL);
			return null;
		}
		catch (ParserConfigurationException err)
		{
			System.out.println(err.getMessage());
			err.printStackTrace();
		}
		catch (SAXException err)
		{
			System.out.println(err.getMessage());
			err.printStackTrace();
		}
		catch (IOException err)
		{
			System.out.println(err.getMessage());
			err.printStackTrace();
		}
		return new ParsedStateMachine(handler.getParsedStates(), handler.getStartStateId());
	}

	/**
	 * Overrides HandlerBase (To be called by the xmlParser).
	 *
	 * @param name
	 * @exception SAXException
	 */
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) {
		try
		{
			if (qName.equals("net"))
			{
				try {
					handler.initializeHandler(attributes.getValue("version"));
				} 
				catch (BpmnParserException e)
				{
					throw new SAXException(
							"The net file version is not supported by this version of AEM_HierarchyParser. Please upgrade", e);
				}
				// FIXME: Use utf8 ... else uft8 ... ???
				if (attributes.getValue("encoding") == null
						|| attributes.getValue("encoding")
								.equalsIgnoreCase("UTF-8"))
				{
					handler.setCharacterBuffer(new AEM_UTF8StringBufferWrapper());
				} else
				{
					System.out.println("Unknown encoding - falling back to UTF-8");
					handler.setCharacterBuffer(new AEM_UTF8StringBufferWrapper());
				}
			}
			if (handler != null) {
				AttributesImpl _attributes;
				if (handler.getCharacterBuffer() == null
						|| handler.getCharacterBuffer() instanceof AEM_UTF8StringBufferWrapper) {
					//_attributes = new UTF8AttributeImpl(attributes);
					_attributes = new AEM_UTF8AttributeImpl(attributes);
				} else {
					_attributes = new AttributesImpl(attributes);
				}
				handler.getBpmnHandler().startElement(uri, localName, qName, _attributes);
			}
		}
		catch (SAXException err)
		{
			//String sMessage = handler.getRootFileURL() + ": ";
			String sMessage = handler.getRootFileURL() + ": ";
			if (handler.getCurrentParsedState() != null && handler.getCurrentParsedState().getId() != null) sMessage += "Node: " + handler.getCurrentParsedState().getId() + ": ";
			else if (handler.getCurrentParsedTransition() != null && handler.getCurrentParsedTransition().getTargetId() != null && handler.getCurrentParsedTransition().getSourceId() != null)
				sMessage += "Edge from \"" + handler.getCurrentParsedTransition().getSourceId() + "\" to \"" + handler.getCurrentParsedTransition().getTargetId() + "\":\n";
			sMessage += err.getMessage();
			System.err.println(sMessage);
		}
	}

	/**
	 * Overrides HandlerBase (To be called by the xmlParser). At the moment only
	 * the description will be filled by this method.
	 *
	 * @param ch
	 *            A Character Array
	 * @param start
	 *            start of content in Array
	 * @param length
	 *            length of content in Array
	 */
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		if (handler != null)
			handler.getBpmnHandler().characters(ch, start, length);
	}

	/**
	 * Overrides HandlerBase (To be called by the xmlParser).
	 *
	 * @param name
	 * @exception SAXException
	 */
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		if (handler != null)
			handler.getBpmnHandler().endElement(uri, localName, qName);
	}

	/**
	 * Overrides HandlerBase (To be called by the xmlParser). The only thing
	 * done in this event is to connect the edges collected so far and add them
	 * to the Graph
	 *
	 * @exception SAXException
	 */
	public void endDocument() throws SAXException {
		
		if (handler != null && handler.getBpmnHandler() != null)
		{
			handler.getBpmnHandler().endDocument();
		}
		
		try
		{
			handler.buildStateMachine();
		}
		catch (BpmnPlanParseException err)
		{
			System.err.println("AEM_ModuleParseException: " + err.getMessage());
			System.out.println("What to do now?");
			//System.exit(1);
		}

	}

}
