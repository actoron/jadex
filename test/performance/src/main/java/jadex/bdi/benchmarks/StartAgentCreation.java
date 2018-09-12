package jadex.bdi.benchmarks;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;

/**
 *  Starter class for agent creation benchmark.
 */
public class StartAgentCreation 
{
	/**
	 *  Main method starts creation benchmark.
	 */
	public static void main(String[] args) throws Exception
	{
		IExternalAccess ea = Starter.createPlatform(new String[]
		{
//			"-logging", "true",
			"-gui", "false",
			"-extensions", "null",
			"-cli", "false",
//			"-awareness", "false"
		}).get();
		ea.createComponent(new CreationInfo().setFilename("jadex.bdi.benchmarks.AgentCreation.agent.xml")).get();
	}
}
