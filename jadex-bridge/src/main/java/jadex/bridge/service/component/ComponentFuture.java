package jadex.bridge.service.component;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The component future ensures that future result/exception notifications
 *  are executed on the calling component thread.
 */
public class ComponentFuture<E> extends Future<E>
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
	public ComponentFuture(IExternalAccess ea, IComponentAdapter adapter, IFuture<E> source)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		source.addResultListener(new DelegationResultListener<E>(this));
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyListener(final IResultListener<E> listener)
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
						ComponentFuture.super.notifyListener(listener);
						return IFuture.DONE;
					}
				});
			}
			catch(ComponentTerminatedException e)
			{
				// Hack!!! Schedule notification on wrong thread. 
				ComponentFuture.this.exception	= e;
				ComponentFuture.super.notifyListener(listener);				
			}
//			.addResultListener(new IResultListener<Void>()
//			{
//				public void resultAvailable(Void result)
//				{
//				}
//				public void exceptionOccurred(Exception exception)
//				{
//					// Only component terminated exception can occur
////					assert exception instanceof ComponentTerminatedException: exception;
//					
//					if(exception instanceof ComponentTerminatedException)
//					{
//					}
//					else
//					{
//						System.out.println("Unexpected exception during schedule step: "+exception);
//					}
//				}
//			});
		}
		else
		{
			super.notifyListener(listener);
		}
	}
}