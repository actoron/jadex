package jadex.commons.transformation.binaryserializer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractDecodingContext implements IDecodingContext
{
	/** The classloader */
	protected ClassLoader classloader;
	
	/** A user context. */
	protected Object usercontext;

	/** The postprocessors. */
	protected List<IDecoderHandler> postprocessors;
	
	/** The last decoded object */
	protected Object lastobject;
	
	/** The current class name. */
	protected String currentclassname;
	
	/** Error Reporter */
	protected IErrorReporter errorreporter;
	
	/** Already known objects */
	protected Map<Integer, Object> knownobjects;
	
	/**
	 *  Initializes the context.
	 */
	public AbstractDecodingContext(List<IDecoderHandler> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter)
	{
		this.postprocessors = postprocessors;
		this.usercontext = usercontext;
		this.classloader = classloader;
		this.errorreporter = errorreporter;
		this.knownobjects = new HashMap<Integer, Object>();
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassloader()
	{
		return classloader;
	}
	
	/**
	 *  Returns the handlers used for post-processing.
	 *  @return Post-processing handlers.
	 */
	public List<IDecoderHandler> getPostProcessors()
	{
		return postprocessors;
	}
	
	/**
	 *  Returns the known objects.
	 *  @return Known objects.
	 */
	public Map<Integer, Object> getKnownObjects()
	{
		return knownobjects;
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
	 *  Gets the error reporter.
	 *  @return The error reporter.
	 */
	public IErrorReporter getErrorReporter()
	{
		return errorreporter;
	}
	
	/**
	 *  Returns the last object decoded.
	 *  @return The last object decoded.
	 */
	public Object getLastObject()
	{
		return lastobject;
	}
	
	/**
	 *  Sets the last object decoded.
	 *  @param lastobject The last object decoded.
	 */
	public void setLastObject(Object lastobject)
	{
		this.lastobject = lastobject;
	}
	
	/**
	 *  Gets the current class name.
	 *  @return The current class name.
	 */
	public String getCurrentClassName()
	{
		return this.currentclassname;
	}
	
	/**
	 *  Sets the current class name.
	 *  @return The current class name.
	 */
	protected void setCurrentClassName(String currentclassname)
	{
		this.currentclassname = currentclassname;
	}
}
