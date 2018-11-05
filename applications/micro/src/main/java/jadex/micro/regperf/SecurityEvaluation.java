package jadex.micro.regperf;

import java.util.ArrayList;
import java.util.List;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.FutureBarrier;

/**
 *  Test if query isolation works using networks
 */
public class SecurityEvaluation
{
	/**
	 *  In this variant each platform has its own network (shared with sp).
	 */
	public static void main(String[] args)	throws Exception
	{
		int len = 10;
		String[] netnames = new String[len];
		String[] netpasses = new String[len];
		String basekey = "key:wlXEahZlSgTfqiv0LwNbsdUZ8qlgtKSSQaKK74XkJxU";
		
		for(int i=0; i<len; i++)
		{
			netnames[i] = ""+i;
			netpasses[i] = basekey+i;
		}
		
		IPlatformConfiguration config = parseArgs(args);
//		config.setGui(true);
		
		// When using SSPs -> disable awareness.
		config.setAwareness(true);
//		createRelayAndSSPs(config);

		IPlatformConfiguration spconf = config.clone();
		spconf.setSuperpeerClient(false);
		spconf.setSuperpeer(true);
		spconf.setNetworkNames(netnames);
		spconf.setNetworkSecrets(netpasses);
		createPlatforms(spconf, 1, "SP");
		
		IExternalAccess[] platforms = null;
		List<String> comps = new ArrayList<String>();
		comps.add(ServiceProviderAgent.class.getName()+".class");
		comps.add(ServiceQueryAgent.class.getName()+".class");
		for(int i=0; i<len; i++)
		{
			IPlatformConfiguration conf = config.clone();
			conf.setNetworkNames(new String[] { netnames[i] });
			conf.setNetworkSecrets(new String[] { netpasses[i] });
			conf.setSuperpeer(false);
			conf.setComponents(comps);
			platforms = createPlatforms(conf, 1, "App"+i);			
		}
	}
	
//	/**
//	 *  In this variant all services are found due to shared network.
//	 */
//	public static void main(String[] args)	throws Exception
//	{
//		int len = 3;
//		String[] netnames = new String[len];
//		String[] netpasses = new String[len];
//		String basekey = "key:wlXEahZlSgTfqiv0LwNbsdUZ8qlgtKSSQaKK74XkJxU";
//		
//		for(int i=0; i<len; i++)
//		{
//			netnames[i] = ""+i;
//			netpasses[i] = basekey+i;
//		}
//		
//		IPlatformConfiguration config = parseArgs(args);
//		config.setGui(true);
//		config.setNetworkName(netnames[0]);
//		config.setNetworkPass(netpasses[0]);
//		
//		// When using SSPs -> disable awareness.
//		config.setAwareness(true);
////		createRelayAndSSPs(config);
//
//		IPlatformConfiguration spconf = config.clone();
//		spconf.setSuperpeerClient(false);
//		spconf.setSuperpeer(true);
////		spconf.setNetworkNames(netnames);
////		spconf.setNetworkPass(netpasses);
//		createPlatforms(spconf, 1, "SP");
//		
//		IExternalAccess[] platforms = null;
//		List<String> comps = new ArrayList<String>();
//		comps.add(ServiceProviderAgent.class.getName()+".class");
//		comps.add(ServiceQueryAgent.class.getName()+".class");
//		for(int i=0; i<len; i++)
//		{
//			IPlatformConfiguration conf = config.clone();
////			conf.setNetworkName(netnames[i]);
////			conf.setNetworkPass(netpasses[i]);
//			conf.setSuperpeer(false);
//			conf.setComponents(comps);
//			platforms = createPlatforms(conf, 1, "App");			
//		}
//	}

	/**
	 *  Parse args into config and extract settings into static fields.
	 *  @param args	The program arguments.
	 *  @return	The parsed platform configuration.
	 */
	protected static IPlatformConfiguration parseArgs(String[] args)
	{
		IPlatformConfiguration config = PlatformConfigurationHandler.getDefaultNoGui();
//		config.setLogging(true);
		config.getExtendedPlatformConfiguration().setChat(false);	// Keep platform at minimum. Todo: minimal server config
		config.getExtendedPlatformConfiguration().setSimulation(false);	// Todo: fix sim delay in registry!?
//		config.setNetworkName("helpline");
//		config.setNetworkPass("key:wlXEahZlSgTfqiv0LwNbsdUZ8qlgtKSSQaKK74XkJxU");
		config.getExtendedPlatformConfiguration().setAwaMechanisms("IntraVM");
//		config.setWsTransport(false);

		return config;
	}

	/**
	 *  Create a local relay and SSP platforms
	 *  @param config	The platform config.
	 */
	protected static void createRelayAndSSPs(IPlatformConfiguration config)
	{
		IPlatformConfiguration relayconf	= createConfig();
		relayconf.enhanceWith(config);
		relayconf.setPlatformName("relay");
		relayconf.getExtendedPlatformConfiguration().setTcpPort(2091);
		relayconf.getExtendedPlatformConfiguration().setRelayForwarding(true);
		Starter.createPlatform(relayconf).get();
		
		// Set relay address for all platforms created from now on.
		config.getExtendedPlatformConfiguration().setRelayAddresses("tcp://relay@localhost:2091");
		
		IPlatformConfiguration sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);		
		sspconf.setPlatformName("ssp1");
		Starter.createPlatform(sspconf).get();
		
		sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);
		sspconf.setPlatformName("ssp2");
		Starter.createPlatform(sspconf).get();
		
		sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);
		sspconf.setPlatformName("ssp3");
		Starter.createPlatform(sspconf).get();
	}
	
	/**
	 *  Create a number of platforms (e.g. SPs or helpline nodes).
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 *  @return The created platforms.
	 */
	protected static IExternalAccess[] createPlatforms(IPlatformConfiguration config, int cnt, String type)
	{
		System.out.println("Starting "+cnt+" "+type+" platforms.");
		config = config.clone();
		config.setPlatformName(type+"_****");
		IExternalAccess[]	platforms	= new IExternalAccess[cnt];
		FutureBarrier<IExternalAccess>	fubar	= new FutureBarrier<IExternalAccess>();
		long start = System.nanoTime();
		for(int i=0; i<cnt; i++)
		{
			fubar.addFuture(Starter.createPlatform(config));
		}
		platforms = fubar.waitForResults().get().toArray(new IExternalAccess[cnt]);
		long end = System.nanoTime();
		System.out.println("Started "+cnt+" "+type+" platforms in "+((end-start)/100000000/10.0)+" seconds.");
		return platforms;
	}
	
	/**
	 * 
	 */
	protected static long createAgents(IExternalAccess[] platforms, int cnt)
	{
		FutureBarrier<IExternalAccess> fubar = new FutureBarrier<IExternalAccess>();
		long start	= System.nanoTime();
		for(int i=0; i<platforms.length; i++)
		{
			for(int j=0; j<cnt; j++)
			{
				fubar.addFuture(platforms[i].createComponent(new CreationInfo().setFilename(ServiceProviderAgent.class.getName()+".class"), null));
				fubar.addFuture(platforms[i].createComponent(new CreationInfo().setFilename(ServiceQueryAgent.class.getName()+".class"), null));
			}
		}
		fubar.waitFor().get();
		long end = System.nanoTime();

		String creation = (""+((end-start)/1000000.0)).replace('.', ',');
		System.out.println("Started "+cnt*platforms.length+" helpline apps in "+creation+" milliseconds. Total: "+cnt+", per platform: "+(cnt/platforms.length));
		return end-start;
	}

	/**
	 *  Create a new default config.
	 */
	protected static IPlatformConfiguration	createConfig()
	{
		return PlatformConfigurationHandler.getDefaultNoGui();
	}
}