package jadex.xml.stax;

import javax.xml.stream.Location;
import javax.xml.stream.XMLReporter;
import javax.xml.stream.XMLStreamException;

public class JadexXMLReporterWrapper implements XMLReporter
{
	private jadex.xml.stax.XMLReporter reporter;

	public JadexXMLReporterWrapper(jadex.xml.stax.XMLReporter reporter)
	{
		this.reporter = reporter;
	}

	public void report(String message, String errorType, Object relatedInformation, Location location) throws XMLStreamException
	{
		try
		{
			reporter.report(message, errorType, relatedInformation, StaxLocationWrapper.fromLocation(location));
		} catch (Exception e)
		{
			throw new XMLStreamException(e);
		}
	}
	
	public static XMLReporter fromXMLReporter(jadex.xml.stax.XMLReporter reporter) {
		return new JadexXMLReporterWrapper(reporter);
	}
}
