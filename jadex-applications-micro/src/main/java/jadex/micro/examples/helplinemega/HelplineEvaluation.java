package jadex.micro.examples.helplinemega;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.registry.RegistryEvent;
import jadex.commons.future.FutureBarrier;

/**
 *  Main class allowing to run different evaluation scenarios for helpline scalability.
 */
public class HelplineEvaluation
{
	//-------- scenario configuration --------

	// may also be specified as command line parameter (e.g. '-spcnt 0').
	
	/** The number of SPs (positive: create only once, negative: create in each round, 0: don't create SPs). */
	private static int	spcnt	= 0;
	
	/** The number of platforms (positive: create only once, negative: create in each round). */
	private static int	platformcnt	= -20;
	
	/** The number of persons (components) to create on each (new) platform in each round. */
	private static int	personcnt	= 1000;
	
	/** True means that one service per platform matches.
	 *  False (single) means that only one service matches, regardless how many are created. */
	private static boolean	multi	= true;
	
	/** Number of measurements to perform in each round. Written value will be averaged. */
	private static int	measurecnt	= 4;
	
	//-------- variables for created elements --------
	
	/** Number of created SPs. */
	private static int	numsps	= 0;

	/** Number of created platforms. */
	private static int	numplatforms	= 0;
	
	/** Number of created persons. */
	private static int	numpersons	= 0;

	/** Output file name. */
	private static String filename;

	/** First created platform, used for searching. */
	private static IExternalAccess	firstplatform;
	
	//-------- methods --------
	
	public static void main(String[] args)	throws Exception
	{
		IPlatformConfiguration config = getConfig(args);
		Map<String, Object> argmap = Starter.parseArgs(args);
		//config.enhanceWith(Starter.processArgs(args));
		spcnt	= (Integer)argmap.get("spcnt");
		platformcnt	= (Integer)argmap.get("platformcnt");
		personcnt	= (Integer)argmap.get("personcnt");
		multi	= (Boolean)argmap.get("multi");
		measurecnt	= (Integer)argmap.get("measurecnt");
		
		if(spcnt!=0)
		{
			// When using SSPs -> disable awareness.
			config.setAwareness(false);
			createRelayAndSSPs(config, args);
		}

		if(spcnt>0)
		{
			createSPs(config, spcnt, args);
		}
		
		IExternalAccess[]	platforms	= null;
		if(platformcnt>0)
		{
			platforms	= createHelplinePlatforms(config, platformcnt, args);			
		}
		
		createOutputFile();
		
		// Loop to start additional components/platforms/SPs until program is interrupted.
		
		while(true)
//		while(numplatforms<1)
		{
			if(spcnt<0)
			{
				createSPs(config, -spcnt, args);
			}
			
			if(platformcnt<0)
			{
				platforms	= createHelplinePlatforms(config, -platformcnt, args);			
			}

			while(getProcessCpuLoad()>0.1)
			{
				Thread.sleep(500);	// Wait for registration/connection?
			}

			// No manual GC required thanks to median?
//			double memfree	= 1.0-Runtime.getRuntime().freeMemory()/Runtime.getRuntime().totalMemory(); 
//			if(memfree<0.2)
//			{
//				System.out.println("+++ GC due to mem "+((int)(memfree*100))/100+"%");
//				System.gc();
//			}
			long creation = createPersons(platforms, personcnt, args);

			// Wait until background processes have settled.
			while(getProcessCpuLoad()>0.1)
			{
				Thread.sleep(500);
			}

//			// No manual GC required thanks to median?
//			memfree	= 1.0-Runtime.getRuntime().freeMemory()/Runtime.getRuntime().totalMemory(); 
//			if(memfree<0.2)
//			{
//				System.out.println("+++ GC due to mem "+((int)(memfree*100))/100+"%");
//				System.gc();
//			}
			
			long	sum	= 0;
			int	minfound	= -1;
			for(int m=0; m<measurecnt; m++)
			{
				// Wait until background processes have settled.
				while(getProcessCpuLoad()>0.1)
				{
					Thread.sleep(500);
				}
	
	//			System.in.read(new byte[16]);	// For profiling -> press key before search
	
				// Search for first person to check if searches get slower.
				Collection<IHelpline>	found	= null;
				long	start	= System.nanoTime();
				// For more accurate results: do #measurecnt measures of #measurecnt searches each.
				for(int m2=0; m2<measurecnt; m2++)	
				{
					try
					{
						// search for person 1 (single: only present on second platform, multi: present once on each platform)
						found	= SServiceProvider.getTaggedServices(firstplatform, IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK, "person1").get();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				long	end	= System.nanoTime();
				sum	+= end-start;
				minfound	= minfound==-1 ? found!=null ? found.size() : 0 : Math.min(minfound, found!=null ? found.size() : 0);
//				if(found.size()==1)
//					System.out.println("First platform: "+firstplatform+" found: "+found);
			}
			String	search	= (""+(sum/1000000.0/measurecnt/measurecnt)).replace('.', ',');
			System.out.println("Found "+minfound+" of "+numpersons+" helpline apps in "+search+" milliseconds.");
			
			writeEntry(creation, sum/(double)measurecnt/measurecnt, minfound);
		}
	}

	/**
	 *  Parse args into config and extract settings into static fields.
	 *  @param args	The program arguments.
	 *  @return	The parsed platform configuration.
	 */
	protected static IPlatformConfiguration getConfig(String[] args)
	{
		RegistryEvent.LEASE_TIME	= 1000*60*60*24*365;
		
		IPlatformConfiguration config	= PlatformConfigurationHandler.getDefaultNoGui();
//		config.setLogging(true);
		config.setChat(false);	// Keep platform at minimum. Todo: minimal server config
		config.setSimulation(false);	// Todo: fix sim delay in registry!?
		config.setNetworkName("helpline");
		config.setNetworkPass("key:wlXEahZlSgTfqiv0LwNbsdUZ8qlgtKSSQaKK74XkJxU");
		config.setAwaMechanisms("IntraVM");
//		config.setWsTransport(false);
		config.setValue("spcnt", spcnt);
		config.setValue("platformcnt", platformcnt);
		config.setValue("personcnt", personcnt);
		config.setValue("multi", multi);
		config.setValue("measurecnt", measurecnt);
		config.setRelayTransport(spcnt!=0);	
		config.setSuperpeerClient(spcnt!=0);

		return config;
	}

	/**
	 *  Create a local relay and SSP platforms
	 *  @param config	The platform config.
	 */
	protected static void createRelayAndSSPs(IPlatformConfiguration config, String[] args)
	{
		IPlatformConfiguration relayconf	= createConfig();
		relayconf.enhanceWith(config);
		relayconf.setPlatformName("relay");
		relayconf.setTcpPort(2091);
		relayconf.setRelayForwarding(true);
		Starter.createPlatform(relayconf).get();
		
		// Set relay address for all platforms created from now on.
		config.setRelayAddresses("tcp://relay@localhost:2091");
		
		IPlatformConfiguration sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);		
		sspconf.setPlatformName("ssp1");
		Starter.createPlatform(sspconf, args).get();
		
		sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);
		sspconf.setPlatformName("ssp2");
		Starter.createPlatform(sspconf, args).get();
		
		sspconf	= createConfig();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		sspconf.setSuperpeerClient(false);
		sspconf.setPlatformName("ssp3");
		Starter.createPlatform(sspconf, args).get();
	}
	
	/**
	 *  Create a number of SP platforms.
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 */
	protected static void createSPs(IPlatformConfiguration config, int cnt, String[] args)
	{
		IPlatformConfiguration spconf	= createConfig();
		spconf.enhanceWith(config);
		spconf.setSuperpeerClient(false);
		spconf.setSuperpeer(true);
		createPlatforms(spconf, cnt, "SP", args);

		numsps	+= cnt;
	}
	
	/**
	 *  Create a number of platforms (e.g. SPs or helpline nodes).
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 *  @return The created platforms.
	 */
	protected static IExternalAccess[] createHelplinePlatforms(IPlatformConfiguration config, int cnt, String[] args)
	{
		IPlatformConfiguration helpconf	= createConfig();
		helpconf.enhanceWith(config);
		helpconf.setSuperpeer(false);
		IExternalAccess[]	ret	= createPlatforms(helpconf, cnt, "helpline", args);
		numplatforms	+= cnt;
		
		if(firstplatform==null)
			firstplatform	= ret[0];
		return ret;
	}

	/**
	 *  Create a number of platforms (e.g. SPs or helpline nodes).
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 *  @return The created platforms.
	 */
	protected static IExternalAccess[] createPlatforms(IPlatformConfiguration config, int cnt, String type, String[] args)
	{
		config.setPlatformName(type+"_*");
		System.out.println("Starting "+cnt+" "+type+" platforms.");
		IExternalAccess[]	platforms	= new IExternalAccess[cnt];
		FutureBarrier<IExternalAccess>	fubar	= new FutureBarrier<IExternalAccess>();
		long	start	= System.nanoTime();
		for(int i=0; i<cnt; i++)
		{
//			fubar.addFuture(Starter.createPlatform(config!=null? config.clone(): null));
			IPlatformConfiguration pconf	= createConfig();
			pconf.enhanceWith(config);
			pconf.setPlatformName(type+"_****");
			fubar.addFuture(Starter.createPlatform(pconf, args));
		}
		platforms	= fubar.waitForResults().get().toArray(new IExternalAccess[cnt]);
		long	end	= System.nanoTime();
		System.out.println("Started "+cnt+" "+type+" platforms in "+((end-start)/100000000/10.0)+" seconds.");
		return platforms;
	}
	
	/**
	 *  Create a number of person-specific helpline components on the given platforms.
	 *  @param platforms	The platforms to create new nodes on.
	 *  @param cnt	The number of components to create on each platform.
	 *  @return	The time needed for creation of cnt services as preformatted string.
	 */
	protected static long createPersons(IExternalAccess[] platforms, int cnt, String[] args) throws Exception
	{
		long	sum	= 0;
		for(int m=0; m<measurecnt; m++)
		{
			// Wait for CPU idle before starting measurement
			while(getProcessCpuLoad()>0.1)
				Thread.sleep(500);
			FutureBarrier<IComponentIdentifier>	fubar	= new FutureBarrier<IComponentIdentifier>();
			long start	= System.nanoTime();
			for(int i=0; i<platforms.length; i++)
			{
				IComponentManagementService	cms	= SServiceProvider.getService(platforms[i], IComponentManagementService.class).get();
				for(int j=0; j<cnt/measurecnt; j++)
				{
					int num	= multi
						? m*cnt/measurecnt + j	// multi: same person numbers used on for all platforms.
						: numpersons + m*platforms.length + i*cnt/measurecnt + j;	// single: different person numbers for each platform
					fubar.addFuture(cms.createComponent(null, HelplineAgent.class.getName()+".class",
						new CreationInfo(Collections.singletonMap("person", (Object)("person"+num))), null));
				}
			}
			fubar.waitFor().get();
			long end	= System.nanoTime();
			sum	+= end-start;
		}

		numpersons	+= cnt*platforms.length;
		String	creation	= (""+(sum/1000000.0/platforms.length)).replace('.', ',');
		System.out.println("Started "+cnt+" helpline apps in "+creation+" milliseconds. Total: "+numpersons+", per platform: "+(numpersons/numplatforms));
		return sum/platforms.length;
	}

	/**
	 *  Create the output file and return the file name.
	 *  @return	The file name.
	 *  @throws IOException
	 */
	protected static void	createOutputFile() throws IOException
	{
		filename	= "eval_"+spcnt+"_"+platformcnt+"_"+personcnt+"_"+(multi?"multi":"single")+"_"+System.currentTimeMillis()+".csv";
		FileWriter	out	= new FileWriter(filename, true);
		String	scenario	= (spcnt==0?"P2P":"SP")	+ "-" + (multi?"multi":"single");
		out.write("# of SPs;# of Platforms;# of Services;"
			+ "Creation Time ("+scenario+");"
			+ "Search Time ("+scenario+");"
			+ "Found Services;"
			+ scenario + " Settings: '-spcnt "+spcnt+" -platformcnt "+platformcnt+" -personcnt "+personcnt+" -multi "+multi+"'\n");
		out.close();
	}
	
	/**
	 *  Write an entry to the output file.
	 *  @param creation	Service creation time.
	 *  @param search	Service search time.
	 *  @param numfound	Number of services found by search.
	 *  @throws IOException
	 */
	protected static void writeEntry(double creation, double search, int numfound) throws IOException
	{
		String	screation	= (""+(creation/1000000.0)).replace('.', ',');
		String	ssearch	= (""+(search/1000000.0)).replace('.', ',');
		
		FileWriter	out	= new FileWriter(filename, true);
		out.write(numsps+";"+numplatforms+";"+numpersons+";"+screation+";"+ssearch+";"+numfound+"\n");
		out.close();
	}
	
	/**
	 *  Create a new default config.
	 */
	protected static IPlatformConfiguration	createConfig()
	{
		return PlatformConfigurationHandler.getDefaultNoGui();
	}

	// https://stackoverflow.com/questions/18489273/how-to-get-percentage-of-cpu-usage-of-os-from-java/21962037
	public static double getProcessCpuLoad() throws Exception
	{
	    // usually takes a couple of seconds before we get real values
		double	ret	= -1;
	    MBeanServer	mbs	= ManagementFactory.getPlatformMBeanServer();
	    ObjectName	name	= ObjectName.getInstance("java.lang:type=OperatingSystem");
	    AttributeList	list	= mbs.getAttributes(name, new String[]{ "ProcessCpuLoad" });

	    if(!list.isEmpty())
	    {
		    Attribute	att	= (Attribute)list.get(0);
		    ret	= (Double)att.getValue();
	    }
	    
	    System.out.println("### cpu: "+ret);
	    
	    return ret;
	}
}