package jadex.xml.reader;

import java.io.InputStream;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import jadex.xml.stax.StaxReaderWrapper;
import jadex.xml.stax.StaxXMLReporterWrapper;

/**
 *  Stax XML reader.
 */
public class Reader extends AReader
{
	//-------- attributes --------
	
//	/** The type info manager. */
//	protected TypeInfoPathManager tipmanager;
	
//	/** The default object reader handler (if any). */
//	protected IObjectReaderHandler defaulthandler;
	

	
	/** The xml input factory. */
	protected XMLInputFactory	factory;
	
	//-------- constructors --------

	/**
	 *  Create a new reader.
	 *  @param readerhandler The handler.
	 */
	public Reader()
	{
		this(false);
	}
	
	/**
	 *  Create a new reader.
	 */
	public Reader(boolean bulklink)
	{
		this(bulklink, false, null);
	}
	
	/**
	 *  Create a new reader.
	 */
	public Reader(boolean bulklink, boolean validate, XMLReporter reporter)
	{
		// Xerces has a stackoverflow bug when coalescing is set to true :-(
		this(bulklink, validate, false, reporter);
	}
	
	/**
	 *  Create a new reader.
	 *  @param readerhandler The handler.
	 */
	public Reader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		super(bulklink, StaxXMLReporterWrapper.fromXMLReporter(reporter));
		factory	= XMLInputFactory.newInstance();
		
		try
		{
			factory.setProperty(XMLInputFactory.IS_VALIDATING, validate ? Boolean.TRUE : Boolean.FALSE);
		}
		catch(Exception e)
		{
			// Validation not supported.
			System.err.println("Error setting validation to "+validate);
//			e.printStackTrace();
		}
		
		try
		{
			factory.setProperty(XMLInputFactory.IS_COALESCING, coalescing ? Boolean.TRUE : Boolean.FALSE);
		}
		catch(Exception e)
		{
			// Validation not supported.
			System.err.println("Error setting coalescing to "+coalescing);
//			e.printStackTrace();
		}
		
		if(reporter!=null)
		{
			factory.setProperty(XMLInputFactory.REPORTER, reporter);
		}
		else
		{
			factory.setProperty(XMLInputFactory.REPORTER, new XMLReporter()
			{
				public void report(String message, String error, Object related, Location location)	throws XMLStreamException
				{
					throw new XMLStreamException(message, location);
				}
			});			
		}
	}
	
	//-------- methods --------
	
	protected IXMLReader createXMLReader(InputStream input) {
		XMLStreamReader parser = null;
		synchronized(factory)
		{
			try {
				parser = factory.createXMLStreamReader(input);
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		return new StaxReaderWrapper(parser);
	}

	protected IXMLReader createXMLReader(java.io.Reader input) {
		XMLStreamReader parser = null;
		synchronized(factory)
		{
			try {
				parser = factory.createXMLStreamReader(input);
			} catch (XMLStreamException e) {
				e.printStackTrace();
			}
		}
		return new StaxReaderWrapper(parser);
	}

}
