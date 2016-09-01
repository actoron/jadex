package jadex.bridge.service.component.interceptors;

import java.util.Collection;
import java.util.logging.Logger;

import jadex.commons.DebugException;
import jadex.commons.ICommand;
import jadex.commons.IResultCommand;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IPullIntermediateFuture;
import jadex.commons.future.IPullSubscriptionIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.ITerminableIntermediateFuture;
import jadex.commons.future.ITuple2Future;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.commons.future.PullIntermediateDelegationFuture;
import jadex.commons.future.PullSubscriptionIntermediateDelegationFuture;
import jadex.commons.future.SubscriptionIntermediateDelegationFuture;
import jadex.commons.future.TerminableDelegationFuture;
import jadex.commons.future.TerminableDelegationResultListener;
import jadex.commons.future.TerminableIntermediateDelegationFuture;
import jadex.commons.future.TerminableIntermediateDelegationResultListener;
import jadex.commons.future.Tuple2Future;
import jadex.commons.future.TupleResult;

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
	 *  Log an exception.
	 */
	protected void	logException(Exception e, Exception userex, boolean terminable, boolean undone, boolean async)
	{
//		if(userex!=null)
//		{
//			StringWriter	sw	= new StringWriter();
//			userex.printStackTrace(new PrintWriter(sw));
//			getLogger().severe("Exception in future functionality: "+sw);
//		}
//		e.printStackTrace(new PrintWriter(sw));
//		Thread.dumpStack();
//		if(!undone && ! async)
//		{
//			throw e instanceof RuntimeException ? (RuntimeException) e : new RuntimeException(e);
//		}
	}
	
	//--------  control flow handling --------
	
	/**
	 *  Schedule forward in result direction,
	 *  i.e. from callee to caller,
	 *  e.g. update timer to avoid timeouts.
	 */
	public void	scheduleForward(ICommand<Void> code)
	{
		code.execute(null);
	}
	
	/**
	 *  Schedule backward in result direction,
	 *  i.e. from caller to callee,
	 *  e.g. future termination.
	 */
	public void	scheduleBackward(ICommand<Void> code)
	{
		code.execute(null);
	}
	
	//-------- data handling --------
	
	/**
	 *  Optionally alter the undone flag.
	 */
	public boolean	isUndone(boolean undone)
	{
		return undone;
	}
	
	/**
	 *  Optionally alter a result.
	 */
	public Object	handleResult(Object result)	throws Exception
	{
		return result;
	}
	
	/**
	 *  Optionally alter a result.
	 */
	public Object	handleIntermediateResult(Object result)	throws Exception
	{
		return result;
	}
	
	/**
	 *  Optionally alter finished behavior.
	 */
	public void	handleFinished(Collection<Object> results)	throws Exception
	{
	}
	
	/**
	 *  Optionally augment termination behavior.
	 */
	public void	handleTerminated(Exception reason)
	{
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
			if(source instanceof DelegatingFuture)
				System.out.println("hhhhhhhh");
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
	 *  Overwritten to change result or undone, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Collection<Object> result, boolean undone)
	{
		try
		{
			result = (Collection<Object>)func.handleResult(result);
			return DelegatingPullSubscriptionIntermediateDelegationFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingPullSubscriptionIntermediateDelegationFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleIntermediateResult(result);
			return DelegatingPullSubscriptionIntermediateDelegationFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			func.handleFinished(getIntermediateResults());
			return DelegatingPullSubscriptionIntermediateDelegationFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}
	
	/**
	 *  Pull an intermediate result.
	 */
	@Override
	public void pullIntermediateResult()
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.pullIntermediateResult();
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	@Override
	public void terminate(final Exception reason)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				func.handleTerminated(reason);
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}	
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(final Object info)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingPullSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Collection<Object> result, boolean undone)
	{
		try
		{
			result = (Collection<Object>)func.handleResult(result);
			return DelegatingPullIntermediateDelegationFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingPullIntermediateDelegationFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleIntermediateResult(result);
			return DelegatingPullIntermediateDelegationFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			func.handleFinished(getIntermediateResults());
			return DelegatingPullIntermediateDelegationFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingPullIntermediateDelegationFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingPullIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}
	
	
	/**
	 *  Pull an intermediate result.
	 */
	@Override
	public void pullIntermediateResult()
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingPullIntermediateDelegationFuture.super.pullIntermediateResult();
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	@Override
	public void terminate(final Exception reason)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				func.handleTerminated(reason);
				DelegatingPullIntermediateDelegationFuture.super.terminate(reason);
			}	
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(final Object info)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingPullIntermediateDelegationFuture.super.sendBackwardCommand(info);
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Collection<Object> result, boolean undone)
	{
		try
		{
			result = (Collection<Object>)func.handleResult(result);
			return DelegatingSubscriptionIntermediateDelegationFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingSubscriptionIntermediateDelegationFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleIntermediateResult(result);
			return DelegatingSubscriptionIntermediateDelegationFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			func.handleFinished(getIntermediateResults());
			return DelegatingSubscriptionIntermediateDelegationFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}	
	
	/**
	 *  Terminate the future.
	 */
	@Override
	public void terminate(final Exception reason)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				func.handleTerminated(reason);
				DelegatingSubscriptionIntermediateDelegationFuture.super.terminate(reason);
			}	
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(final Object info)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingSubscriptionIntermediateDelegationFuture.super.sendBackwardCommand(info);
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Collection<Object> result, boolean undone)
	{
		try
		{
			result = (Collection<Object>)func.handleResult(result);
			return DelegatingTerminableIntermediateDelegationFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingTerminableIntermediateDelegationFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleIntermediateResult(result);
			return DelegatingTerminableIntermediateDelegationFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			func.handleFinished(getIntermediateResults());
			return DelegatingTerminableIntermediateDelegationFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}
	
	/**
	 *  Terminate the future.
	 */
	@Override
	public void terminate(final Exception reason)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				func.handleTerminated(reason);
				DelegatingTerminableIntermediateDelegationFuture.super.terminate(reason);
			}	
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(final Object info)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingTerminableIntermediateDelegationFuture.super.sendBackwardCommand(info);
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleResult(result);
			return DelegatingTerminableDelegationFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingTerminableDelegationFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Notify the listener.
	 */
	@Override
	protected void notifyListener(final IResultListener<Object> listener)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTerminableDelegationFuture.super.notifyListener(listener);
			}
		});
	}

	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTerminableDelegationFuture.super.sendForwardCommand(info);
			}
		});
	}	
	
	/**
	 *  Terminate the future.
	 */
	@Override
	public void terminate(final Exception reason)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				func.handleTerminated(reason);
				DelegatingTerminableDelegationFuture.super.terminate(reason);
			}	
		});
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(final Object info)
	{
		func.scheduleBackward(new ICommand<Void>()
		{
			@Override
			public void execute(Void args)
			{
				DelegatingTerminableDelegationFuture.super.sendBackwardCommand(info);
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doSetResult(Collection<Object> result, boolean undone)
	{
		try
		{
			result = (Collection<Object>)func.handleResult(result);
			return DelegatingIntermediateFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingIntermediateFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleIntermediateResult(result);
			return DelegatingIntermediateFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			func.handleFinished(getIntermediateResults());
			return DelegatingIntermediateFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingIntermediateFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	public boolean	doSetResult(Object result, boolean undone)
	{
		try
		{
			result = func.handleResult(result);
			return DelegatingFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Notify the listener.
	 */
	@Override
	protected void notifyListener(final IResultListener<Object> listener)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingFuture.super.notifyListener(listener);
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
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
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean doSetResult(Collection<TupleResult> result, boolean undone)
	{
		try
		{
			result = (Collection<TupleResult>)func.handleResult(result);
			return DelegatingTupleFuture.super.doSetResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}
	
	/**
	 *  Overwritten to change undone, if necessary.
	 */
	@Override
	protected boolean	doSetException(Exception exception, boolean undone)
	{
		return DelegatingTupleFuture.super.doSetException(exception, func.isUndone(undone));
	}
	
	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected boolean	doAddIntermediateResult(TupleResult result, boolean undone)
	{
		try
		{
			result = (TupleResult)func.handleIntermediateResult(result);
			return DelegatingTupleFuture.super.doAddIntermediateResult(result, func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
	 *  Overwritten to change result, if necessary.
	 */
	@Override
	protected synchronized boolean doSetFinished(boolean undone)
	{
		try
		{
			Collection<?> results	= getIntermediateResults();
			func.handleFinished((Collection<Object>)results);
			return DelegatingTupleFuture.super.doSetFinished(func.isUndone(undone));
		}
		catch(Exception e)
		{
			return doSetException(e, func.isUndone(undone));
		}		
	}

	/**
     *  Start scheduled listener notifications if not already running.
     *  Must not be called from synchronized block.
     */
	@Override
    protected void	startScheduledNotifications()
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTupleFuture.super.startScheduledNotifications();
			}
		});
	}
	
	/**
	 *  Send a forward command.
	 */
	@Override
	public void sendForwardCommand(final Object info)
	{
		func.scheduleForward(new ICommand<Void>()
		{
			public void execute(Void result)
			{
				DelegatingTupleFuture.super.sendForwardCommand(info);
			}
		});
	}	
};

