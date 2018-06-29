package jadex.binary;

import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Abstract encoding context that provides base functionality.
 *
 */
public abstract class AbstractEncodingContext implements IEncodingContext
{
	/** Map with known objects and their IDs */
	protected Map<Object, Long> knownobjects;
	
	/** The last input object. */
	protected Object lastinputobject;
	
	/** The preprocessors. */
	protected List<ITraverseProcessor> preprocessors;
	
	/** The classloader */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** A user context. */
	protected Object usercontext;
	
	/** Flag indicating class names should not be written (can be temporarily disabled for one write). */
	protected boolean ignorewriteclass;
	
	/** The cache for non-inner classes. */
	protected Set<Class<?>> nonanonclasscache = new HashSet<Class<?>>();
	
	/** The bytes written to the output. */
	protected long writtenbytes;
	
	public AbstractEncodingContext(Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader)
	{
		this.knownobjects = new IdentityHashMap<Object, Long>();
		this.rootobject = rootobject;
		this.usercontext = usercontext;
		this.preprocessors = preprocessors;
		this.classloader = classloader;
		this.ignorewriteclass = false;
	}
	
	/**
	 *  Returns the preprocessors.
	 *  @return The preprocessors
	 */
	public List<ITraverseProcessor> getPreprocessors()
	{
		return preprocessors;
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Get the rootobject.
	 *  @return the rootobject.
	 */
	public Object getRootObject()
	{
		return rootobject;
	}
	
	/**
	 *  Returns the number of bytes written.
	 *  
	 *  @return The number of bytes written.
	 */
	public long getWrittenBytes()
	{
		return writtenbytes;
	}
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}
	
	/**
	 *  Returns the non-inner class cache.
	 *  @return The non-inner class cache.
	 */
	public Set<Class<?>> getNonInnerClassCache()
	{
		return nonanonclasscache;
	}
	
	/**
	 *  Creates ID for an object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public long createObjectId()
	{
		long ret = knownobjects.size();
		knownobjects.put(lastinputobject, ret);
		return ret;
	}
	
	/**
	 *  Sets the object for which the next createObjectId() call creates an ID.
	 * @param object The object.
	 */
	public void setInputObject(Object object)
	{
		lastinputobject = object;
	}
	
	/**
	 *  Gets the ID of a known object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public Long getObjectId(Object object)
	{
		return knownobjects.get(object);
	}
	
	/**
	 *  Puts the context in a state where the next call to
	 *  writeClass is ignored.
	 *  
	 *  @param state If true, the next class write will be ignored and the state reset.
	 */
	public void setIgnoreNextClassWrite(boolean state)
	{
		ignorewriteclass = state;
	}
	
	protected boolean isIgnoreNextClassWrite()
	{
		return ignorewriteclass;
	}
}
