package jadex.bdiv3.runtime.impl;

import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bdiv3.runtime.impl.RPlan.PlanLifecycleState;
import jadex.bdiv3.runtime.impl.RPlan.PlanProcessingState;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;

/**
 *  This listener has the purpose to keep the current plan
 *  in the RPLANS thread local. One problem is that the listener
 *  calls are not async so that RPLANS have to be resetted after
 *  the sync listener call.
 */
public class BDIComponentResultListener<E> implements IResultListener<E>, IUndoneResultListener<E>
{
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The interpreter. */
	protected IInternalAccess agent;
	
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
	public BDIComponentResultListener(IResultListener<E> listener, IInternalAccess agent)
	{
//		System.out.println("creating: "+this+" "+Thread.currentThread());
//		Thread.dumpStack();
		this.listener = listener;
		this.agent = agent;
		this.rplan = RPlan.RPLANS.get();
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
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread())
		{
			try
			{
				agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						doNotify(null, result);
						return IFuture.DONE;
					}
					
					public String toString()
					{
						return "resultAvailable("+result+")_#"+this.hashCode();
					}
				});
			}
			catch(final Exception e)
			{
				Starter.scheduleRescueStep(agent.getIdentifier(), new Runnable()
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
		if(!agent.getFeature(IExecutionFeature.class).isComponentThread() && !Starter.isRescueThread(agent.getIdentifier()))
		{
			try
			{
				agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						doNotify(exception, null);
						return IFuture.DONE;
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
	public void commandAvailable(Object command)
	{
		if(listener instanceof IFutureCommandResultListener<?>)
		{
			((IFutureCommandResultListener<?>)listener).commandAvailable(command);
		}
		else
		{
//			System.out.println("Cannot forward command: "+listener+" "+command);
			Logger.getLogger("bdi-component-result-listener").fine("Cannot forward command: "+listener+" "+command);
		}
	}
	
//	/**
//	 * 
//	 */
//	protected IComponentAdapter getAdapter()
//	{
//		return interpreter.getAgentAdapter();
//	}
	
	/**
	 * 
	 */
	protected void doNotify(final Exception ex, final E result)
	{
		if(rplan!=null)
		{
			// Schedule plan code notification on separate component step, as it might be triggered inside other plan execution
			agent.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				@Override
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
//						System.out.println("plan state was: "+rplan.getProcessingState());
						rplan.setProcessingState(PlanProcessingState.RUNNING);
						assert RPlan.RPLANS.get()==null : RPlan.RPLANS.get()+", "+rplan;
						RPlan.RPLANS.set(rplan);
						Exception e	= ex;
						if(rplan.isFinishing() && rplan.getLifecycleState()==PlanLifecycleState.BODY)
							e = new PlanAbortedException();
						if(e!=null)
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
//						System.out.println("setting to null "+this+" "+Thread.currentThread());
					}
					finally
					{
						rplan.setProcessingState(PlanProcessingState.WAITING);
						assert RPlan.RPLANS.get()==rplan : RPlan.RPLANS.get()+", "+rplan;
						RPlan.RPLANS.set(null);
					}
					return IFuture.DONE;
				}
			});
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
