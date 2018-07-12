package jadex.bdiv3.quickstart.treasureisland;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
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
		IPlatformConfiguration config = PlatformConfigurationHandler.getMinimal();
		config.setValue("kernel_component", true);
		config.setValue("kernel_bdiv3", true);
//		config.getRootConfig().setLogging(true);
		config.addComponent("jadex.bdiv3.quickstart.treasureisland.TreasureHunterB1BDI.class");
		Starter.createPlatform(config).get();
	}
}
