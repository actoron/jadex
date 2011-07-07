package jadex.bridge.service.component.interceptors;

import jadex.bridge.IComponentAdapter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.component.ComponentFuture;
import jadex.bridge.service.component.ComponentIntermediateFuture;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;

import java.lang.reflect.Method;

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
	
	/** The parameter copy flag. */
	protected boolean copy;
	
	//-------- constructors --------
	
	/**
	 *  Create a new invocation handler.
	 */
	public DecouplingReturnInterceptor(IExternalAccess ea, IComponentAdapter adapter, boolean copy)
	{
		assert ea!=null;
		assert adapter!=null;
		this.ea = ea;
		this.adapter = adapter;
		this.copy = copy;
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
				
				if(res instanceof IIntermediateFuture)
				{
					Method method = sic.getMethod();
					Reference ref = method.getAnnotation(Reference.class);
					boolean copy = !sic.isRemoteCall() && (ref!=null? !ref.local(): true);
					sic.setResult(new ComponentIntermediateFuture(ea, adapter, (IFuture)res, copy));
				}
				else if(res instanceof IFuture)
				{
					Method method = sic.getMethod();
					Reference ref = method.getAnnotation(Reference.class);
					boolean copy = !sic.isRemoteCall() && (ref!=null? !ref.local(): true);
					sic.setResult(new ComponentFuture(ea, adapter, (IFuture)res, copy));
				}
				super.customResultAvailable(null);
			}
		});
		return fut; 
	}
}
