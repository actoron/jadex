package jadex.xml.writer;


/**
 * Factory implementation for Android.
 */
public class XMLWriterFactoryAndroid extends XMLWriterFactory
{

	@Override
	public AWriter createWriter()
	{
		return createWriter(true);
	}

	@Override
	public AWriter createWriter(boolean genIds)
	{
		return createWriter(genIds, true);
	}

	@Override
	public AWriter createWriter(boolean genIds, boolean indent)
	{
		return new PullParserWriter(genIds, indent);
	}

}
