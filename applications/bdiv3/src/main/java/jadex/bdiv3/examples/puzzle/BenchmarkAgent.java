package jadex.bdiv3.examples.puzzle;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;

@Agent(type=BDIAgentFactory.TYPE)
public class BenchmarkAgent extends SokratesAgent
{
	/**
	 *  Overwrite wait time.
	 */
	public BenchmarkAgent()
	{
		delay	= 0;
	}

	/**
	 *  Overridden to skip gui creation.	
	 */
	protected void createGui(IInternalAccess agent)
	{
	}
}
