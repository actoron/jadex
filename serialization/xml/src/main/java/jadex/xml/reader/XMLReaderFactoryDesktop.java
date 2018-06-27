package jadex.xml.reader;

import jadex.xml.stax.JadexXMLReporterWrapper;
import jadex.xml.stax.XMLReporter;

/**
 * Factory implementation for the Java SE Environment.
 * Uses {@link Reader}.
 */
public class XMLReaderFactoryDesktop extends XMLReaderFactory
{
	//---------- methods ------------
	/**
	 * Creates a new default XML Reader.
	 * 
	 * @return reader
	 */
	public AReader createReader()
	{
		return new Reader();
	}

	/**
	 * Creates a new XML Reader.
	 */
	public AReader createReader(boolean bulklink)
	{
		return new Reader(bulklink);
	}

	/**
	 * Creates a new XML Reader.
	 */
	public AReader createReader(boolean bulklink, boolean validate, XMLReporter reporter)
	{
		return new Reader(bulklink, validate, JadexXMLReporterWrapper.fromXMLReporter(reporter));
	}

	/**
	 * Creates a new XML Reader.
	 */
	public AReader createReader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		return new Reader(bulklink, validate, coalescing, JadexXMLReporterWrapper.fromXMLReporter(reporter));
	}

}
