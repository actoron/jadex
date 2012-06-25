package jadex.xml.reader;

import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.XMLReporter;

import java.io.InputStream;
import java.io.Reader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PullParserReader extends AReader
{

	private XmlPullParserFactory factory;
	private XmlPullParser parser;

	public PullParserReader()
	{
		this(false, null);
	}

	public PullParserReader(boolean validate, XMLReporter reporter)
	{
		this(validate, false, reporter);
	}

	public PullParserReader(boolean validate, boolean coalescing, XMLReporter reporter)
	{
		try
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(validate);
			parser = factory.newPullParser();
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, Reader input, ClassLoader classloader, Object callcontext)
			throws Exception
	{
		int type = 0;
		while (type != XmlPullParser.END_DOCUMENT)
		{
			type = parser.next();
			switch (type)
			{
			case XmlPullParser.CDSECT:

				break;
			case XmlPullParser.COMMENT:

				break;

			case XmlPullParser.DOCDECL:

				break;
			case XmlPullParser.END_TAG:

				break;
			case XmlPullParser.ENTITY_REF:

				break;
			case XmlPullParser.IGNORABLE_WHITESPACE:

				break;
			case XmlPullParser.PROCESSING_INSTRUCTION:

				break;
			case XmlPullParser.START_DOCUMENT:

				break;
			case XmlPullParser.START_TAG:

				break;
			case XmlPullParser.TEXT:

				break;
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, InputStream input, ClassLoader classloader, Object callcontext)
			throws Exception
	{
		// TODO Auto-generated method stub
		return null;
	}

}
