package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateDelegationResultListener;
import jadex.commons.future.IntermediateFuture;

/**
 *  The component future ensures that intermediate future notifications
 *  are executed on the calling component thread.
 */
public class ComponentIntermediateFuture extends IntermediateFuture
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
	public ComponentIntermediateFuture(IExternalAccess ea, IComponentAdapter adapter, IFuture source)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		source.addResultListener(new IntermediateDelegationResultListener(this));
	}
		
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyListener(final IResultListener listener)
	{
		// Hack!!! Notify multiple listeners at once?
		if(adapter.isExternalThread())
		{
			ea.scheduleStep(new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					ComponentIntermediateFuture.super.notifyListener(listener);
					return null;
				}
			});
		}
		else
		{
			super.notifyListener(listener);
		}
	}
	
	/**
	 *  Schedule listener notification on component thread. 
	 */
	protected void notifyIntermediateResult(final IIntermediateResultListener listener, final Object result)
	{
		// Hack!!! Notify multiple results at once?
		if(adapter.isExternalThread())
		{
			ea.scheduleStep(new IComponentStep()
			{
				public Object execute(IInternalAccess ia)
				{
					ComponentIntermediateFuture.super.notifyIntermediateResult(listener, result);
					return null;
				}
			});
		}
		else
		{
			super.notifyIntermediateResult(listener, result);
		}
	}
}
