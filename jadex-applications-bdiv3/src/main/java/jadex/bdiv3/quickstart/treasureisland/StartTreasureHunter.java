package jadex.bdiv3.quickstart.treasureisland;

import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
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
		PlatformConfiguration	config	= PlatformConfiguration.getMinimal();
        config.setKernels(RootComponentConfiguration.KERNEL.micro, RootComponentConfiguration.KERNEL.v3);
//		config.getRootConfig().setLogging(true);
		config.addComponent("jadex.bdiv3.quickstart.treasureisland.TreasureHunterB1BDI.class");
		Starter.createPlatform(config).get();
	}
}
