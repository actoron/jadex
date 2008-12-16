package jadex.rules.state.io.xml;

import jadex.commons.collection.MultiCollection;
import jadex.rules.state.IOAVState;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 *  Class for reading an XML file into an OAV state.
 */
public class Reader
{
	//-------- attributes --------
	
	/** The parser factory. */
	protected SAXParserFactory	factory;
	
	//-------- constructors --------
	
	/**
	 *  Create a new XML reader.
	 */
	public Reader()
	{
		factory	= SAXParserFactory.newInstance();
		factory.setNamespaceAware(false);
	}
	
	//-------- methods --------
	
	/**
	 *  Read an XML from a given input stream into
	 *  the given state using the given meta model.
	 *  @param in	The XML input stream.
	 *  @param state	The OAV state.
	 *  @param xmlmapping	The XML -> meta model mapping.
	 *  @param report	An empty check report for adding errors during loading.
	 *  @return The object id of the root object, created from the xml.
	 *  @throws IOException 
	 */
	public Object	read(InputStream in, IOAVState state, IOAVXMLMapping xmlmapping, MultiCollection report) throws IOException
	{
		try
		{
			OAVContentHandler	handler	= new OAVContentHandler(state, xmlmapping, report);
			XMLReader	xmlreader	= factory.newSAXParser().getXMLReader();
			xmlreader.setContentHandler(handler);
			xmlreader.parse(new InputSource(in));
			handler.performSecondPass();
			return handler.getRoot();
		}
		catch(SAXException e)
		{
//			throw new IOException(e); // Java 6
			throw new IOException(e.getMessage());
		}
		catch(ParserConfigurationException e)
		{
//			throw new IOException(e); // Java 6
			throw new IOException(e.getMessage());
		}
	}
}
