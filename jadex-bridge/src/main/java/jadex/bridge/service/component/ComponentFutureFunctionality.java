package jadex.bridge.service.component;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

public class ComponentFutureFunctionality extends FutureFunctionality
{
	//-------- attributes --------
	
	/** The adapter. */
	protected IComponentAdapter	adapter;
	
	/** The external acces. */
	protected IExternalAccess	ea;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFutureFunctionality(IExternalAccess ea, IComponentAdapter adapter)
	{
		this.ea	= ea;
		this.adapter	= adapter;
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	public void notifyListener(final IResultListener<Void> notify) 
	{
		// Hack!!! Notify multiple listeners at once?
		if(adapter.isExternalThread())
		{
			try
			{
				ea.scheduleStep(new IComponentStep<Void>()
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