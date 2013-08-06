package jadex.micro.testcases.seqfuture;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.ISequenceFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 * 
 */
@Agent
@RequiredServices(@RequiredService(name="ts", type=ITestService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class UserAgent
{
	@Agent
	protected MicroAgent agent;
	
	/**
	 * 
	 */
	@AgentBody
	public void body()
	{
		ITestService ts = (ITestService)agent.getServiceContainer().getRequiredService("ts").get();
		ISequenceFuture<String, Integer> fut = ts.getSomeResults();
		System.out.println(fut.getFirstResult());
		System.out.println(fut.getSecondResult());
	}
}
