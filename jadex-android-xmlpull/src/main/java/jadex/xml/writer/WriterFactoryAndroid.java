package jadex.xml.writer;


/**
 * Factory implementation for Android.
 */
public class WriterFactoryAndroid extends XMLWriterFactory
{

	@Override
	public AWriter createWriter()
	{
		return new PullParserWriter();
	}

	@Override
	public AWriter createWriter(boolean genIds)
	{
		return new PullParserWriter(genIds);
	}

}
