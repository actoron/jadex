package jadex.micro.examples.helplinemega;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;

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
	
	/** Fixed name true means that all ever created services match the query.
	 *  Fixed name false means that number of found services should be constant as only the initially created services match. */
	private static boolean	fixedname	= true;
	
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
		{
			if(spcnt<0)
			{
				createSPs(config, -spcnt);
			}
			
			if(platformcnt<0)
			{
				platforms	= createHelplinePlatforms(config, -platformcnt);			
			}

			Thread.sleep(spcnt==0 ? numplatforms*500 : 50);	// Wait for registration/connection?

			System.gc();
			String creation = createPersons(platforms, personcnt);

			Thread.sleep(spcnt==0 ? numplatforms*500 : 50);	// Wait for registration/connection?

			// Search for first person to check if searches get slower.
			System.gc();
			long	start	= System.nanoTime();
			int numfound	= 0;
			try
			{
				Collection<IHelpline>	found	= SServiceProvider.getTaggedServices(firstplatform, IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK, "person0").get();
				numfound = found.size();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			long	end	= System.nanoTime();
			String	search	= (""+((end-start)/1000000.0)).replace('.', ',');
			System.out.println("Found "+numfound+" of "+numpersons+" helpline apps in "+search+" milliseconds.");
			
			writeEntry(creation, search, numfound);
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
		config.setChat(false);	// Keep platform at minimum. Todo: minimal server config
		config.setSimulation(false);	// Todo: fix sim delay in registry!?
		config.setNetworkName("helpline");
		config.setNetworkPass("p09 p6rfzb pﬂ7pv0 78rtvo0b 67rf");
		config.setAwaMechanisms("Local");
		config.setValue("spcnt", spcnt);
		config.setValue("platformcnt", platformcnt);
		config.setValue("personcnt", personcnt);
		config.setValue("fixedname", fixedname);
		config.enhanceWith(Starter.processArgs(args));
		spcnt	= (Integer) config.getArgs().get("spcnt");
		platformcnt	= (Integer) config.getArgs().get("platformcnt");
		personcnt	= (Integer) config.getArgs().get("personcnt");
		fixedname	= (Boolean) config.getArgs().get("fixedname");
		return config;
	}

	/**
	 *  Create a local relay and SSP platforms
	 *  @param config	The platform config.
	 */
	protected static void createRelayAndSSPs(IPlatformConfiguration config)
	{
		IPlatformConfiguration relayconf	= PlatformConfigurationHandler.getDefaultNoGui();
		relayconf.enhanceWith(config);
		relayconf.setPlatformName("relay");
		relayconf.setTcpPort(2091);
		relayconf.setRelayForwarding(true);
		Starter.createPlatform(relayconf).get();
		
		config.setRelayAddresses("tcp://relay@localhost:2091");
		
		IPlatformConfiguration sspconf	= PlatformConfigurationHandler.getDefaultNoGui();
		sspconf.enhanceWith(config);
		sspconf.setSupersuperpeer(true);
		
		sspconf.setPlatformName("ssp1");
		Starter.createPlatform(sspconf).get();
		
		sspconf.setPlatformName("ssp2");
		Starter.createPlatform(sspconf).get();
		
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
		config.setSuperpeer(true);
		createPlatforms(config, cnt, "SP");
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
		config.setSuperpeer(false);
		IExternalAccess[]	ret	= createPlatforms(config, cnt, "helpline");
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
		config.setPlatformName(type+"_*");
		System.out.println("Starting "+cnt+" "+type+" platforms.");
		long	start	= System.nanoTime();
		IExternalAccess[]	platforms	= new IExternalAccess[cnt];
		for(int i=0; i<cnt; i++)
		{
			platforms[i]	= Starter.createPlatform(config).get();
			System.out.println("Started "+type+" platform #"+i);
		}
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
	protected static String createPersons(IExternalAccess[] platforms, int cnt)
	{
		long start	= System.nanoTime();
		for(int i=0; i<platforms.length; i++)
		{
			IComponentManagementService	cms	= SServiceProvider.getService(platforms[i], IComponentManagementService.class).get();
			for(int j=0; j<cnt; j++)
			{
				Object	person	= fixedname ? "person"+j : "person"+(numpersons+j);
				cms.createComponent(HelplineAgent.class.getName()+".class",
					new CreationInfo(Collections.singletonMap("person", person))).getFirstResult();
			}
		}
		numpersons	+= cnt*platforms.length;
		long end	= System.nanoTime();
		String	creation	= (""+((end-start)/1000000.0)).replace('.', ',');
		System.out.println("Started "+cnt*platforms.length+" helpline apps in "+creation+" milliseconds. Total: "+numpersons+", per platform: "+(numpersons/numplatforms));
		return creation;
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
			+ "Found Services;Settings: '-spcnt "+spcnt+" -platformcnt "+platformcnt+" -personcnt "+personcnt+"'\n");
		out.close();
	}
	
	/**
	 *  Write an entry to the output file.
	 *  @param creation	Service creation time.
	 *  @param search	Service search time.
	 *  @param numfound	Number of services found by search.
	 *  @throws IOException
	 */
	protected static void writeEntry(String creation, String search, int numfound) throws IOException
	{
		FileWriter	out	= new FileWriter(filename, true);
		out.write(numsps+";"+numplatforms+";"+numpersons+";"+creation+";"+search+";"+numfound+"\n");
		out.close();
	}
}