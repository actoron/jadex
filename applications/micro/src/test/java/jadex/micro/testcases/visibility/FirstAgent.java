package jadex.micro.testcases.visibility;

import java.util.Collection;
import java.util.Iterator;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;


/**
 *  Agent that provides a service and 
 */
@Agent
@Arguments(@Argument(name="selfkill", clazz=boolean.class))
@RequiredServices(@RequiredService(name = "MessageService", type = IMessageService.class, binding = @Binding(scope = RequiredServiceInfo.SCOPE_GLOBAL)))
@ProvidedServices(@ProvidedService(type = IMessageService.class, name = "MessageService", implementation = @Implementation(MessageService.class)))
@Results(@Result(name="found", clazz=IServiceIdentifier[].class))
public class FirstAgent
{
	@Agent
	private IInternalAccess ia;
	
	@AgentArgument
	private boolean selfkill;

	@AgentBody
	public void body()
	{
//		System.out.println("MY PLATFORM :" + ia.getComponentIdentifier().getPlatformName());
		
		@SuppressWarnings({"unchecked", "rawtypes"})
		Collection<IMessageService> services = (Collection)ia.getFeature(IRequiredServicesFeature.class).getServices("MessageService").get();
		IServiceIdentifier[] res = new IServiceIdentifier[services!=null? services.size(): 0];
		if(services!=null)
		{
			Iterator<IMessageService> it = services.iterator();
			for(int i=0; i<services.size(); i++)
			{
				res[i] = ((IService)it.next()).getId();
			}
			ia.getFeature(IArgumentsResultsFeature.class).getResults().put("found", res);
			if(selfkill)
				ia.killComponent();
		}
	}
}