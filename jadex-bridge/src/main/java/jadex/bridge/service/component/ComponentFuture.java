package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The component future ensures that future result/exception notifications
 *  are executed on the calling component thread.
 */
public class ComponentFuture extends Future
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
	public ComponentFuture(IExternalAccess ea, IComponentAdapter adapter, IFuture source)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		source.addResultListener(new DelegationResultListener(this));
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
					ComponentFuture.super.notifyListener(listener);
					return null;
				}
			});
		}
		else
		{
			super.notifyListener(listener);
		}
	
	}
}
