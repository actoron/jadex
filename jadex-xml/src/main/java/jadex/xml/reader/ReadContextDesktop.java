package jadex.xml.reader;

import jadex.commons.collection.MultiCollection;
import jadex.xml.StackElement;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.StaxLocationWrapper;
import jadex.xml.stax.XMLReporter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamReader;

/**
 *  Java SE Implementation of {@link AReadContext}.
 */
public class ReadContextDesktop extends AReadContext<XMLStreamReader>
{

	//-------- constructors --------
	
	/**
	 * @param parser
	 */
	public ReadContextDesktop(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, XMLStreamReader parser, XMLReporter reporter,
			Object callcontext, ClassLoader classloader)
	{
		super(pathmanager, handler, parser, reporter, callcontext, classloader, null, new ArrayList(), null, null, new HashMap(), 0,
				new MultiCollection());
	}

	public ReadContextDesktop(TypeInfoPathManager pathmanager, IObjectReaderHandler handler, XMLStreamReader parser, XMLReporter reporter,
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
		return StaxLocationWrapper.fromLocation(((XMLStreamReader) parser).getLocation());
	}

}
