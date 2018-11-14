package jadex.xml.stax;

import javax.xml.stream.XMLStreamException;

/**
 * Stax API: XMLReporter
 */
public interface XMLReporter
{
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
	public void report(String message, String errorType, Object relatedInformation, ILocation location) throws Exception;
}
