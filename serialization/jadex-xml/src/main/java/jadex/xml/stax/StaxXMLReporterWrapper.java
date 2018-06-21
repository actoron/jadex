package jadex.xml.stax;

import javax.xml.stream.XMLStreamException;

/**
 * Wraps a javax.xml.stream.XMLReporter Object to provide the
 * jadex.xml.stax.XMLReporter API.
 */
public class StaxXMLReporterWrapper implements jadex.xml.stax.XMLReporter
{
	// -------- attributes --------
	/** Holds the wrapped object */
	private javax.xml.stream.XMLReporter reporter;

	// -------- constructors --------
	/**
	 * Constructor.
	 * 
	 * @param reporter
	 *            The Reporter object to be wrapped.
	 */
	public StaxXMLReporterWrapper(javax.xml.stream.XMLReporter reporter)
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
	public void report(String message, String errorType, Object relatedInformation, jadex.xml.stax.ILocation location) throws Exception
	{
		reporter.report(message, errorType, relatedInformation, JadexLocationWrapper.fromLocation(location));
	}

	/**
	 * Static method to wrap an XMLReporter
	 * 
	 * @param reporter
	 * @return the wrapped XMLReporter
	 */
	public static jadex.xml.stax.XMLReporter fromXMLReporter(javax.xml.stream.XMLReporter reporter)
	{
		return new StaxXMLReporterWrapper(reporter);
	}

}
