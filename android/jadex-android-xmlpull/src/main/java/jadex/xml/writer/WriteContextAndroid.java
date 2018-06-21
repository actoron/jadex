package jadex.xml.writer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlSerializer;

import jadex.commons.collection.MultiCollection;

/**
 * Android Implementation of {@link AWriteContext}.
 */
public class WriteContextAndroid extends AWriteContext
{
	// -------- constructors --------
	/**
	 * Create a new write context.
	 */
	public WriteContextAndroid(IObjectWriterHandler handler, XmlSerializer writer, Object usercontext, Object rootobject,
			ClassLoader classloader)
	{
		this(handler, writer, usercontext, rootobject, classloader, new IdentityHashMap(), new ArrayList(), new MultiCollection());
	}

	/**
	 * Create a new write context.
	 */
	public WriteContextAndroid(IObjectWriterHandler handler, XmlSerializer writer, Object usercontext, Object rootobject,
			ClassLoader classloader, Map writtenobs, List stack, MultiCollection preprocessors)
	{
		super(handler, writer, usercontext, rootobject, classloader, writtenobs, stack, preprocessors);
	}

	// -------- methods --------

}
