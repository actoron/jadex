package jadex.xml.stax;

import javax.xml.stream.XMLReporter;

public class StaxXMLReporterWrapper implements jadex.xml.stax.XMLReporter
{

	private XMLReporter reporter;

	public StaxXMLReporterWrapper(XMLReporter reporter)
	{
		this.reporter = reporter;
	}

	public static jadex.xml.stax.XMLReporter fromXMLReporter(XMLReporter reporter)
	{
		return new StaxXMLReporterWrapper(reporter);
	}

	public void report(String message, String errorType, Object relatedInformation, jadex.xml.stax.ILocation location) throws Exception
	{
//		try
//		{
			reporter.report(message, errorType, relatedInformation, JadexLocationWrapper.fromLocation(location));
//		} catch (Exception e)
//		{
//			throw new XMLStreamException(e);
//		}
	}

}
