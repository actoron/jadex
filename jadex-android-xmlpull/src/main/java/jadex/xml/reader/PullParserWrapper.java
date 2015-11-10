package jadex.xml.reader;

import jadex.xml.stax.ILocation;
import jadex.xml.stax.Location;
import jadex.xml.stax.QName;
import jadex.xml.stax.XmlTag;
import jadex.xml.stax.XmlUtil;
import jadex.xml.SXML;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

public class PullParserWrapper implements IXMLReader
{
	private static final Map<Integer, Integer> EVENT_TYPE_MAPPING = new HashMap<Integer, Integer>();
	static
	{
		EVENT_TYPE_MAPPING.put(XmlPullParser.START_TAG, XmlUtil.START_ELEMENT);
		EVENT_TYPE_MAPPING.put(XmlPullParser.END_TAG, XmlUtil.END_ELEMENT);

		EVENT_TYPE_MAPPING.put(XmlPullParser.TEXT, XmlUtil.CHARACTERS);
		EVENT_TYPE_MAPPING.put(XmlPullParser.CDSECT, XmlUtil.CDATA);
		EVENT_TYPE_MAPPING.put(XmlPullParser.ENTITY_REF, XmlUtil.CHARACTERS);

		EVENT_TYPE_MAPPING.put(XmlPullParser.COMMENT, XmlUtil.COMMENT);
	}
	
	/** The input stream reader. */
	protected InputStreamReader isreader;
	
	/** The parser. */
	protected XmlPullParser parser;
	
	/** Flag whether the reader has more to read. */
	protected boolean hasnext;
	
	/** Current internal event type */
	protected int inttype;
	
	/** The tag stack. */
	protected LinkedList<XmlTag> tagstack = new LinkedList<XmlTag>();
	
	/** The last tag that was closed. */
	protected XmlTag closedtag;
	
	/** The current attributes. */
	Map<String, String> attrs;
	
	public PullParserWrapper(InputStream in)
	{
		XmlPullParserFactory factory;
		try
		{
			factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			factory.setValidating(false);
			parser = factory.newPullParser();
		}
		catch (XmlPullParserException e)
		{
			throw new RuntimeException(e);
		}
		
		try
		{
			isreader = new InputStreamReader(in, SXML.DEFAULT_ENCODING);
		}
		catch (UnsupportedEncodingException e)
		{
			throw new RuntimeException(e);
		}
		
		try
		{
			parser.setInput(isreader);
		}
		catch (XmlPullParserException e)
		{
			throw new RuntimeException(e);
		}
		hasnext = true;
	}

	public PullParserWrapper(XmlPullParser parser) {
		this.parser = parser;
		hasnext = true;
	}
	
	/**
	 *  Gets the XML event type.
	 *  
	 *  @return Event type.
	 */
	public int getEventType()
	{
		Integer ret = EVENT_TYPE_MAPPING.get(inttype);
		return ret == null? Integer.MIN_VALUE : ret;
	}
	
	/**
	 *  Returns if the reader has more events.
	 *  
	 *  @return True, if there are more events.
	 */
	public boolean hasNext()
	{
		return hasnext;
	}
	
	/**
	 *  Selects the next event.
	 */
	public int next()
	{
		try
		{
			inttype = parser.nextToken();
		}
		catch (Exception e)
		{
			if (e instanceof RuntimeException)
			{
				throw (RuntimeException) e;
			}
			throw new RuntimeException(e);
		}
		if (inttype == XmlPullParser.END_DOCUMENT)
		{
			hasnext = false;
		}
		
		if (inttype == XmlPullParser.START_TAG)
		{
			tagstack.addFirst(new XmlTag(parser.getNamespace(), parser.getName()));
			if (parser.getAttributeCount() > 0)
			{
				attrs = new HashMap<String, String>(parser.getAttributeCount());
		    	for (int i = 0; i < parser.getAttributeCount(); ++i)
		    	{
		    		attrs.put(parser.getAttributeName(i), XmlUtil.unescapeString(parser.getAttributeValue(i)));
		    	}
			}
			else
			{
				attrs = null;
			}
		}
		
		if (inttype == XmlPullParser.END_TAG)
		{
			closedtag = tagstack.removeFirst();
		}

		return getEventType();
	}
	
	/**
	 *  Get the XML tag struct.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getXmlTag()
	{
		return tagstack.peek();
	}
	
	/**
	 *  Get the XML tag struct of the last closed tag.
	 *  
	 *  @return Struct defining the tag.
	 */
	public XmlTag getClosedTag()
	{
		return closedtag;
	}
	
	/**
	 *  Get the XML tag stack.
	 *  
	 *  @return Stack defining the tags.
	 */
	public LinkedList<XmlTag> getXmlTagStack()
	{
		return tagstack;
	}
	
	/**
	 *  Returns the attributes.
	 *  
	 *  @return The attributes.
	 */
	public Map<String, String> getAttributes()
	{
		return attrs;
	}
	
	/**
	 *  Get the text for the element.
	 *  
	 *  @return The text.
	 */
	public String getText()
	{
		return parser.getText();
	}
	
	/**
	 *  Closes the reader.
	 */
	public void close()
	{
		try
		{
			if (isreader != null) {
				isreader.close();
			}
		}
		catch (IOException e)
		{
		}
	}

	/**
	 * Returns the current parser location.
	 * @return Location
	 */
	public ILocation getLocation()
	{
		return new Location(parser.getLineNumber(),
				parser.getColumnNumber(), 0, null, null);
	}

	@Override
	public String getLocalName() {
		return parser.getName();
	}

	@Override
	public int getAttributeCount() {
		return parser.getAttributeCount();
	}

	@Override
	public String getAttributeLocalName(int i) {
		return parser.getAttributeName(i);
	}

	@Override
	public String getAttributeValue(int i) {
		return parser.getAttributeValue(i);
	}

	@Override
	public QName getName() {
		return new QName(parser.getNamespace(), parser.getName(), parser.getPrefix());
	}

	@Override
	public String getAttributePrefix(int i) {
		return parser.getAttributePrefix(i);
	}

	@Override
	public String getAttributeNamespace(int i) {
		return parser.getAttributeNamespace(i);
	}
}
