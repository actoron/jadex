package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.bridge.service.component.interceptors.AbstractApplicableInterceptor;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  
 */
@Description("This agent is a minimal calculator.")
@ProvidedServices({
	@ProvidedService(type=IAddService.class, implementation=@Implementation(expression="new AddService($component)")),
	@ProvidedService(type=ISubService.class, implementation=@Implementation(expression="new SubService($component)"))}
)
@Agent
public class CorruptAdderAgent
{
	protected int calls;
	
	@Agent
	protected IInternalAccess agent;
	
	/**
	 * 
	 */
	@AgentCreated
	public IFuture<Void> agentCreated()
	{
		IService addser = (IService)agent.getFeature(IProvidedServicesFeature.class).getProvidedServices(IAddService.class)[0];
		
		agent.getFeature(IProvidedServicesFeature.class).addInterceptor(new AbstractApplicableInterceptor()
		{
			public IFuture execute(ServiceInvocationContext context)
			{
				final Future ret = new Future();
				try
				{
					if(context.getMethod().equals(IAddService.class.getMethod("add", new Class[]{double.class, double.class})))
					{
						context.setResult(new Future(new ComponentTerminatedException(agent.getId())));
//						System.out.println("hello interceptor");
//						if(calls++>0)
						{
							// Wait till agent has terminated to ensure that its
							// service is not found as result again.
							agent.killComponent().addResultListener(new IResultListener()
							{
								public void resultAvailable(Object result)
								{
//									System.out.println("agent terminated: "+getComponentIdentifier());
									ret.setResult(null);
								}
								public void exceptionOccurred(Exception exception)
								{
									System.out.println("cannot terminate, already terminated");
									ret.setException(exception);
								}
							});
						}
					}
					else
					{
						context.invoke().addResultListener(new DelegationResultListener(ret));
					}
				}
				catch(Exception e)
				{
//					e.printStackTrace();
				}
				
				return ret;
			}
		}, addser, 0);
		
		return IFuture.DONE;
	}
}

