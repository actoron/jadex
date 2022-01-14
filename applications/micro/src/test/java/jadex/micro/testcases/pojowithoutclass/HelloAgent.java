package jadex.micro.testcases.pojowithoutclass;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentResult;

@Agent
public class HelloAgent
{
	@AgentResult
	protected String result;
	
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	@OnStart
	public void body()
	{		
		System.out.println("hello body end: "+getClass().getName());
		result = "executed";
		agent.killComponent();
	}
}