package jadex.platform;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;

/**
 *  Example specialization of service starter to allow java-based configuration settings
 */
public class TimeServiceStarter extends ServiceStarter
{
	/**
	 *  Time provider daemon platform configuration. 
	 */
	@Override
	protected IPlatformConfiguration getConfig()
	{
		IPlatformConfiguration	config	= PlatformConfigurationHandler.getMinimalComm();
		config.addComponent("jadex.micro.quickstart.TimeProviderAgent.class");
		return config;
	}
}
