package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.ComponentFuture;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  The decoupling return interceptor ensures that the result
 *  notifications of a future a delivered on the calling 
 *  component thread.
 */
public class DecouplingReturnInterceptor extends AbstractApplicableInterceptor
{
	//-------- attributes --------
	
	/** The external access. */
	protected IExternalAccess ea;	
		
	/** The component adapter. */
	protected IComponentAdapter adapter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingReturnInterceptor(IExternalAccess ea, IComponentAdapter adapter)
	{
		assert ea!=null;
		assert adapter!=null;
		this.ea = ea;
		this.adapter = adapter;
	}
	
	//-------- methods --------
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture execute(final ServiceInvocationContext sic)
	{
//		return sic.invoke();
		Future	fut	= new Future();
		sic.invoke().addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				Object	res	= sic.getResult();
				// Replace result, if schedulable.
				if(res instanceof IFuture)
				{
					sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res));
				}
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
