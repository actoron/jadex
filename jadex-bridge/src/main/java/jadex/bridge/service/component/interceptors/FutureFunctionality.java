package jadex.bridge.service.component.interceptors;

import jadex.commons.DebugException;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.PullIntermediateDelegationFuture;
import jadex.commons.future.PullSubscriptionIntermediateDelegationFuture;
import jadex.commons.future.Tuple2Future;
import jadex.commons.future.TupleResult;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableDelegationResultListener;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;

import java.util.Collection;
import java.util.logging.Logger;

/**
 *  Default future functionality.
 */
public class FutureFunctionality
{
	/** The logger used for notification failure warnings (if any). */
	protected Logger	logger;
	protected IResultCommand<Logger, Void> loggerfetcher;
	protected boolean undone;
	
	/**
	 * 
	 */
	public FutureFunctionality(Logger logger)
	{
		this.logger	= logger;
	}
	
	/**
	 * 
	 */
	public FutureFunctionality(IResultCommand<Logger, Void> loggerfetcher)
	{
		this.loggerfetcher = loggerfetcher;
	}
	
	/**
	 *  Get the logger.
	 */
	protected Logger	getLogger()
	{
		if(logger==null)
		{
			if(loggerfetcher!=null)
			{
				logger = loggerfetcher.execute(null);
			}
			else
			{
				Logger.getAnonymousLogger();
			}
		}
		return logger;
//		return logger!=null ? logger : Logger.getAnonymousLogger();
	}
	
	/**
	 *  Get the undone.
	 *  @return The undone.
	 */
	public boolean isUndone()
	{
		return undone;
	}

	/**
	 * 
	 */
	public Object addIntermediateResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 */
	public Object addIntermediateResultIfUndone(Object result)
	{
		undone = true;
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
		undone = true;
	}
	
	/**
	 * 
	 */
	public Object setResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 */
	public Object setResultIfUndone(Object result)
	{
		undone = true;
		return result;
	}
	
	/**
	 * 
	 */
	public Exception setException(Exception exception)
	{
		return exception;
	}
	
	/**
	 * 
	 */
	public Exception setExceptionIfUndone(Exception exception)
	{
		undone = true;
		return exception;
	}
	
	/**
	 *  Terminate the future.
	 */
	public void	terminate(Exception reason, IResultListener<Void> terminate)
	{
		terminate.resultAvailable(null);
	}
	
	/**
	 *  Send a foward command.
	 */
	public void sendForwardCommand(Object info, IResultListener<Void> com)
	{
		com.resultAvailable(null);
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(Object info, IResultListener<Void> com)
	{
		com.resultAvailable(null);
	}
	
//	/**
//	 *  Notify the listener.
//	 */
//	public IFuture<Void> notifyListener(final IResultListener<?> listener)
//	{
//		return IFuture.DONE;
//	}
	
	/**
	 *  Notify the listener.
	 */
	public void notifyListener(IResultListener<Void> notify)
	{
		notify.resultAvailable(null);
	}
	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	public IFuture<Void> notifyIntermediateResult(final IIntermediateResultListener<Object> listener, final Object result)
//	{
//		return IFuture.DONE;
//	}
	
	/**
	 *  Start the notifications.
	 */
	public void startScheduledNotifications(IResultListener<Void> notify)
	{
		notify.resultAvailable(null);
	}
	
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult(IResultListener<Void> notify)
	{
		notify.resultAvailable(null);
	}
	
	/**
	 * 
	 */
	public Object setFirstResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 */
	public Object setSecondResult(Object result)
	{
		return result;
	}
	
	/**
	 * 
	 */
	public static <T> Future<T> getDelegationFuture(IFuture<T> orig, final FutureFunctionality func)
	{
		Future ret = null;
		
		if(orig instanceof IPullSubscriptionIntermediateFuture)
		{
			PullSubscriptionIntermediateDelegationFuture<Object> fut = new DelegatingPullSubscriptionIntermediateDelegationFuture((IPullSubscriptionIntermediateFuture)orig, func);
			// automatically done in future constructor
//			((Future<Collection<Object>>)orig).addResultListener(new TerminableIntermediateDelegationResultListener<Object>(fut, (ITerminableIntermediateFuture)orig));
			ret	= fut;
		}
		else if(orig instanceof IPullIntermediateFuture)
		{
			PullIntermediateDelegationFuture<Object> fut = new DelegatingPullIntermediateDelegationFuture((IPullIntermediateFuture)orig, func);
			// automatically done in future constructor
//			((Future<Collection<Object>>)orig).addResultListener(new TerminableIntermediateDelegationResultListener<Object>(fut, (ITerminableIntermediateFuture)orig));
			ret	= fut;
		}
		else if(orig instanceof ISubscriptionIntermediateFuture)
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
		else if(orig instanceof ITuple2Future)
		{
			Tuple2Future<Object, Object> fut = new DelegatingTupleFuture(func);
			((Tuple2Future<Object, Object>)orig).addResultListener(new IntermediateDelegationResultListener<TupleResult>(fut));
			ret = fut;
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
	public static Future<?> getDelegationFuture(Class<?> clazz, final FutureFunctionality func)
	{
		Future<?> ret = null;
		
		if(SReflect.isSupertype(ITuple2Future.class, clazz))
		{
			ret = new DelegatingTupleFuture(func);
		}
		else if(SReflect.isSupertype(IPullSubscriptionIntermediateFuture.class, clazz))
		{
			ret = new DelegatingPullSubscriptionIntermediateDelegationFuture(func);
		}
		else if(SReflect.isSupertype(IPullIntermediateFuture.class, clazz))
		{
			ret = new DelegatingPullIntermediateDelegationFuture(func);
		}
		else if(SReflect.isSupertype(ISubscriptionIntermediateFuture.class, clazz))
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
	 *  Connect a delegation future with the source.
	 *  Add delegation listener on the source (+ termination handling).
	 */
	public static void connectDelegationFuture(Future target, IFuture source)
	{
		if(target instanceof IPullSubscriptionIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(PullSubscriptionIntermediateDelegationFuture)target, (IPullSubscriptionIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(target instanceof IPullIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(PullIntermediateDelegationFuture)target, (IPullIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(target instanceof ISubscriptionIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(TerminableIntermediateDelegationFuture)target, (ISubscriptionIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(target instanceof ITerminableIntermediateFuture)
		{
			TerminableIntermediateDelegationResultListener lis = new TerminableIntermediateDelegationResultListener(
				(TerminableIntermediateDelegationFuture)target, (ITerminableIntermediateFuture)source);
			source.addResultListener(lis);
		}
		else if(target instanceof ITerminableFuture)
		{
			TerminableDelegationResultListener lis = new TerminableDelegationResultListener(
				(TerminableDelegationFuture)target, (ITerminableFuture)source);
			source.addResultListener(lis);
		}
		else if(target instanceof IIntermediateFuture)
		{
			source.addResultListener(new IntermediateDelegationResultListener((IntermediateFuture)target));
		}
		else
		{
			source.addResultListener(new DelegationResultListener(target));
		}
	}
}


/**
 * 
 */
class DelegatingPullSubscriptionIntermediateDelegationFuture extends PullSubscriptionIntermediateDelegationFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingPullSubscriptionIntermediateDelegationFuture(FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingPullSubscriptionIntermediateDelegationFuture(IPullSubscriptionIntermediateFuture<?> src, FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
	}
	
	/**
	 * 
	 */
	public void	setResult(Collection<Object> result)
	{
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResult(result);
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullSubscriptionIntermediateDelegationFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.addIntermediateResult(res);
		}
		catch(RuntimeException e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullSubscriptionIntermediateDelegationFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullSubscriptionIntermediateDelegationFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void startScheduledNotifications()
    {
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
    
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		func.pullIntermediateResult(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.pullIntermediateResult();
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(final Object info)
	{
		func.sendBackwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending backward command: "+exception);
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}
		});
	}
	
	/**
	 *  Send a foward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}
};

/**
 * 
 */
class DelegatingPullIntermediateDelegationFuture extends PullIntermediateDelegationFuture<Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/**
	 * 
	 */
	public DelegatingPullIntermediateDelegationFuture(FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingPullIntermediateDelegationFuture(IPullIntermediateFuture<?> src, FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
	}
	
	/**
	 * 
	 */
	public void	setResult(Collection<Object> result)
	{
		try
		{
			Collection<Object> res = (Collection<Object>)func.setResult(result);
			DelegatingPullIntermediateDelegationFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullIntermediateDelegationFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullIntermediateDelegationFuture.super.addIntermediateResult(res);
		}
		catch(RuntimeException e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullIntermediateDelegationFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullIntermediateDelegationFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullIntermediateDelegationFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			DelegatingPullIntermediateDelegationFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void startScheduledNotifications()
    {
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingPullIntermediateDelegationFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
    
	/**
	 *  Pull an intermediate result.
	 */
	public void pullIntermediateResult()
	{
		func.pullIntermediateResult(new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullIntermediateDelegationFuture.super.pullIntermediateResult();
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingPullIntermediateDelegationFuture.super.setExceptionIfUndone(exception);
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingPullIntermediateDelegationFuture.super.terminate(reason);
			}
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(final Object info)
	{
		func.sendBackwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending backward command: "+exception);
				DelegatingPullIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}
		});
	}
	
	/**
	 *  Send a foward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingPullIntermediateDelegationFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingPullIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}
};

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
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingSubscriptionIntermediateDelegationFuture(ISubscriptionIntermediateFuture<?> src, FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(e);
			}
		}
		
		return ret;
	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void startScheduledNotifications()
    {
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				if(!DelegatingSubscriptionIntermediateDelegationFuture.super.isDone())
				{
					DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(exception);
				}
				else
				{
					throw exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
				}
			}
		});
    }
    
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(final Object info)
	{
		func.sendBackwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending backward command: "+exception);
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
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
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
	}
	
	/**
	 * 
	 */
	public DelegatingTerminableIntermediateDelegationFuture(ITerminableIntermediateFuture<?> src, FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
		src.addResultListener(new TerminableIntermediateDelegationResultListener(this, src));
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(e);
			}
		}
		
		return ret;
	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void	startScheduledNotifications()
    {
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				if(!DelegatingTerminableIntermediateDelegationFuture.super.isDone())
				{
					DelegatingTerminableIntermediateDelegationFuture.super.terminate(exception);
				}
				else
				{
					throw exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
				}
			}
		});
    }
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				Logger.getAnonymousLogger().warning("Exception when terminating future: "+exception);
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(reason);
			}
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(final Object info)
	{
		func.sendBackwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending backward command: "+exception);
				DelegatingTerminableIntermediateDelegationFuture.super.sendBackwardCommand(info);
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingTerminableIntermediateDelegationFuture.super.sendForwardCommand(info);
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableDelegationFuture.super.isDone())
			{
				DelegatingTerminableDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableDelegationFuture.super.isDone())
			{
				DelegatingTerminableDelegationFuture.super.terminate(e);
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableDelegationFuture.super.isDone())
			{
				DelegatingTerminableDelegationFuture.super.terminate(e);
			}
			else
			{
				throw e;
			}
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
		catch(RuntimeException e)
		{
			if(!DelegatingTerminableDelegationFuture.super.isDone())
			{
				DelegatingTerminableDelegationFuture.super.terminate(e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Notify the listener.
	 */
	protected void notifyListener(final IResultListener<Object> listener)
	{
		func.notifyListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTerminableDelegationFuture.super.notifyListener(listener);
			}

			public void exceptionOccurred(Exception exception)
			{
				if(!DelegatingTerminableDelegationFuture.super.isDone())
				{
					DelegatingTerminableDelegationFuture.super.terminate(exception);
				}
				else
				{
					throw exception instanceof RuntimeException ? (RuntimeException) exception : new RuntimeException(exception);
				}
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	public void terminate(final Exception reason)
	{
		func.terminate(reason, new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTerminableDelegationFuture.super.terminate(reason);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when terminating future: "+exception);
				DelegatingTerminableDelegationFuture.super.terminate(reason);
			}
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	public void sendBackwardCommand(final Object info)
	{
		func.sendBackwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableDelegationFuture.super.sendBackwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending backward command: "+exception);
				DelegatingTerminableDelegationFuture.super.sendBackwardCommand(info);
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTerminableDelegationFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingTerminableDelegationFuture.super.sendForwardCommand(info);
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
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingIntermediateFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when starting scheduled notifications: "+exception);
				DelegatingIntermediateFuture.super.startScheduledNotifications();
			}
		});
    }
    
	/**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingIntermediateFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingIntermediateFuture.super.sendForwardCommand(info);
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
//	   	if(result!=null && result.getClass().getName().indexOf("Log")!=-1)
//    		System.out.println("ggg");

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
		func.notifyListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingFuture.super.notifyListener(listener);
			}
	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! functionality failed -> should change result of future to failure?
//				System.out.println("Exception when notifying: "+exception+" "+listener);
				func.getLogger().warning("Exception when notifying: "+exception);
				DelegatingFuture.super.notifyListener(listener);
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingFuture.super.sendForwardCommand(info);
			}
		});
	}
};

/**
 * 
 */
class DelegatingTupleFuture extends Tuple2Future<Object, Object>
{
	/** The future functionality. */
	protected FutureFunctionality func;
	
	/** creation stack trace. */
	protected Exception	creaex;
	
	/**
	 * 
	 */
	public DelegatingTupleFuture(FutureFunctionality func)
	{
		if(func==null)
			throw new IllegalArgumentException("Func must not null.");
		this.func = func;
		if(Future.DEBUG)
		{
			this.creaex	= new DebugException();
		}
	}
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setFirstResult(Object result)
    {
    	try
		{
			Object res = func.setFirstResult(result);
			DelegatingTupleFuture.super.setFirstResult(res);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
    }
    
    /**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setSecondResult(Object result)
    {
    	try
		{
			Object res = func.setSecondResult(result);
			DelegatingTupleFuture.super.setSecondResult(res);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
    }
	
	/**
	 * 
	 */
	public void	setResult(Collection<TupleResult> result)
	{
		try
		{
			Collection<TupleResult> res = (Collection<TupleResult>)func.setResult(result);
			DelegatingTupleFuture.super.setResult(res);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean	setResultIfUndone(Collection<TupleResult> result)
	{
		boolean ret = false;
		
		try
		{
			Collection<TupleResult> res = (Collection<TupleResult>)func.setResultIfUndone(result);
			ret = DelegatingTupleFuture.super.setResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public void addIntermediateResult(TupleResult result)
	{
		try
		{
			TupleResult res = (TupleResult)func.addIntermediateResult(result);
			DelegatingTupleFuture.super.addIntermediateResult(res);
		}
		catch(RuntimeException e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
	}
	
	/**
	 * 
	 */
	public boolean addIntermediateResultIfUndone(TupleResult result)
	{
		boolean ret = false;
		
		try
		{
			TupleResult res = (TupleResult)func.addIntermediateResultIfUndone(result);
			ret = DelegatingTupleFuture.super.addIntermediateResultIfUndone(res);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
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
			Collection col = getIntermediateResults();
			func.setFinished((Collection<Object>)col);
			DelegatingTupleFuture.super.setFinished();
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
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
			Collection col = getIntermediateResults();
			func.setFinishedIfUndone((Collection<Object>)col);
			ret = DelegatingTupleFuture.super.setFinishedIfUndone();
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
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
			DelegatingTupleFuture.super.setException(ex);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
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
			ret = DelegatingTupleFuture.super.setExceptionIfUndone(exception);
		}
		catch(Exception e)
		{
			DelegatingTupleFuture.super.setExceptionIfUndone(e);
		}
		
		return ret;
	}
	
	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
    protected void startScheduledNotifications()
    {
    	func.startScheduledNotifications(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				DelegatingTupleFuture.super.startScheduledNotifications();
			}
	
			public void exceptionOccurred(Exception exception)
			{
				DelegatingTupleFuture.super.setExceptionIfUndone(exception);
			}
		});
    }
    
    /**
	 *  Send a forward command.
	 */
	public void sendForwardCommand(final Object info)
	{
		func.sendForwardCommand(info, new IResultListener<Void>()
		{
			public void resultAvailable(Void v)
			{
				DelegatingTupleFuture.super.sendForwardCommand(info);
			}	
			public void exceptionOccurred(Exception exception)
			{
				// Hack!!! termination in functionality failed -> should change result of future to failure?
				func.getLogger().warning("Exception when sending forward command: "+exception);
				DelegatingTupleFuture.super.sendForwardCommand(info);
			}
		});
	}
};

