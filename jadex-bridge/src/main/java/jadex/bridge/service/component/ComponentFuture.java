package jadex.bridge.service.component;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.JadexCloner;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.component.interceptors.DecouplingInterceptor;
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
	
	/** The result copy flag. */
	protected boolean copy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFuture(IExternalAccess ea, IComponentAdapter adapter, IFuture source, boolean copy)
	{
		this.ea	= ea;
		this.adapter	= adapter;
		this.copy = copy;
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
	
	/**
     *  Set the result. 
     *  Listener notifications occur on calling thread of this method.
     *  @param result The result.
     */
    public void	setResult(Object result)
    {
		// Copy result if
		// - copy flag is true
		// - and result is not a reference object
    	if(copy && result!=null)
		{
			boolean copy = !SServiceProvider.isLocalReference(result);
			if(copy)
			{
//				System.out.println("copy result: "+result);
				result = JadexCloner.deepCloneObject(result);
			}
		}
		super.setResult(result);
    }
}
