package jadex.platform.service.registryv2;


import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;

/**
 *  Test search and query managing functionality.
 */
public class SearchQueryManagerTest
{
	/*
	 *  cases for testing: services
	 *  1) search for service -> not found (test if works with no superpeers and no other platforms)
	 *  2) start remote platform, search for service -> test if awa fallback works with one platform 
	 *  3) start remote platform, search for service -> test if awa fallback works with two platforms 
	 *  4) start SP, wait for connection from remote platforms and local platform, search for service -> test if SP connection works
	 *  5) kill one remote platform, search for service -> test if remote disconnection and service removal works
	 *  6) kill SP, search for service -> test if re-fallback to awa works
	 *  
	 *  cases for testing: queries
	 *  1) add query -> not found (test if works with no superpeers and no other platforms)
	 *  2) start remote platform, wait for service -> test if awa fallback works with one platform, also checks local duplicate removal over time
	 *  3) start remote platform, wait for service -> test if awa fallback works with two platforms 
	 *  4) start SP, wait for connection from remote platforms and local platform, should get no service-> test if duplicate removal still works
	 *  5) add second query -> wait for two services (test if works when already SP)
	 *  6) start remote platform, wait for service in both queries -> test if works for existing queries (before and after SP)
	 *  7) kill SP, start remote platform, wait for service on both queries -> test if re-fallback to awa works for queries
	 */
	
	//-------- main for testing --------
	
	/**
	 *  Main for testing.
	 */
	public static void	main(String[] args)
	{
		// Common base configuration
		IPlatformConfiguration	baseconfig	= PlatformConfigurationHandler.getMinimalComm();
		baseconfig.addComponent("jadex.platform.service.pawareness.PassiveAwarenessIntraVMAgent.class");
//		baseconfig.setGui(true);
//		baseconfig.setLogging(true);
		
		// Super peer base configuration
		IPlatformConfiguration	spbaseconfig	= baseconfig.clone();
		spbaseconfig.addComponent(SuperpeerRegistryAgent.class);
		
		IPlatformConfiguration	config;
		
		// Super peer AB
		config	= spbaseconfig.clone();
		config.setPlatformName("SPAB_*");
		config.setNetworkNames("network-a", "network-b");
		config.setNetworkSecrets("secret-a1234", "secret-b1234");
		Starter.createPlatform(config, args).get();
		
		// Super peer BC
		config	= spbaseconfig.clone();
		config.setPlatformName("SPBC_*");
		config.setNetworkNames("network-c", "network-b");
		config.setNetworkSecrets("secret-c1234", "secret-b1234");
		Starter.createPlatform(config, args).get();

		// Client ABC
		config	= baseconfig.clone();
		config.addComponent(SuperpeerClientAgent.class);
		config.setPlatformName("ClientABC_*");
		config.setNetworkNames("network-a", "network-b", "network-c");
		config.setNetworkSecrets("secret-a1234", "secret-b1234", "secret-c1234");
		Starter.createPlatform(config, args).get();
	}
}
