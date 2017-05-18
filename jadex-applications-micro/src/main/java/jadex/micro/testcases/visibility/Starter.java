package jadex.micro.testcases.visibility;

import jadex.base.PlatformConfiguration;


public class Starter
{
	public static void main(String[] args)
	{
		PlatformConfiguration config = PlatformConfiguration.getDefaultNoGui();
		config.addComponent("jadex.micro.testcases.visibility.FIRSTAgent.class");
		config.addComponent("jadex.micro.testcases.visibility.SECONDAgent.class");
		jadex.base.Starter.createPlatform(config).get();
	}
}
