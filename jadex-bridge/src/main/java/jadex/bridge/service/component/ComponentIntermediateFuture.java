package jadex.bridge.service.component;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

import java.util.Collection;

/**
 *  The component future ensures that intermediate future notifications
 *  are executed on the calling component thread.
 */
public class ComponentIntermediateFuture<E> extends IntermediateFuture<E>
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
	public ComponentIntermediateFuture(IExternalAccess ea, IComponentAdapter adapter, IIntermediateFuture<E> source)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		source.addResultListener(new IntermediateDelegationResultListener<E>(this));
	}
		
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyListener(final IResultListener<Collection<E>> listener)
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
						ComponentIntermediateFuture.super.notifyListener(listener);
						return IFuture.DONE;
					}
				});
			}
			catch(ComponentTerminatedException e)
			{
				ComponentIntermediateFuture.super.notifyListener(listener);
			}
		}
		else
		{
			super.notifyListener(listener);
		}
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyIntermediateResult(final IIntermediateResultListener<E> listener, final E result)
	{
		// Hack!!! Notify multiple results at once?
		if(adapter.isExternalThread())
		{
			try
			{
				ea.scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
						return IFuture.DONE;
					}
				});
			}
			catch(ComponentTerminatedException e)
			{
				ComponentIntermediateFuture.super.notifyListener(listener);
			}				
		}
		else
		{
			super.notifyIntermediateResult(listener, result);
		}
	}
}
