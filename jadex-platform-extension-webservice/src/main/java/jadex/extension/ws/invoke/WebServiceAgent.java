package jadex.extension.ws.invoke;

import java.lang.reflect.Proxy;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Agent that wraps a normal web service as Jadex service.
 *  In this way the web service can be used by active components
 *  in the same way as normal Jadex component services.
 */
@Agent
@RequiredServices(@RequiredService(name="cms", type=IComponentManagementService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
@ComponentTypes(@ComponentType(name="invocation", filename="jadex/platform/service/ws/WebServiceInvocationAgent.class"))
public class WebServiceAgent
{
	//-------- attributes --------
	
	/** The micro agent. */
	@Agent
	protected IInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Create a wrapper service implementation based on the JAXB generated
	 *  Java service class and the service mapping information.
	 */
	public Object createServiceImplementation(Class<?> type, WebServiceMappingInfo mapping)
	{
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new WebServiceWrapperInvocationHandler(agent, mapping));
	}
}
