package jadex.bpmn.examples.service;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  Test agent for service process.
 */
@Agent
@RequiredServices(@RequiredService(name="calc", type=ICalculatorService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM)))
public class ServiceClientAgent
{
	//-------- attributes --------
	
	/** The agent field. */
	@Agent
	protected MicroAgent	agent;
	
	//-------- methods --------

	/**
	 * 	The agent's main method.
	 */
	@AgentBody
	public void	run()
	{
		agent.getRequiredService("calc").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				ICalculatorService	calc	= (ICalculatorService)result;
				System.out.println(agent.getAgentName()+" adding 1+2");
				calc.addValues(1, 2);

				System.out.println(agent.getAgentName()+" adding 1+2+3");
				calc.addValues(1, 2, 3);

				System.out.println(agent.getAgentName()+" subtracting 17+4");
				calc.subtractValues(17, 4);
			}
		});
	}
}
