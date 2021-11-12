package jadex.extension.rs.publish;

import jakarta.servlet.AsyncContext;

/**
 * 
 */
public interface IAsyncContextInfo
{
	/** Async context info. */
	public static final String ASYNC_CONTEXT_INFO = "__cinfo";
	
	/**
	 *  Test if context is complete.
	 */
	public boolean isComplete();
	
	/**
	 *  Get the context itself.
	 */
	public AsyncContext getAsyncContext();
}
