package jadex.micro.examples.helplinemega;

import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import jadex.commons.future.FutureBarrier;

/**
 *  Main class allowing to run different evaluation scenarios for helpline scalability.
 */
public class HelplineEvaluation
{
	//-------- scenario configuration --------

	// may also be specified as command line parameter (e.g. '-spcnt 0').
	
	/** The number of SPs (positive: create only once, negative: create in each round, 0: don't create SPs). */
	private static int	spcnt	= 3;
	
	/** The number of platforms (positive: create only once, negative: create in each round). */
	private static int	platformcnt	= -1;
	
	/** The number of persons (components) to create on each (new) platform in each round. */
	private static int	personcnt	= 1000;
	
	/** Fixed name true means that all services match the query.
	 *  Fixed name false means that only the first person on each platform in each round matches. */
	private static boolean	fixedname	= false;
	
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
	
	/** Values for average creation time. */
	private static List<Double>	avgcreation	= new ArrayList<Double>();
	
	/** Values for average search time. */
	private static List<Double>	avgsearch	= new ArrayList<Double>();

	
	//-------- methods --------
	
	public static void main(String[] args)	throws Exception
	{
		IPlatformConfiguration config = parseArgs(args);
		
		if(spcnt!=0)
		{
			// When using SSPs -> disable awareness.
			config.setAwareness(false);
			createRelayAndSSPs(config);
		}

		if(spcnt>0)
		{
			createSPs(config, spcnt);
		}
		
		IExternalAccess[]	platforms	= null;
		if(platformcnt>0)
		{
			platforms	= createHelplinePlatforms(config, platformcnt);			
		}
		
		createOutputFile();
		
		// Loop to start additional components/platforms/SPs until program is interrupted.
		
		while(true)
//		while(numplatforms<1)
		{
			if(spcnt<0)
			{
				createSPs(config, -spcnt);
			}
			
			if(platformcnt<0)
			{
				platforms	= createHelplinePlatforms(config, -platformcnt);			
			}

			while(getProcessCpuLoad()>0.1)
			{
				Thread.sleep(500);	// Wait for registration/connection?
			}

			System.gc();
			long creation = createPersons(platforms, personcnt);

			while(getProcessCpuLoad()>0.1)
			{
				Thread.sleep(500);	// Wait for registration/connection?
			}

			// Search for first person to check if searches get slower.
			System.gc();
//			System.in.read();
			Collection<IHelpline>	found	= null;
			long	start	= System.nanoTime();
			try
			{
				found	= SServiceProvider.getTaggedServices(firstplatform, IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK, "person0").get();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			long	end	= System.nanoTime();
			int numfound	= found!=null ? found.size() : 0;
			String	search	= (""+((end-start)/1000000.0)).replace('.', ',');
			System.out.println("Found "+numfound+" of "+numpersons+" helpline apps in "+search+" milliseconds.");
//			System.out.println("Services: "+found);
			
			writeEntry(creation, end-start, numfound);
		}
	}

	/**
	 *  Parse args into config and extract settings into static fields.
	 *  @param args	The program arguments.
	 *  @return	The parsed platform configuration.
	 */
	protected static IPlatformConfiguration parseArgs(String[] args)
	{
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
		config.setValue("fixedname", fixedname);
		config.enhanceWith(Starter.processArgs(args));
		spcnt	= (Integer) config.getArgs().get("spcnt");
		platformcnt	= (Integer) config.getArgs().get("platformcnt");
		personcnt	= (Integer) config.getArgs().get("personcnt");
		fixedname	= (Boolean) config.getArgs().get("fixedname");

		config.setRelayTransport(spcnt!=0);	
		config.setSuperpeerClient(spcnt!=0);

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
	 *  Create a number of SP platforms.
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 */
	protected static void createSPs(IPlatformConfiguration config, int cnt)
	{
		IPlatformConfiguration spconf	= createConfig();
		spconf.enhanceWith(config);
		spconf.setSuperpeerClient(false);
		spconf.setSuperpeer(true);
		createPlatforms(spconf, cnt, "SP");

		numsps	+= cnt;
	}
	
	/**
	 *  Create a number of platforms (e.g. SPs or helpline nodes).
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 *  @return The created platforms.
	 */
	protected static IExternalAccess[] createHelplinePlatforms(IPlatformConfiguration config, int cnt)
	{
		IPlatformConfiguration helpconf	= createConfig();
		helpconf.enhanceWith(config);
		helpconf.setSuperpeer(false);
		IExternalAccess[]	ret	= createPlatforms(helpconf, cnt, "helpline");
		numplatforms	+= cnt;
		
		if(firstplatform==null)
		{
			firstplatform	= ret[0];
		}
		return ret;
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
		IExternalAccess[]	platforms	= new IExternalAccess[cnt];
		FutureBarrier<IExternalAccess>	fubar	= new FutureBarrier<IExternalAccess>();
		long	start	= System.nanoTime();
		for(int i=0; i<cnt; i++)
		{
			IPlatformConfiguration pconf	= createConfig();
			pconf.enhanceWith(config);
			pconf.setPlatformName(type+"_****");
			fubar.addFuture(Starter.createPlatform(pconf));
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
	 *  @return	The time needed for creation as preformatted string.
	 */
	protected static long createPersons(IExternalAccess[] platforms, int cnt)
	{
		FutureBarrier<IComponentIdentifier>	fubar	= new FutureBarrier<IComponentIdentifier>();
		long start	= System.nanoTime();
		for(int i=0; i<platforms.length; i++)
		{
			IComponentManagementService	cms	= SServiceProvider.getService(platforms[i], IComponentManagementService.class).get();
			for(int j=0; j<cnt; j++)
			{
				Object	person	= fixedname ? "person0" : "person"+j;// : "person"+(numpersons+j);
				fubar.addFuture(cms.createComponent(null, HelplineAgent.class.getName()+".class",
					new CreationInfo(Collections.singletonMap("person", person)), null));
			}
		}
		fubar.waitFor().get();
		long end	= System.nanoTime();

		numpersons	+= cnt*platforms.length;
		String	creation	= (""+((end-start)/1000000.0)).replace('.', ',');
		System.out.println("Started "+cnt*platforms.length+" helpline apps in "+creation+" milliseconds. Total: "+numpersons+", per platform: "+(numpersons/numplatforms));
		return end-start;
	}

	/**
	 *  Create the output file and return the file name.
	 *  @return	The file name.
	 *  @throws IOException
	 */
	protected static void	createOutputFile() throws IOException
	{
		filename	= "eval_"+spcnt+"_"+platformcnt+"_"+personcnt+"_"+fixedname+"_"+System.currentTimeMillis()+".csv";
		FileWriter	out	= new FileWriter(filename, true);
		out.write("# of SPs;# of Platforms;# of Services;"
			+ "Creation Time ("	+ (spcnt==0?"Awa":"SP")	+ ");"
			+ "Search Time ("	+ (spcnt==0?"Awa":"SP")	+ ");"
			+ "Found Services;"
			+ (spcnt==0?"Awa":"SP") + " Settings: '-spcnt "+spcnt+" -platformcnt "+platformcnt+" -personcnt "+personcnt+"'\n");
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
		avgcreation.add(creation);
		avgsearch.add(search);
		
		if(platformcnt>=0 || numplatforms%10==0)
		{
			System.out.println("++++ "+numplatforms+": "+avgcreation+" "+avgsearch);
			
			creation	= 0;
			for(double c: avgcreation)
				creation	+= c;
			creation	/= avgcreation.size();
			avgcreation.clear();
			
			search	= 0;
			for(double s: avgsearch)
				search	+= s;
			search	/= avgsearch.size();
			avgsearch.clear();
			
			String	screation	= (""+(creation/1000000.0)).replace('.', ',');
			String	ssearch	= (""+(search/1000000.0)).replace('.', ',');
			
			FileWriter	out	= new FileWriter(filename, true);
			out.write(numsps+";"+numplatforms+";"+numpersons+";"+screation+";"+ssearch+";"+numfound+"\n");
			out.close();
		}
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