package jadex.micro.testcases.semiautomatic.compositeservice;

import jadex.bridge.service.component.IServiceInvocationInterceptor;
import jadex.bridge.service.component.ServiceInvocationContext;
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
//		addServiceInterceptor(IAddService.class, new IServiceInvocationInterceptor()
//		{
//			public void execute(ServiceInvocationContext context)
//			{
//				if(calls++>0)
//					killAgent();
//			}
//		});
		return IFuture.DONE;
	}
}

