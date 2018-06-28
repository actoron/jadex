package jadex.bridge.service.component.interceptors;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.ServiceInvocationContext;

/**
 *  Ensures that interceptor is only called when component thread is in the chain.
 */
public abstract class ComponentThreadInterceptor extends AbstractApplicableInterceptor
{
	/** The internal access. */
	protected IInternalAccess ia;	
	
	/**
	 *  Create a new ComponentThreadInterceptor. 
	 */
	public ComponentThreadInterceptor(IInternalAccess ia)
	{
		this.ia = ia;
	}

	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
//		if(!getComponent().isComponentThread())
//			System.out.println("not on comp: "+context.getMethod().toString());
//			throw new RuntimeException("Must be called on component thread: "+Thread.currentThread());

		return getComponent().getComponentFeature(IExecutionFeature.class).isComponentThread();
	}
	
	/**
	 *  Get the component.
	 */
	public IInternalAccess getComponent()
	{
		return ia;
	}
}
