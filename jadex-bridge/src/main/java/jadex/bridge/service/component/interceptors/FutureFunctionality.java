package jadex.bridge.service.component.interceptors;

import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableDelegationResultListener;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

import java.util.Collection;

/**
 * 
 */
public class FutureFunctionality
{
	/**
	 * 
	 */
	public FutureFunctionality()
	{
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object addIntermediateResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object addIntermediateResultIfUndone(Object result)
	{
		return result;
	}
	
//	/**
//	 * 
//	 */
//	public Collection<Object> finished(Collection<Object> results)
//	{
//		return results;
//	}
	
	/**
	 * 
	 */
	public void setFinished(Collection<Object> results)
	{
	}
	
	/**
	 * 
	 */
	public void setFinishedIfUndone(Collection<Object> results)
	{
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object setResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 * @param result
	 * @return
	 */
	public Object setResultIfUndone(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 * @param exception
	 * @return
	 */
	public Exception setException(Exception exception)
	{
		return exception;
	}
	
	/**
	 * 
	 * @param exception
	 * @return
	 */
	public Exception setExceptionIfUndone(Exception exception)
	{
		return exception;
	}
	
	/**
	 *  Terminate the future.
	 */
	public IFuture<Void>	terminate(Exception reason)
	{
		return IFuture.DONE;
	}
	
	/**
	 *  Notify the listener.
	 */
	public IFuture<Void> notifyListener(final IResultListener<?> listener)
	{
		return IFuture.DONE;
	}
	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	public IFuture<Void> notifyIntermediateResult(final IIntermediateResultListener<Object> listener, final Object result)
//	{
//		return IFuture.DONE;
//	}
	
	/**
	 * 
	 */
	public IFuture<Void> startScheduledNotifications()
	{
		return IFuture.DONE;
	}
	
	/**
	 * 
	 */
	public static Future getDelegationFuture(IFuture<?> orig, final FutureFunctionality func)
	{
		Future ret = null;
		
		if(orig instanceof ISubscriptionIntermediateFuture)
		{
			SubscriptionIntermediateDelegationFuture<Object> fut = new DelegatingSubscriptionIntermediateDelegationFuture((ISubscriptionIntermediateFuture)orig, func);
			// automatically done in future constructor
//			((Future<Collection<Object>>)orig).addResultListener(new TerminableIntermediateDelegationResultListener<Object>(fut, (ITerminableIntermediateFuture)orig));
			ret	= fut;
		}
		else if(orig instanceof ITerminableIntermediateFuture)
		{
			TerminableIntermediateDelegationFuture<Object> fut = new DelegatingTerminableIntermediateDelegationFuture((ITerminableIntermediateFuture)orig, func);
			// automatically done in future constructor
//			((Future<Collection<Object>>)orig).addResultListener(new TerminableIntermediateDelegationResultListener<Object>(fut, (ITerminableIntermediateFuture)orig));
			ret	= fut;
		}
		else if(orig instanceof ITerminableFuture)
		{
			TerminableDelegationFuture<Object> fut = new DelegatingTerminableDelegationFuture((ITerminableFuture)orig, func);
			// automatically done in future constructor
//			((Future<Object>)orig).addResultListener(new TerminableDelegationResultListener<Object>(fut, (ITerminableFuture)orig));
			ret	= fut;
		}
		else if(orig instanceof IIntermediateFuture)
		{
			IntermediateFuture<Object>	fut	= new DelegatingIntermediateFuture(func);
			((IntermediateFuture<Object>)orig).addResultListener(new IntermediateDelegationResultListener<Object>(fut));
			ret	= fut;
		}
		else
		{
			Future<Object>	fut	= new DelegatingFuture(func);
			((Future<Object>)orig).addResultListener(new DelegationResultListener<Object>(fut));
			ret	= fut;
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static Future getDelegationFuture(Class<?> clazz, final FutureFunctionality func)
	{
		Future ret = null;
		
		if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, clazz))
		{
			ret = new DelegatingSubscriptionIntermediateDelegationFuture(func);
		}
		else if(SReflect.isSupertype(ITerminableIntermediateFuture.class, clazz))
		{
			ret = new DelegatingTerminableIntermediateDelegationFuture(func);
		}
		else if(SReflect.isSupertype(ITerminableFuture.class, clazz))
		{
			ret = new DelegatingTerminableDelegationFuture(func);
		}
		else if(SReflect.isSupertype(IIntermediateFuture.class, clazz))
		{
			ret	= new DelegatingIntermediateFuture(func);
		}
		else
		{
			ret	= new DelegatingFuture(func);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static void connectDelegationFuture(Future target, IFuture source)
	{
		if(source instanceof ISubscriptionIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(TerminableIntermediateDelegationFuture)target, (ISubscriptionIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(source instanceof ITerminableIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(TerminableIntermediateDelegationFuture)target, (ITerminableIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(source instanceof ITerminableFuture)
		{
			TerminableDelegationResultListener lis = new TerminableDelegationResultListener(
				(TerminableDelegationFuture)target, (ITerminableFuture)source);
			source.addResultListener(lis);
		}
		else if(source instanceof IIntermediateFuture)
		{
			source.addResultListener(new IntermediateDelegationResultListener((IntermediateFuture)target));
		}
		else if(source instanceof IFuture)
		{
			source.addResultListener(new DelegationResultListener((Future)target));
		}
		else
		{
			throw new IllegalArgumentException("Unknown source type: "+(source!=null ? source.getClass() : null));
		}
	}
}

/**
 * 
 */
class DelegatingSubscriptionIntermediateDelegationFuture extends SubscriptionIntermediateDelegationFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingSubscriptionIntermediateDelegationFuture(FutureFunctionality func)
	{
		super();
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingSubscriptionIntermediateDelegationFuture(ISubscriptionIntermediateFuture<?> src, FutureFunctionality func)
	{
		super(src);
		this.func = func;
	}
	
	/**
	 * 
	 */
	public void	setResult(Collection<Object> result)
	{
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResult(result);
			DelegatingSubscriptionIntermediateDelegationFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean	setResultIfUndone(Collection<Object> result)
	{
		boolean ret = false;
		
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResultIfUndone(result);
			ret = DelegatingSubscriptionIntermediateDelegationFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void addIntermediateResult(Object result)
	{
		try
		{
			Object res = func.addIntermediateResult(result);
			DelegatingSubscriptionIntermediateDelegationFuture.super.addIntermediateResult(res);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean addIntermediateResultIfUndone(Object result)
	{
		boolean ret = false;
		
		try
		{
			Object res = func.addIntermediateResultIfUndone(result);
			ret = DelegatingSubscriptionIntermediateDelegationFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	
	/**
	 * 
	 */
	public void setFinished()
	{
		try
		{
			func.setFinished((Collection<Object>)getIntermediateResults());
			DelegatingSubscriptionIntermediateDelegationFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setFinishedIfUndone()
	{
		boolean ret = false;
		
		try
		{
			func.setFinishedIfUndone((Collection<Object>)getIntermediateResults());
			ret = DelegatingSubscriptionIntermediateDelegationFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setException(Exception exception)
	{
		try
		{
			Exception ex = func.setException(exception);
			DelegatingSubscriptionIntermediateDelegationFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setExceptionIfUndone(Exception exception)
	{
		boolean ret = false;
		
		try
		{
			func.setExceptionIfUndone(exception);
			ret = DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Notify the listener.
//	 */
//	protected void notifyListener(final IResultListener<Collection<Object>> listener)
//	{
//		func.notifyListener(listener).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				DelegatingSubscriptionIntermediateDelegationFuture.super.notifyListener(listener);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
//	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	protected void notifyIntermediateResult(final IIntermediateResultListener<Object> listener, final Object result)
//	{
//		func.notifyIntermediateResult(listener, result).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void v)
//			{
//				DelegatingSubscriptionIntermediateDelegationFuture.super.notifyIntermediateResult(listener, result);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void	startScheduledNotifications()
    {
    	func.startScheduledNotifications().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.startScheduledNotifications();
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
};


/**
 * 
 */
class DelegatingTerminableIntermediateDelegationFuture extends TerminableIntermediateDelegationFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingTerminableIntermediateDelegationFuture(FutureFunctionality func)
	{
		super();
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingTerminableIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src, FutureFunctionality func)
	{
		super(src);
		this.func = func;
	}
	
	/**
	 * 
	 */
	public void	setResult(Collection<Object> result)
	{
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResult(result);
			DelegatingTerminableIntermediateDelegationFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean	setResultIfUndone(Collection<Object> result)
	{
		boolean ret = false;
		
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResultIfUndone(result);
			ret = DelegatingTerminableIntermediateDelegationFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void addIntermediateResult(Object result)
	{
		try
		{
			Object res = func.addIntermediateResult(result);
			DelegatingTerminableIntermediateDelegationFuture.super.addIntermediateResult(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean addIntermediateResultIfUndone(Object result)
	{
		boolean ret = false;
		
		try
		{
			Object res = func.addIntermediateResultIfUndone(result);
			ret = DelegatingTerminableIntermediateDelegationFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setFinished()
	{
		try
		{
			func.setFinished((Collection<Object>)getIntermediateResults());
			DelegatingTerminableIntermediateDelegationFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setFinishedIfUndone()
	{
		boolean ret = false;
		
		try
		{
			func.setFinishedIfUndone((Collection<Object>)getIntermediateResults());
			ret = DelegatingTerminableIntermediateDelegationFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setException(Exception exception)
	{
		try
		{
			Exception ex = func.setException(exception);
			DelegatingTerminableIntermediateDelegationFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setExceptionIfUndone(Exception exception)
	{
		boolean ret = false;
		
		try
		{
			func.setExceptionIfUndone(exception);
			ret = DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Notify the listener.
//	 */
//	protected void notifyListener(final IResultListener<Collection<Object>> listener)
//	{
//		func.notifyListener(listener).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				DelegatingTerminableIntermediateDelegationFuture.super.notifyListener(listener);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
//	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	protected void notifyIntermediateResult(final IIntermediateResultListener<Object> listener, final Object result)
//	{
//		func.notifyIntermediateResult(listener, result).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void v)
//			{
//				DelegatingTerminableIntermediateDelegationFuture.super.notifyIntermediateResult(listener, result);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void	startScheduledNotifications()
    {
    	func.startScheduledNotifications().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.startScheduledNotifications();
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingTerminableIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
};

/**
 * 
 */
class DelegatingTerminableDelegationFuture extends TerminableDelegationFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingTerminableDelegationFuture(FutureFunctionality func)
	{
		super();
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingTerminableDelegationFuture(ITerminableFuture<?> src, FutureFunctionality func)
	{
		super(src);
		this.func = func;
	}
	
	/**
	 * 
	 */
	public void	setResult(Object result)
	{
		try
		{
			Object res = func.setResult(result);
			DelegatingTerminableDelegationFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean	setResultIfUndone(Object result)
	{
		boolean ret = false;
		
		try
		{
			Object res = func.setResultIfUndone(result);
			ret = DelegatingTerminableDelegationFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setException(Exception exception)
	{
		try
		{
			Exception ex = func.setException(exception);
			DelegatingTerminableDelegationFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setExceptionIfUndone(Exception exception)
	{
		boolean ret = false;
		
		try
		{
			func.setExceptionIfUndone(exception);
			ret = DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 *  Notify the listener.
	 */
	protected void notifyListener(final IResultListener<Object> listener)
	{
		func.notifyListener(listener).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTerminableDelegationFuture.super.notifyListener(listener);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTerminableDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingTerminableDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
};

/**
 * 
 */
class DelegatingIntermediateFuture extends IntermediateFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingIntermediateFuture(FutureFunctionality func)
	{
		this.func = func;
	}
	
	/**
	 * 
	 */
	public void	setResult(final Collection<Object> result)
	{
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResult(result);
			DelegatingIntermediateFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean	setResultIfUndone(Collection<Object> result)
	{
		boolean ret = false;
		
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResultIfUndone(result);
			ret = DelegatingIntermediateFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void addIntermediateResult(Object result)
	{
//		System.out.println("ires: "+result+" "+this);
		try
		{
			Object res = func.addIntermediateResult(result);
			DelegatingIntermediateFuture.super.addIntermediateResult(res);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean addIntermediateResultIfUndone(Object result)
	{
		boolean ret = false;
		
		try
		{
			Object res = func.addIntermediateResultIfUndone(result);
			ret = DelegatingIntermediateFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setFinished()
	{
//		System.out.println("finished: "+result+" "+this);
		try
		{
			func.setFinished((Collection<Object>)getIntermediateResults());
			DelegatingIntermediateFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setFinishedIfUndone()
	{
		boolean ret = false;
		
		try
		{
			func.setFinishedIfUndone((Collection<Object>)getIntermediateResults());
			ret = DelegatingIntermediateFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setException(Exception exception)
	{
		try
		{
			Exception ex = func.setException(exception);
			DelegatingIntermediateFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setExceptionIfUndone(Exception exception)
	{
		boolean ret = false;
		
		try
		{
			func.setExceptionIfUndone(exception);
			ret = DelegatingIntermediateFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingIntermediateFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
//	/**
//	 *  Notify the listener.
//	 */
//	protected void notifyListener(final IResultListener<Collection<Object>> listener)
//	{
//		func.notifyListener(listener).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void result)
//			{
//				DelegatingIntermediateFuture.super.notifyListener(listener);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingIntermediateFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
//	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	protected void notifyIntermediateResult(final IIntermediateResultListener<Object> listener, final Object result)
//	{
//		func.notifyIntermediateResult(listener, result).addResultListener(new IResultListener<Void>()
//		{
//			public void resultAvailable(Void v)
//			{
//				DelegatingIntermediateFuture.super.notifyIntermediateResult(listener, result);
//			}	
//			public void exceptionOccurred(Exception exception)
//			{
//				DelegatingIntermediateFuture.super.setExceptionIfUndone(exception);
//			}
//		});
//	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void	startScheduledNotifications()
    {
    	func.startScheduledNotifications().addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingIntermediateFuture.super.startScheduledNotifications();
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingIntermediateFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
};

/**
 * 
 */
class DelegatingFuture extends Future<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingFuture(FutureFunctionality func)
	{
		this.func = func;
	}
	
	/**
	 * 
	 */
	public void	setResult(final Object result)
	{
		try
		{
			Object res = func.setResult(result);
			DelegatingFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setResultIfUndone(Object result)
	{
		boolean ret = false;
		
		try
		{
			Object res = func.setResultIfUndone(result);
			ret = DelegatingFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void setException(Exception exception)
	{
		try
		{
			Exception ex = func.setException(exception);
			DelegatingFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean setExceptionIfUndone(Exception exception)
	{
		boolean ret = false;
		
		try
		{
			func.setExceptionIfUndone(exception);
			ret = DelegatingFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 *  Notify the listener.
	 */
	protected void notifyListener(final IResultListener<Object> listener)
	{
		func.notifyListener(listener).addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingFuture.super.notifyListener(listener);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Todo: why set exception on future!?
				DelegatingFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
};

