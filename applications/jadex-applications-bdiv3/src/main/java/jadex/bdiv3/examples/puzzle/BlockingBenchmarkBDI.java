package jadex.bdiv3.examples.puzzle;

import jadex.bridge.IInternalAccess;
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
	protected void createGui(IInternalAccess agent)
	{
	}
}
