package jadex.micro.testcases.semiautomatic.nfpropvis;

import jadex.bridge.service.annotation.Service;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.OnStart;

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
