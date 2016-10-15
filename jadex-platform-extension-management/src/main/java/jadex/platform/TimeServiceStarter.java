package jadex.platform;

import jadex.base.PlatformConfiguration;

/**
 *  Example specialization of service starter to allow java-based configuration settings
 */
public class TimeServiceStarter extends ServiceStarter
{
	/**
	 *  Time provider daemon platform configuration. 
	 */
	@Override
	protected PlatformConfiguration getConfig()
	{
		PlatformConfiguration	config	= PlatformConfiguration.getMinimalRelayAwareness();
		config.addComponent("jadex.micro.quickstart.TimeProviderAgent.class");
		return config;
	}
}
