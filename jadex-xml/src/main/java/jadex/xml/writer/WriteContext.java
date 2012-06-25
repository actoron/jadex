package jadex.xml.writer;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IContext;
import jadex.xml.Namespace;

import java.util.ArrayList;
import java.util.HashMap;
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
	
	/** The handler. */
	protected IObjectWriterHandler handler;
	
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
	
	/** The id counter. */
	protected int id;
	
	/** The namespaces. */
	protected Map namespacebypackage;
	protected int nscnt;
	
//	/** Storage for objects. */
//	protected Map storage;
	
	//-------- constructors --------
		
	/**
	 *  Create a new write context.
	 */
	public WriteContext(IObjectWriterHandler handler, XMLStreamWriter writer, Object usercontext, Object rootobject, ClassLoader classloader)
	{
		this(handler, writer, usercontext, rootobject, classloader, new IdentityHashMap(), new ArrayList(), new MultiCollection());
	}
		
	/**
	 *  Create a new read context.
	 */
	public WriteContext(IObjectWriterHandler handler, XMLStreamWriter writer, Object usercontext, Object rootobject, ClassLoader classloader, 
		Map writtenobs, List stack, MultiCollection preprocessors)
	{
		this.handler = handler;
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
	 *  Get the handler.
	 *  @return the handler.
	 */
	public IObjectWriterHandler getHandler()
	{
		return handler;
	}

	/**
	 *  Set the handler.
	 *  @param handler The handler to set.
	 */
	public void setHandler(IObjectWriterHandler handler)
	{
		this.handler = handler;
	}

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
	 *  Get the current object.
	 *  @return The current object.
	 * /
	public Object getCurrentObject()
	{
		return ((StackElement)getStack().get(getStack().size()-1)).getObject();
	}*/

	
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

	/**
	 *  Get the id.
	 *  @return the id.
	 */
	public int getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(int id)
	{
		this.id = id;
	}		
	
	/**
	 *  Get or create a namespace.
	 *  @param uri The namespace uri.
	 */
	public Namespace getNamespace(String uri)
	{
		if(namespacebypackage==null)
			namespacebypackage = new HashMap();
		
		Namespace ns = (Namespace)namespacebypackage.get(uri);
		if(ns==null)
		{
			String prefix = "p"+nscnt;
			ns = new Namespace(prefix, uri);
			namespacebypackage.put(uri, ns);
			nscnt++;
		}
		return ns;
	}
	
//	/**
//	 *  Get store object.
//	 */
//	public Object getStorageObject(Object key)
//	{
//		return storage==null? null: storage.get(key);
//	}
//	
//	/**
//	 *  Put an object in the storage.
//	 */
//	public void putStorageObject(Object key, Object value)
//	{
//		if(storage==null)
//			storage = new HashMap();
//		storage.put(key, value);
//	}
}
