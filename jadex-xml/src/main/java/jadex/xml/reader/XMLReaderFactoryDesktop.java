package jadex.xml.reader;

import javax.xml.stream.XMLReporter;

public class XMLReaderFactoryDesktop extends XMLReaderFactory
{
	public XMLReaderFactoryDesktop() {
		super();
	}

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
		return new Reader(bulklink, validate, reporter);
	}

	@Override
	public AReader createReader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		return new Reader(bulklink, validate, coalescing, reporter);
	}

}
