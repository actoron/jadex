package jadex.micro.testcases.timeoutcascade;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;


/**
 *  Service 1 agent.
 *  Calls service 2 on agent 2.
 */
@Agent(autoprovide=true)
@Service
@RequiredServices({@RequiredService(name = "ser2", type=IService2.class, 
	binding = @Binding(dynamic = true, scope = Binding.SCOPE_PLATFORM))})
public class Service1Agent implements IService1
{
	@Agent
	protected MicroAgent	agent;

	public IFuture<Void> service()
	{
		IService2 ser2 = (IService2)agent.getRequiredService("ser2").get();
		ser2.service().get();

		return IFuture.DONE;
	}
}