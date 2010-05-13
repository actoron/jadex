package jadex.xml.writer;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IContext;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLStreamWriter;

/**
 *  Context for writing an xml.
 */
public class WriteContext implements IContext
{
	//-------- attributes --------
	
	/** The writer. */
	protected XMLStreamWriter writer;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** The user context. */
	protected Object usercontext;
	
	/** The written objects. */
	protected Map writtenobs;
	
	/** The stack. */
	protected List stack;
	
	/** The pre processors. */
	protected MultiCollection preprocessors;
	
	//-------- constructors --------
		
	/**
	 *  Create a new write context.
	 */
	public WriteContext(XMLStreamWriter writer, Object usercontext, Object rootobject, ClassLoader classloader)
	{
		this(writer, usercontext, rootobject, classloader, new IdentityHashMap(), new ArrayList(), new MultiCollection());
	}
		
	/**
	 *  Create a new read context.
	 */
	public WriteContext(XMLStreamWriter writer, Object usercontext, Object rootobject, ClassLoader classloader, 
		Map writtenobs, List stack, MultiCollection preprocessors)
	{
		this.writer = writer;
		this.usercontext = usercontext;
		this.rootobject = rootobject;
		this.classloader = classloader;
		this.writtenobs = writtenobs;
		this.stack = stack;
		this.preprocessors = preprocessors;
	}

	//-------- methods --------
	
	/**
	 *  Get the writer.
	 *  @return The writer.
	 */
	public XMLStreamWriter getWriter()
	{
		return this.writer;
	}

	/**
	 *  Set the writer.
	 *  @param writer The writer to set.
	 */
	public void setWriter(XMLStreamWriter writer)
	{
		this.writer = writer;
	}

	/**
	 *  Get the root object.
	 *  @return The root object.
	 */
	public Object getRootObject()
	{
		return rootobject;
	}

	/**
	 *  Set the root object.
	 *  @param root The rootobject to set.
	 */
	public void setRootObject(Object root)
	{
		this.rootobject = root;
	}
	
	/**
	 *  Get the classloader.
	 *  @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return this.classloader;
	}

	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassLoader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}

	/**
	 *  Get the callcontext.
	 *  @return The callcontext.
	 */
	public Object getUserContext()
	{
		return this.usercontext;
	}

	/**
	 *  Set the callcontext.
	 *  @param callcontext The callcontext to set.
	 */
	public void setUserContext(Object usercontext)
	{
		this.usercontext = usercontext;
	}

	/**
	 *  Get the writtenobs.
	 *  @return The writtenobs.
	 */
	public Map getWrittenObjects()
	{
		return this.writtenobs;
	}

	/**
	 *  Set the writtenobs.
	 *  @param writtenobs The writtenobs to set.
	 */
	public void setWrittenObjects(Map writtenobs)
	{
		this.writtenobs = writtenobs;
	}

	/**
	 *  Get the stack.
	 *  @return The stack.
	 */
	public List getStack()
	{
		return this.stack;
	}

	/**
	 *  Set the stack.
	 *  @param stack The stack to set.
	 */
	public void setStack(List stack)
	{
		this.stack = stack;
	}

	/**
	 *  Get the preprocessors.
	 *  @return The preprocessors.
	 */
	public MultiCollection getPreProcessors()
	{
		return this.preprocessors;
	}

	/**
	 *  Set the preprocessors.
	 *  @param preprocessors The preprocessors to set.
	 */
	public void setPreProcessors(MultiCollection preprocessors)
	{
		this.preprocessors = preprocessors;
	}		
	
}
