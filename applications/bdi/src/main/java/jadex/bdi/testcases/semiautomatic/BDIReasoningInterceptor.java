package jadex.bdi.testcases.semiautomatic;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service call interceptor that starts BDI reasoning.
 */
public class BDIReasoningInterceptor implements IServiceInvocationInterceptor
{
	/** The internal access. */
	protected IInternalAccess ia;
	
	/**
	 *  Create a new interceptor.
	 */
	public BDIReasoningInterceptor(IInternalAccess ia)
	{
		this.ia = ia;
	}
	
	/**
	 *  Test if the interceptor is applicable.
	 *  @return True, if applicable.
	 */
	public boolean isApplicable(ServiceInvocationContext context)
	{
		return context.getMethod().getName().equals("printHello");
	}
	
	/**
	 *  Execute the interceptor.
	 *  @param context The invocation context.
	 */
	public IFuture<Void> execute(final ServiceInvocationContext sic)
	{
		final Future<Void> ret = new Future<Void>();
		IBDIXAgentFeature capa = ia.getFeature(IBDIXAgentFeature.class);
		final IGoal g = capa.getGoalbase().createGoal("reasoncall");
//		g.addGoalListener(new IGoalListener()
//		{
//			public void goalFinished(AgentEvent ae)
//			{
//				if(g.isSucceeded() && ((Boolean)g.getParameter("execute").getValue()).booleanValue())
//				{
//					sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
//				}
//				else
//				{
//					ret.setException(new ServiceInvalidException(sic.getMethod().getName()));
//				}
//			}
//			
//			public void goalAdded(AgentEvent ae)
//			{
//			}
//		});
		IFuture<Void> fut = capa.getGoalbase().dispatchTopLevelGoal(g);
		fut.addResultListener(new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				if(g.isSucceeded() && ((Boolean)g.getParameter("execute").getValue()).booleanValue())
				{
					sic.invoke().addResultListener(new DelegationResultListener<Void>(ret));
				}
				else
				{
					ret.setException(new ServiceInvalidException(sic.getMethod().getName()));
				}
			}
		});
		return ret;
	}
}
