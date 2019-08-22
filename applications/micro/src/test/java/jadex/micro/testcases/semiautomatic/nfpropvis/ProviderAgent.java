package jadex.micro.testcases.semiautomatic.nfpropvis;

import jadex.bridge.service.annotation.OnStart;
import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;

/**
 *  Just provider without using services.
 */
@Agent
@Service
public class ProviderAgent extends ProviderAndUserAgent
{
	/**
	 *  The agent body.
	 */
	//@AgentBody
	@OnStart
	public void body()
	{
		// Empty overridden.
	}
}
