package jadex.xml.reader;

import jadex.commons.collection.MultiCollection;
import jadex.xml.StackElement;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.Location;
import jadex.xml.stax.XMLReporter;

import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

import org.xmlpull.v1.XmlPullParser;


/**
 *  Android Implementation of {@link AReadContext}.
 */
public class ReadContextAndroid extends AReadContext
{
	
	/** The XML Pull Parser */
	private XmlPullParser	pullparser;

	//-------- constructors --------

	public ReadContextAndroid(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, XmlPullParser parser, XMLReporter reporter,
			Object callcontext, ClassLoader classloader)
	{
		super(pathmanager, handler, parser, reporter, callcontext, classloader);
		this.pullparser = parser;
	}

	public ReadContextAndroid(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, XmlPullParser parser, XMLReporter reporter,
			Object callcontext, ClassLoader classloader, Object root, List stack, StackElement topse, String comment, Map readobjects,
			int readignore, MultiCollection postprocessors)
	{
		super(pathmanager, handler, parser, reporter, callcontext, classloader, root, stack, topse, comment, readobjects, readignore,
				postprocessors);
	}
	

	//-------- methods --------
	
	/**
	 * Returns the current parser location.
	 * @return Location
	 */
	public ILocation getLocation()
	{
		return new Location(pullparser.getLineNumber(),
				pullparser.getColumnNumber(), 0, null, null);
	}

}
