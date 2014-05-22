package jadex.bridge.service.types.factory;

import jadex.bridge.IComponentInterpreter;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;

/**
 *  Interface for operations on the component to be called from the platform.
 */
public interface IPlatformComponentAccess
{
	//-------- constants -------- 
	
	/** The currently executing component (if any). */
	// Provided for fast caller/callee context-switching avoiding to use cms.
	public static final ThreadLocal<IPlatformComponentAccess>	LOCAL	= new ThreadLocal<IPlatformComponentAccess>();
	
	//-------- methods --------
	
	/**
	 *  Perform the initialization of the component.
	 *  
	 *  @param info The component creation info.
	 *  @param interpreter The kernel specific component part.
	 *  @return A future to indicate when the initialization is done.
	 */
	public IFuture<Void>	init(ComponentCreationInfo info, IComponentInterpreter interpreter);
	
	/**
	 *  Get the user view of this platform component.
	 *  
	 *  @return An internal access exposing user operations of the component.
	 */
	public IInternalAccess	getInternalAccess();
	
	/**
	 *  Execute a step of the component.
	 *  Used for platform bootstrapping, until execution service is running.
	 *  
	 *  @return true, if component wants to be executed again. 
	 */
	public boolean executeStep();
}
