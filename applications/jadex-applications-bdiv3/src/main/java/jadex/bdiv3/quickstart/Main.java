package jadex.bdiv3.quickstart;

import jadex.base.Starter;

/**
 *  This class allows starting the agent.
 */
public class Main
{
	/**
	 *  Start the platform and also the agent.
	 */
	public static void main(String[] args)
	{
		Starter.createPlatform(new String[]
		{
			"-component", "jadex.bdiv3.quickstart.QuickstartBDI.class"
		});
	}
}
