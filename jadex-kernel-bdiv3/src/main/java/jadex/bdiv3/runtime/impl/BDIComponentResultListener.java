package jadex.bdiv3.runtime.impl;

import java.util.Collection;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bdiv3.actions.ExecutePlanStepAction;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.ICommandFuture.Type;
import jadex.commons.future.IFutureCommandListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 * 
 */
public class BDIComponentResultListener<E> implements IResultListener<E>, IUndoneResultListener<E>
{
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The interpreter. */
	protected BDIAgentInterpreter interpreter;
	
	/** The plan. */
	protected RPlan rplan;
	
	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public BDIComponentResultListener(IResultListener<E> listener, BDIAgentInterpreter interpreter)
	{
//		System.out.println("creating: "+this+" "+Thread.currentThread());
//		Thread.dumpStack();
		this.listener = listener;
		this.interpreter = interpreter;
		this.rplan = ExecutePlanStepAction.RPLANS.get();
//		if(rplan==null)
//			System.out.println("ash");
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailable(final E result)
	{
		if(getAdapter().isExternalThread())
		{
			try
			{
				getAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						doNotify(null, result);
					}
					
					public String toString()
					{
						return "resultAvailable("+result+")_#"+this.hashCode();
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(getAdapter().getComponentIdentifier(), new Runnable()
				{
					public void run()
					{
						if(undone && listener instanceof IUndoneResultListener)
						{
							((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(e);
						}
						else
						{
							listener.exceptionOccurred(e);
						}
					}
				});
			}
		}
		else
		{
			doNotify(null, result);
		}	
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(final Exception exception)
	{
		if(getAdapter().isExternalThread() && !Starter.isRescueThread(getAdapter().getComponentIdentifier()))
		{
			try
			{
				getAdapter().invokeLater(new Runnable()
				{
					public void run()
					{
						doNotify(exception, null);
					}
					
					public String toString()
					{
						return "exceptionOccurred("+exception+")_#"+this.hashCode();
					}
				});
			}
			catch(Exception e)
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(e);
				}
				else
				{
					listener.exceptionOccurred(e);
				}
			}
		}
		else
		{
			doNotify(exception, null);
		}
	}
	
	/**
	 *  Called when a command is available.
	 */
	public void commandAvailable(Type command)
	{
		if(listener instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)listener).commandAvailable(command);
		}
		else
		{
//			System.out.println("Cannot forward command: "+listener+" "+command);
			Logger.getLogger("bdi-component-result-listener").fine("Cannot forward command: "+listener+" "+command);
		}
	}
	
	/**
	 * 
	 */
	protected IComponentAdapter getAdapter()
	{
		return interpreter.getAgentAdapter();
	}
	
	/**
	 * 
	 */
	protected void doNotify(Exception ex, E result)
	{
		if(rplan!=null)
		{
//			System.out.println("plan state was: "+rplan.getProcessingState());
			rplan.setProcessingState(PlanProcessingState.RUNNING);
			ExecutePlanStepAction.RPLANS.set(rplan);
			if(rplan.aborted && rplan.getLifecycleState()==PlanLifecycleState.BODY)
				ex = new PlanAbortedException();
			if(ex!=null)
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(ex);
				}
				else
				{
					listener.exceptionOccurred(ex);
				}
			}
			else
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
				}
				else
				{
					listener.resultAvailable(result);
				}
			}
			rplan.setProcessingState(PlanProcessingState.WAITING);
//			System.out.println("setting to null "+this+" "+Thread.currentThread());
			ExecutePlanStepAction.RPLANS.set(null);
		}
		else
		{
			if(ex!=null)
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(ex);
				}
				else
				{
					listener.exceptionOccurred(ex);
				}
			}
			else
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
				}
				else
				{
					listener.resultAvailable(result);
				}
			}
		}
	}
	
	/**
	 *  Called when the result is available.
	 *  @param result The result.
	 */
	public void resultAvailableIfUndone(E result)
	{
		undone = true;
		resultAvailable(result);
	}
	
	/**
	 *  Called when an exception occurred.
	 *  @param exception The exception.
	 */
	public void exceptionOccurredIfUndone(Exception exception)
	{
		undone = true;
		exceptionOccurred(exception);
	}

	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}
	
}
