package jadex.xml.reader;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import jadex.xml.SXML;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.XMLReporter;

public class PullParserReader extends AReader
{

	private XmlPullParserFactory factory;

	private boolean bulklink;

	public PullParserReader()
	{
		this(false, null, false);
	}

	public PullParserReader(boolean validate, XMLReporter reporter, boolean bulklink)
	{
		this(validate, false, reporter, bulklink);
	}

	public PullParserReader(boolean validate, boolean coalescing, XMLReporter reporter, boolean bulklink)
	{
		super(bulklink, reporter);
		try
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(validate);
			this.bulklink = bulklink;
		} catch (XmlPullParserException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public Object read(TypeInfoPathManager tipmanager, IObjectReaderHandler handler, InputStream input, ClassLoader classloader,
			Object callcontext) throws Exception
	{
		return read(tipmanager, handler, new InputStreamReader(input, SXML.DEFAULT_ENCODING), classloader, callcontext);
	}

	protected IXMLReader createXMLReader(InputStream input) {
		try {
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(input, Xml.Encoding.UTF_8.name());
			return new PullParserWrapper(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected IXMLReader createXMLReader(Reader input) {
		try {
			XmlPullParser parser = factory.newPullParser();
			parser.setInput(input);
			return new PullParserWrapper(parser);
		} catch (XmlPullParserException e) {
			e.printStackTrace();
			return null;
		}
	}

}
