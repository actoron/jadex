package jadex.bdiv3.examples.puzzle;

import jadex.bdiv3.BDIAgent;
import jadex.micro.annotation.Agent;

@Agent
public class BlockingBenchmarkBDI extends BlockingSokratesBDI
{
	/**
	 *  Overwrite wait time.
	 */
	public BlockingBenchmarkBDI()
	{
		delay	= 0;
	}

	/**
	 *  Overridden to skip gui creation.	
	 */
	protected void createGui(BDIAgent agent)
	{
	}
}
