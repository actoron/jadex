package jadex.commons.transformation.binaryserializer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Abstract encoding context that provides base functionality.
 *
 */
public abstract class AbstractEncodingContext implements IEncodingContext
{
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
	protected Set<Class> nonanonclasscache = new HashSet<Class>();
	
	/** The bytes written to the output. */
	protected long writtenbytes;
	
	public AbstractEncodingContext(Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader)
	{
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
	public Set<Class> getNonInnerClassCache()
	{
		return nonanonclasscache;
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
