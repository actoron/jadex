package jadex.micro.testcases.visibility;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;


public class Starter
{
	public static void main(String[] args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getDefaultNoGui();
		config.addComponent("jadex.micro.testcases.visibility.FirstAgent.class");
		config.addComponent("jadex.micro.testcases.visibility.SecondAgent.class");
		jadex.base.Starter.createPlatform(config).get();
	}
}
