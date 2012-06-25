package jadex.xml.reader;

import jadex.xml.stax.XMLReporter;

/**
 * Factory implementation for Android.
 */
public class XMLReaderFactoryAndroid extends XMLReaderFactory
{

	private boolean bulklink;

	@Override
	public AReader createReader()
	{
		return new PullParserReader();
	}

	@Override
	public AReader createReader(boolean bulklink)
	{
		return createReader(bulklink, false, null);
	}

	@Override
	public AReader createReader(boolean bulklink, boolean validate, XMLReporter reporter)
	{
		return createReader(bulklink, validate, false, reporter);
	}

	@Override
	public AReader createReader(boolean bulklink, boolean validate, boolean coalescing, XMLReporter reporter)
	{
		this.bulklink = bulklink;
		return new PullParserReader(validate, coalescing, reporter);
	}

}
