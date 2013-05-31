package jadex.bridge;

import jadex.base.Starter;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.ICommandFuture;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ICommandFuture.Type;

/**
 *  The result listener for executing listener invocations as a component step.
 */
public class ComponentResultListener<E> implements IResultListener<E>, IFutureCommandListener
{
	//-------- attributes --------
	
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The external acess. */
	protected IExternalAccess access;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IComponentAdapter adapter)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not null.");
		this.listener = listener;
		this.adapter = adapter;
	}
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IExternalAccess access)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not null.");
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
		if(access!=null)
		{
			access.scheduleStep(new IComponentStep<Void>()
			{
				public static final String XML_CLASSNAME = "res";
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						listener.resultAvailable(result);
					}
					catch(Exception e)
					{
						// always return null to ensure that listener is not invoked twice
					}
					return IFuture.DONE;
				}
			}).addResultListener(new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
				}
				
				public void exceptionOccurred(final Exception exception)
				{
					Starter.scheduleRescueStep(access.getComponentIdentifier(), new Runnable()
					{
						public void run()
						{
							listener.exceptionOccurred(exception);
						}
					});
				}
			});
		}
		else
		{
			if(adapter.isExternalThread())
			{
				try
				{
					adapter.invokeLater(new Runnable()
					{
						public void run()
						{
							listener.resultAvailable(result);
						}
						
						public String toString()
						{
							return "resultAvailable("+result+")_#"+this.hashCode();
						}
					});
				}
				catch(final Exception e)
				{
					Starter.scheduleRescueStep(adapter.getComponentIdentifier(), new Runnable()
					{
						public void run()
						{
							listener.exceptionOccurred(e);
						}
					});
				}
			}
			else
			{
				listener.resultAvailable(result);
			}	
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void exceptionOccurred(final Exception exception)
	{
		if(access!=null)
		{
			access.scheduleStep(new IComponentStep<Void>()
			{
				public static final String XML_CLASSNAME = "ex";
				public IFuture<Void> execute(IInternalAccess ia)
				{
					try
					{
						listener.exceptionOccurred(exception);
					}
					catch(Exception e)
					{
						// always return null to ensure that listener is not invoked twice
					}
					return IFuture.DONE;
				}
			}).addResultListener(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
				}
				public void exceptionOccurred(Exception exception)
				{
					listener.exceptionOccurred(exception);
				}
			});
		}
		else
		{
			if(adapter.isExternalThread() && !Starter.isRescueThread(adapter.getComponentIdentifier()))
			{
				try
				{
					adapter.invokeLater(new Runnable()
					{
						public void run()
						{
//							try
//							{
								listener.exceptionOccurred(exception);
//							}
//							catch(Exception e) 
//							{
//								exception.printStackTrace();
//								e.printStackTrace();
//							}
						}
						
						public String toString()
						{
							return "exceptionOccurred("+exception+")_#"+this.hashCode();
						}
					});
				}
				catch(Exception e)
				{
					listener.exceptionOccurred(e);
				}
			}
			else
			{
				listener.exceptionOccurred(exception);
			}
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
			System.out.println("Cannot forward command: "+listener+" "+command);
		}
	}
}
