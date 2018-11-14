package jadex.xml.stax;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;

/**
 * Wraps a jadex.xml.stax.XMLReporter Object to provide the
 * javax.xml.stream.XMLReporter API.
 */
public class JadexXMLReporterWrapper implements javax.xml.stream.XMLReporter
{
	// -------- attributes --------

	private jadex.xml.stax.XMLReporter reporter;

	// -------- constructors --------

	/**
	 * Constructor
	 * 
	 * @param reporter
	 *            The reporter to be wrapped.
	 */
	public JadexXMLReporterWrapper(jadex.xml.stax.XMLReporter reporter)
	{
		this.reporter = reporter;
	}

	// -------- methods --------
	/**
	 * Report the desired message in an application specific format. Only
	 * warnings and non-fatal errors should be reported through this interface.
	 * Fatal errors should be thrown as XMLStreamException.
	 * 
	 * @param message
	 *            the error message
	 * @param errorType
	 *            an implementation defined error type
	 * @param relatedInformation
	 *            information related to the error, if available
	 * @param location
	 *            the location of the error, if available
	 * @throws XMLStreamException
	 */
	public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException
	{
		try
		{
			reporter.report(message, errorType, relatedInformation, StaxLocationWrapper.fromLocation(location));
		} 
		catch(Exception e)
		{
			throw new XMLStreamException(e);
		}
	}

	/**
	 * Wraps a reporter object.
	 * 
	 * @param reporter
	 * @return wrapped XMLReporter
	 */
	public static javax.xml.stream.XMLReporter fromXMLReporter(jadex.xml.stax.XMLReporter reporter)
	{
		return reporter==null? null: new JadexXMLReporterWrapper(reporter);
	}
}
