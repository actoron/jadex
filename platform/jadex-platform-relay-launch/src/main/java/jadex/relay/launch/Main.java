package jadex.relay.launch;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;

/**
 *  Starter for public relay.
 */
public class Main
{
	public static void main(String[] args)
	{
		IPlatformConfiguration config	= PlatformConfigurationHandler.getMinimal();
		config.getExtendedPlatformConfiguration().setSecurity(true);	
		config.setPlatformName("PublicRelay1");
		config.setValue("wstransport", true);
		config.setValue("wsport", 8080);
		config.setValue("relaytransport", true);
		config.setValue("relayforwarding", true);
		
//		config.enhanceWith(Starter.processArgs(args));
		
		Starter.createPlatform(config, args).get();
	}
}
