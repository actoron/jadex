package jadex.bridge.service.component;

import java.util.logging.Logger;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Schedule future executions on component thread. 
 */
public class ComponentFutureFunctionality extends FutureFunctionality
{
	//-------- attributes --------
	
	/** The adapter. */
	protected IInternalAccess	access;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFutureFunctionality(IInternalAccess access)
	{
		super((Logger)null);
		this.access = access;
	}

	/**
	 *  Schedule termination on component thread.
	 */
	@Override
	public void terminate(Exception reason, IResultListener<Void> terminate)
	{
		// As termination is done in listener, can use same decoupling code as for listener notification.
		notifyListener(terminate);
	}
	
	/**
	 *  Send a foward command.
	 */
	@Override
	public void sendForwardCommand(Object info, IResultListener<Void> com)
	{
		notifyListener(com);
	}
	
	/**
	 *  Send a backward command.
	 */
	@Override
	public void sendBackwardCommand(Object info, IResultListener<Void> com)
	{
		notifyListener(com);
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	@Override
	public void notifyListener(final IResultListener<Void> notify) 
	{
		// Hack!!! Notify multiple listeners at once?
		if(!access.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			try
			{
				access.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						notify.resultAvailable(null);
//						ComponentIntermediateFuture.super.notifyListener(listener);
						return IFuture.DONE;
					}
				});
			}
			catch(ComponentTerminatedException e)
			{
				notify.exceptionOccurred(e);
//				ComponentIntermediateFuture.super.notifyListener(listener);
			}
		}
		else
		{
			notify.resultAvailable(null);
//			super.notifyListener(listener);
		}
	};
	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	public IFuture<Void> notifyListener(IResultListener<?> listener) 
//	{
//		final Future<Void> ret = new Future<Void>();
//		// Hack!!! Notify multiple listeners at once?
//		if(adapter.isExternalThread())
//		{
//			try
//			{
//				ea.scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ret.setResult(null);
////						ComponentIntermediateFuture.super.notifyListener(listener);
//						return IFuture.DONE;
//					}
//				});
//			}
//			catch(ComponentTerminatedException e)
//			{
////				ComponentIntermediateFuture.super.notifyListener(listener);
//				ret.setResult(null);
//			}
//		}
//		else
//		{
//			ret.setResult(null);
////			super.notifyListener(listener);
//		}
//		
//		return ret;
//	};
	
//	/**
//	 *  Schedule listener notification on component thread. 
//	 */
//	public IFuture<Void> notifyIntermediateResult(IIntermediateResultListener<Object> listener, Object result)
//	{
//		final Future<Void> ret = new Future<Void>();
//		// Hack!!! Notify multiple results at once?
//		if(adapter.isExternalThread())
//		{
//			try
//			{
//				ea.scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ret.setResult(null);
////						ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
//						return IFuture.DONE;
//					}
//				});
//			}
//			catch(ComponentTerminatedException e)
//			{
//				ret.setException(e);
////				ComponentIntermediateFuture.super.notifyListener(listener);
//			}				
//		}
//		else
//		{
//			ret.setResult(null);
////			super.notifyIntermediateResult(listener, result);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Start the notifications.
	 */
	@Override
	public void startScheduledNotifications(IResultListener<Void> notify)
	{
		notifyListener(notify);
	}
	
//	/**
//	 * 
//	 */
//	public IFuture<Void> startScheduledNotifications()
//	{
//		final Future<Void> ret = new Future<Void>();
//		// Hack!!! Notify multiple results at once?
//		if(adapter.isExternalThread())
//		{
//			try
//			{
//				ea.scheduleStep(new IComponentStep<Void>()
//				{
//					public IFuture<Void> execute(IInternalAccess ia)
//					{
//						ret.setResult(null);
////						ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
//						return IFuture.DONE;
//					}
//				});
//			}
//			catch(ComponentTerminatedException e)
//			{
//				ret.setException(e);
////				ComponentIntermediateFuture.super.notifyListener(listener);
//			}				
//		}
//		else
//		{
//			ret.setResult(null);
////			super.notifyIntermediateResult(listener, result);
//		}
//		
//		return ret;
//	}
};