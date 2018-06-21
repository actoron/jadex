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
	public AWriter createWriter(boolean genids)
	{
		return createWriter(genids, true);
	}

	@Override
	public AWriter createWriter(boolean genids, boolean indent)
	{
		return new PullParserWriter(genids, indent);
	}
	
	@Override
	public AWriter createWriter(boolean genids, boolean indent, boolean newline)
	{
		// Todo: support disabling generation of newline characters.
		return new PullParserWriter(genids, indent);		
	}
}
