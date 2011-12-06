package jadex.bridge.service.component.interceptors;

import jadex.bridge.IExternalAccess;
import jadex.bridge.service.component.ComponentFuture;
import jadex.bridge.service.component.ComponentIntermediateFuture;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.types.factory.IComponentAdapter;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

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
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		Future<Void> fut	= new Future<Void>();
		sic.invoke().addResultListener(new DelegationResultListener<Void>(fut)
		{
			public void customResultAvailable(Void result)
			{
				Object	res	= sic.getResult();
				
				if(res instanceof IIntermediateFuture)
				{
					sic.setResult(new ComponentIntermediateFuture(ea, adapter, (IIntermediateFuture)res));
				}
				else if(res instanceof IFuture)
				{
					sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res));
				}
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
