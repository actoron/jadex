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
	private static int	platformcnt	= -2;
	
	/** The number of persons (components) to create on each (new) platform in each round. */
	private static int	personcnt	= 500;
	
	//-------- counters for creates elements --------
	
	/** Number of created SPs. */
	private static int	numsps	= 0;

	/** Number of created platforms. */
	private static int	numplatforms	= 0;
	
	/** Number of created persons. */
	private static int	numpersons	= 0;

	/** Output file name. */
	private static final String filename = "eval"+System.currentTimeMillis()+".csv";

	
	//-------- methods --------
	
	public static void main(String[] args)	throws Exception
	{
		IPlatformConfiguration config = parseArgs(args);

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
			
			System.gc();
			String creation = createPersons(platforms, personcnt);
			
//			Thread.sleep(5000);	// Wait for registration?

			// Search for first person to check if searches get slower.
			System.gc();
			long	start	= System.nanoTime();
			Collection<IHelpline>	found	= SServiceProvider.getTaggedServices(platforms[0], IHelpline.class, RequiredServiceInfo.SCOPE_NETWORK, "person0").get();
			long	end	= System.nanoTime();
			String	search	= (""+((end-start)/1000000)).replace('.', ',');
			int	numfound	= found.size();
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
		config.setSimulation(false);	// Todo: fix sim delay in registry!?
		config.setValue("spcnt", spcnt);
		config.setValue("platformcnt", platformcnt);
		config.setValue("personcnt", personcnt);
		config.enhanceWith(Starter.processArgs(args));
		spcnt	= (Integer) config.getArgs().get("spcnt");
		platformcnt	= (Integer) config.getArgs().get("platformcnt");
		personcnt	= (Integer) config.getArgs().get("personcnt");
		return config;
	}

	/**
	 *  Create a number of SP platforms.
	 *  @param config	The platform config.
	 *  @param cnt	The number of platforms
	 */
	protected static void createSPs(IPlatformConfiguration config, int cnt)
	{
		config.setSuperpeer(true);	// hack???
		createPlatforms(config, cnt, "SP");
		config.setSuperpeer(false);
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
		IExternalAccess[]	ret	= createPlatforms(config, cnt, "helpline");
		numplatforms	+= cnt;
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
				cms.createComponent(HelplineAgent.class.getName()+".class",
					new CreationInfo(Collections.singletonMap("person", (Object)("person0")))).getFirstResult();
//						new CreationInfo(Collections.singletonMap("person", (Object)("person"+(offset+j))))).getFirstResult();
				numpersons++;
			}
		}
		long end	= System.nanoTime();
		String	creation	= (""+((end-start)/1000000));//.replace('.', ',');
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
		FileWriter	out	= new FileWriter(filename, true);
		out.write("# of SPs;# of Platforms;# of Services;Service Creation Time;Service Search Time;Found Services;Settings: '-spcnt "+spcnt+" -platformcnt "+platformcnt+" -personcnt "+personcnt+"'\n");
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