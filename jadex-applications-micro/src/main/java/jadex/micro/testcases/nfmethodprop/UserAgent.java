package jadex.micro.testcases.nfmethodprop;

import jadex.bridge.service.annotation.Service;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *
 */
@Agent
@Service
@RequiredServices(@RequiredService(name="testser", type=ITestService.class, 
	binding=@Binding(create=true, creationinfo=@CreationInfo(type="provider"))))
@ComponentTypes(@ComponentType(name="provider", filename="jadex.micro.testcases.nfmethodprop.ProviderAgent.class"))
public class UserAgent
{
	/** The agent. */
	@Agent
	protected MicroAgent agent;
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		ITestService ser = (ITestService)agent.getServiceContainer().getRequiredService("testser").get();
		
		for(int i=0; i<100; i++)
		{
			ser.methodA(500).get();
			
			ser.methodB(1000).get();
		}
	}
}

