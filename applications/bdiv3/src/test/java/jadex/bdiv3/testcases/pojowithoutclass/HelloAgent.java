package jadex.bdiv3.testcases.pojowithoutclass;

import jadex.bdiv3.runtime.BDIAgent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnEnd;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentResult;

@Agent(type="bdi")
public class HelloAgent extends BDIAgent 
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