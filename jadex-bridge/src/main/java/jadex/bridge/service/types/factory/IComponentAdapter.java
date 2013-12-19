package jadex.bridge.service.types.factory;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.cms.IComponentDescription;

import java.util.logging.Logger;


/**
 *  The adapter for a specific platform component (e.g. a JADE agent).
 *  These are the methods a kernel components needs to call on its host component.
 *  Implementations of this interface should be thread safe.
 */
@Reference(local=true, remote=false)	// Hack!!! required because of local getComponentAdapter method of CMS.
public interface IComponentAdapter
{
	//-------- constants -------- 
	
	/** The currently executing component (if any). */
	// Provided for fast caller/callee context-switching avoiding to use cms.
	public static final ThreadLocal<IComponentAdapter>	LOCAL	= new ThreadLocal<IComponentAdapter>();
	
	//-------- methods --------
	
	/**
	 *  Block the current thread and allow execution on other threads.
	 *  @param monitor	The monitor to wait for.
	 */
	public void	block(Object monitor);
	
	/**
	 *  Unblock the thread waiting for the given monitor
	 *  and cease execution on the current thread.
	 *  @param monitor	The monitor to notify.
	 *  @param kill	True, if the blocked thread should be killed instead of continued. 
	 */
	public void	unblock(Object monitor, boolean kill);
	
	/**
	 *  Called by the component when it probably awoke from an idle state.
	 *  The platform has to make sure that the component will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no component related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void	wakeup() throws ComponentTerminatedException;
	
	/**
	 *  Execute an action on the component thread.
	 *  May be safely called from any (internal or external) thread.
	 *  The contract of this method is as follows:
	 *  The component adapter ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action to be executed on the component thread.
	 */
	public void invokeLater(Runnable action);
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread();

	/**
	 *  Return the native component-identifier that allows to send
	 *  messages to this component.
	 */
	public IComponentIdentifier getComponentIdentifier() throws ComponentTerminatedException;
	
	/**
	 *  Return the component description.
	 */
	public IComponentDescription getDescription();
	
	/**
	 *  Get the component logger.
	 *  @return The logger.
	 */
	public Logger getLogger();
	
	/**
	 *  Get the parent component.
	 *  @return The parent (if any).
	 */
	public IExternalAccess getParent();
	
	/**
	 *  Get the exception.
	 *  @return The exception.
	 */
	public Exception getException();
	
	/**
	 *  
	 */
}

