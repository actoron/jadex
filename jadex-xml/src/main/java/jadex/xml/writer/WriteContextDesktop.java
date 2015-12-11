package jadex.xml.writer;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IPreProcessor;
import jadex.xml.StackElement;

/**
 * Java SE Implementation of {@link AWriteContext}.
 */
public class WriteContextDesktop extends AWriteContext
{

	// -------- constructors --------
	/**
	 * Create a new write context.
	 */
	public WriteContextDesktop(IObjectWriterHandler handler, Object writer, Object usercontext, Object rootobject,
			ClassLoader classloader)
	{
		this(handler, writer, usercontext, rootobject, classloader, new IdentityHashMap<Object, Object>(), new ArrayList<StackElement>(), new MultiCollection<Integer, IPreProcessor>());
	}

	/**
	 * Create a new write context.
	 */
	public WriteContextDesktop(IObjectWriterHandler handler, Object writer, Object usercontext, Object rootobject,
			ClassLoader classloader, Map<Object, Object> writtenobs, List<StackElement> stack, MultiCollection<Integer, IPreProcessor> preprocessors)
	{
		super(handler, writer, usercontext, rootobject, classloader, writtenobs, stack, preprocessors);
	}

	// -------- methods --------

}
