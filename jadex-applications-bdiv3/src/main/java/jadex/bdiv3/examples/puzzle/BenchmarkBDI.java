package jadex.bdiv3.examples.puzzle;

import jadex.bridge.IInternalAccess;
import jadex.micro.annotation.Agent;

@Agent
public class BenchmarkBDI extends SokratesBDI
{
	/**
	 *  Overwrite wait time.
	 */
	public BenchmarkBDI()
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
