package jadex.bridge;

import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IFutureCommandListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IUndoneResultListener;
import jadex.commons.future.SResultListener;

/**
 *  The result listener for executing listener invocations as a component step.
 */
public class ComponentResultListener<E> implements IResultListener<E>, IFutureCommandListener, IUndoneResultListener<E>
{
	//-------- attributes --------
	
	/** The result listener. */
	protected IResultListener<E> listener;
	
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	/** The external acess. */
	protected IExternalAccess access;
	
	/** The undone flag. */
	protected boolean undone;
	
	//-------- constructors --------
	
	/**
	 *  Create a new component result listener.
	 *  @param listener The listener.
	 *  @param adapter The adapter.
	 */
	public ComponentResultListener(IResultListener<E> listener, IComponentAdapter adapter)
	{
		if(listener==null)
			throw new NullPointerException("Listener must not be null.");
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
			throw new NullPointerException("Listener must not be null.");
		this.listener = listener;
		this.access = access;
	}
	
	/**
	 * Create a new component result listener.
	 * 
	 * @param listener The functional listener.
	 * @param exceptionListener The functional exception listener. Maybe
	 *        <code>null</code>, which will lead to default exception logging.
	 * @param access External access of the component to schedule the listener
	 *        methods on.
	 */
	public ComponentResultListener(final IFunctionalResultListener<E> listener, final IFunctionalExceptionListener exceptionListener, IExternalAccess access)
	{
		if(listener == null)
		{
			throw new NullPointerException("Listener must not be null.");
		}
		if(exceptionListener != null)
		{
			this.listener = SResultListener.createResultListener(listener, exceptionListener);
		}
		else
		{
			this.listener = SResultListener.createResultListener(listener);
		}
		this.access = access;
	}

	/**
	 * Create a new component result listener.
	 * 
	 * @param listener The functional listener.
	 * @param exceptionListener The functional exception listener. Maybe
	 *        <code>null</code>, which will lead to default exception logging.
	 * @param adapter The adapter. to schedule the listener methods on.
	 */
	public ComponentResultListener(final IFunctionalResultListener<E> listener, final IFunctionalExceptionListener exceptionListener, IComponentAdapter adapter)
	{
		if(listener == null)
		{
			throw new NullPointerException("Listener must not be null.");
		}
		if(exceptionListener != null)
		{
			this.listener = SResultListener.createResultListener(listener, exceptionListener);
		}
		else
		{
			this.listener = SResultListener.createResultListener(listener);
		}
		this.adapter = adapter;
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
						if(undone && listener instanceof IUndoneResultListener)
						{
							((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
						}
						else
						{
							listener.resultAvailable(result);
						}
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
							if(undone && listener instanceof IUndoneResultListener)
							{
								((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
							}
							else
							{
								listener.exceptionOccurred(exception);
							}
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
							if(undone && listener instanceof IUndoneResultListener)
							{
								((IUndoneResultListener<E>)listener).resultAvailableIfUndone(result);
							}
							else
							{
								listener.resultAvailable(result);
							}
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
						if(undone && listener instanceof IUndoneResultListener)
						{
							((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
						}
						else
						{
							listener.exceptionOccurred(exception);
						}
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
					if(undone && listener instanceof IUndoneResultListener)
					{
						((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
					}
					else
					{
						listener.exceptionOccurred(exception);
					}
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
								if(undone && listener instanceof IUndoneResultListener)
								{
									((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
								}
								else
								{
									listener.exceptionOccurred(exception);
								}
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
					if(undone && listener instanceof IUndoneResultListener)
					{
						((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
					}
					else
					{
						listener.exceptionOccurred(exception);
					}
				}
			}
			else
			{
				if(undone && listener instanceof IUndoneResultListener)
				{
					((IUndoneResultListener<E>)listener).exceptionOccurredIfUndone(exception);
				}
				else
				{
					listener.exceptionOccurred(exception);
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
		if(listener instanceof IFutureCommandListener)
		{
			((IFutureCommandListener)listener).commandAvailable(command);
		}
		else
		{
			Logger.getLogger("component-result-listener").fine("Cannot forward command: "+listener+" "+command);
//			System.out.println("Cannot forward command: "+listener+" "+command);
		}
	}
}
