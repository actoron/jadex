package jadex.micro.testcases.timeoutcascade;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.Boolean3;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  Service 1 agent.
 *  Calls service 2 on agent 2.
 */
@Agent(autoprovide=Boolean3.TRUE)
@Service
@RequiredServices({@RequiredService(name = "ser2", type=IService2.class, 
	binding = @Binding(dynamic = true, scope = Binding.SCOPE_PLATFORM))})
public class Service1Agent implements IService1
{
	@Agent
	protected IInternalAccess agent;

	public IFuture<Void> service()
	{
		IService2 ser2 = (IService2)agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("ser2").get();
		ser2.service().get();

		return IFuture.DONE;
	}
}