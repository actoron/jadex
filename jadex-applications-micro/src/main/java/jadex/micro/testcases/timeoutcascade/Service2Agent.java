package jadex.micro.testcases.timeoutcascade;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;

/**
 *  Service 2 agent.
 *  Offers service that needs long time (exceeds def timeout).
 */
@Service
@Agent(autoprovide=true)
public class Service2Agent implements IService2
{
	@Agent
	protected MicroAgent agent;	
	
	public IFuture<Void> service()
	{
		// wait longer than default 30 secs
		agent.waitForDelay(35000).get();
		return IFuture.DONE;
	}
}