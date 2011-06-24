package jadex.bdi.testcases.semiautomatic;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.Plan;
import jadex.bridge.service.ServiceInvalidException;
import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Plan that adds a reasoning interceptor to the service and invokes it.
 */
public class InterceptorPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		IServiceInvocationInterceptor ic = new IServiceInvocationInterceptor()
		{
			public boolean isApplicable(ServiceInvocationContext context)
			{
				return context.getMethod().getName().equals("printHello");
			}
			
			public IFuture execute(final ServiceInvocationContext sic)
			{
				final Future ret = new Future();
				final IGoal g = createGoal("reasoncall");
				g.addGoalListener(new IGoalListener()
				{
					public void goalFinished(AgentEvent ae)
					{
						if(((Boolean)g.getParameter("execute").getValue()).booleanValue())
						{
							sic.invoke().addResultListener(new DelegationResultListener(ret));
						}
						else
						{
							ret.setException(new ServiceInvalidException(sic.getMethod().getName()));
						}
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
				dispatchTopLevelGoal(g);
				return ret;
			}
		};
		
		IPrintHelloService ps = (IPrintHelloService)getServiceContainer().getProvidedService("printservice");
		IServiceInvocationInterceptor[] ics = getServiceContainer().getInterceptors(ps);
		getServiceContainer().addInterceptor(ic, ps, ics.length-1);

		try
		{
			ps.printHello().get(this);
		}
		catch(Exception e)
		{
			System.out.println("Could not call service: "+e);
		}
	}
}
