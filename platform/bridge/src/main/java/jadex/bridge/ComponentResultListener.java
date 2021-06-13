package jadex.bridge;

import java.util.function.Supplier;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.SUtil;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 *  The result listener for executing listener invocations as a component step.
 */
public class ComponentResultListener<E> implements IResultListener<E>, IFutureCommandResultListener<E>, IUndoneResultListener<E>
{
	//-------- attributes --------
	
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The external access. */
	protected IExternalAccess access;
	
	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IInternalAccess component)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not be null.");
		this.listener = listener;
		this.component = component;
	}
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IExternalAccess access)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not be null.");
		this.listener = listener;
		this.access = access;
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(final E result)
	{
		scheduleForward(() ->
		{
			if(undone && listener instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
			}
			else
			{
				listener.resultAvailable(result);
			}			
		});
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(final Exception exception)
	{
		scheduleForward(() ->
		{
			if(undone && listener instanceof IUndoneResultListener)
			{
				((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
			}
			else
			{
				listener.exceptionOccurred(exception);
			}			
		});
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
	{
		this.undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		this.undone = true;
		exceptionOccurred(exception);
	}
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Object command)
	{
		scheduleForward(() ->
		{
			if(listener instanceof IFutureCommandResultListener<?>)
			{
				((IFutureCommandResultListener<?>)listener).commandAvailable(command);
			}
			else
			{
				Logger.getLogger("component-result-listener").fine("Cannot forward command: "+listener+" "+command);
	//			System.out.println("Cannot forward command: "+listener+" "+command);
			}
		});
	}
	
	//-------- helper methods --------
	
	/**
	 *  Execute a listener notification on the component.
	 */
	protected void	scheduleForward(Runnable notification)
	{
		scheduleForward(access, component, notification);
	}
	
	/**
	 *  Execute a listener notification on the component using either an external access or the internal one
	 *  and robustly use the rescue thread for the notification, when the component is terminated.
	 */
	public static void	scheduleForward(IExternalAccess access, IInternalAccess component,	Runnable notification)
	{
		// Differentiate between exception in listener (true) and exception before invocation (false)
		// to avoid double listener invocation, but invoke listener, when scheduling step fails.
		boolean	invoked[]	= new boolean[]{false};
		Supplier<IFuture<Void>>	invocation	= () ->
		{
			if(!invoked[0])
			{
				invoked[0]	= true;
				notification.run();
			}
			return IFuture.DONE;
		};
		
		// Schedule using external access, let execution feature deal with exceptions in listener code
		if(access!=null)
		{
			access.scheduleStep(ia -> invocation.get())
				.catchEx(ex -> Starter.scheduleRescueStep(access.getId(), () -> invocation.get()));
		}
		
		// Schedule using internal access, let execution feature deal with exceptions in listener code
		else if(!component.getFeature(IExecutionFeature.class).isComponentThread())
		{
			component.getFeature(IExecutionFeature.class).scheduleStep(ia -> invocation.get())
				.catchEx(ex -> Starter.scheduleRescueStep(component.getId(), () -> invocation.get()));
		}
		
		// Execute directly on component thread and also throw exception, if any
		else
		{
			invocation.get().catchEx(ex -> SUtil.throwUnchecked(ex));
		}	
	}
}
