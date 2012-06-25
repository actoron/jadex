package jadex.xml.reader;

import jadex.xml.stax.JadexXMLReporterWrapper;
import jadex.xml.stax.StaxXMLReporterWrapper;
import jadex.xml.stax.XMLReporter;


public class XMLReaderFactoryDesktop extends XMLReaderFactory
{
	@Override
	public AReader createReader()
	{
		return new Reader();
	}

	@Override
	public AReader createReader(boolean bulklink)
	{
		return new Reader(bulklink);
	}

	@Override
	public AReader createReader(boolean bulklink, boolean validate, XMLReporter reporter)
	{
		return new Reader(bulklink, validate, JadexXMLReporterWrapper.fromXMLReporter(reporter));
	}

	@Override
	public AReader createReader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		return new Reader(bulklink, validate, coalescing, JadexXMLReporterWrapper.fromXMLReporter(reporter));
	}

}
