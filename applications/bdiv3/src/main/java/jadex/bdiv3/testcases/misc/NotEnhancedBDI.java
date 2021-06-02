package jadex.bdiv3.testcases.misc;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.OnStart;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;

/**
 *  Agent that tests if bdi agent factory detects non-enhanced bdi classes.
 */
@Agent(type=BDIAgentFactory.TYPE)
public class NotEnhancedBDI 
{
	/** The injected agent (not injected when not enhanced). */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		if(agent==null)
		{
			throw new RuntimeException("Agent class was not enhaned but creation was initiated.");
		}
	}
}
