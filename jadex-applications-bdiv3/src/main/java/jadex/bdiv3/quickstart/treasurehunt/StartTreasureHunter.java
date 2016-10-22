package jadex.bdiv3.quickstart.treasurehunt;

import jadex.base.PlatformConfiguration;
import jadex.base.Starter;

/**
 *  Main class for starting the treasure hunter scenario.
 */
public class StartTreasureHunter
{
	/**
	 *  Start the platform and the agent.
	 */
	public static void main(String[] args)
	{
		PlatformConfiguration	config	= PlatformConfiguration.getDefault();
		config.addComponent("jadex.bdiv3.quickstart.treasurehunt.TreasureHunterA1BDI.class");
		Starter.createPlatform(config).get();
	}
}
