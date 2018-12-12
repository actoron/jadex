package jadex.xml.writer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import jadex.commons.collection.MultiCollection;
import jadex.xml.IContext;
import jadex.xml.IPreProcessor;
import jadex.xml.Namespace;
import jadex.xml.StackElement;

/**
 *  Context for writing an xml.
 */
public abstract class AWriteContext implements IContext
{
	//-------- attributes --------
	
	/** The handler. */
	protected IObjectWriterHandler handler;
	
	/** The writer. */
	protected Object writer;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** The user context. */
	protected Object usercontext;
	
	/** The written objects. */
	protected Map<Object, Object> writtenobs;
	
	/** The stack. */
	protected List<StackElement> stack;
	
	/** The pre processors. */
	protected MultiCollection<Integer, IPreProcessor> preprocessors;
	
	/** The id counter. */
	protected int id;
	
	/** The namespaces. */
	protected Map<String, Namespace> namespacebypackage;
	protected int nscnt;
	
//	/** Storage for objects. */
//	protected Map storage;
	
	//-------- constructors --------
		
	/**
	 *  Create a new write context.
	 */
	public AWriteContext(IObjectWriterHandler handler, Object writer, Object usercontext, Object rootobject, ClassLoader classloader)
	{
		this(handler, writer, usercontext, rootobject, classloader, new IdentityHashMap<Object, Object>(), new ArrayList<StackElement>(), new MultiCollection<Integer, IPreProcessor>());
	}
		
	/**
	 *  Create a new write context.
	 */
	public AWriteContext(IObjectWriterHandler handler, Object writer, Object usercontext, Object rootobject, ClassLoader classloader, 
		Map<Object, Object> writtenobs, List<StackElement> stack, MultiCollection<Integer, IPreProcessor> preprocessors)
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
	public Object getWriter()
	{
		return this.writer;
	}
	
	/**
	 *  Set the writer.
	 *  @param writer The writer to set.
	 */
	public void setWriter(Object writer)
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
	public Map<Object, Object> getWrittenObjects()
	{
		return this.writtenobs;
	}

	/**
	 *  Set the writtenobs.
	 *  @param writtenobs The writtenobs to set.
	 */
	public void setWrittenObjects(Map<Object, Object> writtenobs)
	{
		this.writtenobs = writtenobs;
	}

	/**
	 *  Get the stack.
	 *  @return The stack.
	 */
	public List<StackElement> getStack()
	{
		return this.stack;
	}

	/**
	 *  Set the stack.
	 *  @param stack The stack to set.
	 */
	public void setStack(List<StackElement> stack)
	{
		this.stack = stack;
	}

	/**
	 *  Get the preprocessors.
	 *  @return The preprocessors.
	 */
	public MultiCollection<Integer, IPreProcessor> getPreProcessors()
	{
		return this.preprocessors;
	}

	/**
	 *  Set the preprocessors.
	 *  @param preprocessors The preprocessors to set.
	 */
	public void setPreProcessors(MultiCollection<Integer, IPreProcessor> preprocessors)
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
			namespacebypackage = new HashMap<String, Namespace>();
		
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
