package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.service.component.AbstractApplicableInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;

/**
 *  
 */
@Description("This agent is a minimal calculator.")
@ProvidedServices({
	@ProvidedService(type=IAddService.class, expression="new AddService($component)"),
	@ProvidedService(type=ISubService.class, expression="new SubService($component)")}
)
public class CorruptAdderAgent extends MicroAgent
{
	protected int calls;
	
	/**
	 * 
	 */
	public IFuture agentCreated()
	{
		final Future ret = new Future();
		addProvidedServiceInterceptor(IAddService.class, new AbstractApplicableInterceptor()
		{
			public IFuture execute(ServiceInvocationContext context)
			{
				System.out.println("hello interceptor");
				if(calls++>0)
					killAgent();
				return context.invoke();
			}
		}).addResultListener(new DelegationResultListener(ret));
		return ret;
	}
}

