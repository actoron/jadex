package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.ComponentFuture;
import jadex.bridge.service.component.ServiceInvocationContext;
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
	public IFuture execute(ServiceInvocationContext sic)
	{
//		return sic.invoke();
		boolean scheduleable = sic.getMethod().getReturnType().equals(IFuture.class);
		return scheduleable? new ComponentFuture(ea, adapter, sic.invoke()): sic.invoke(); 
	}
}
