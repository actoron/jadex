package jadex.adapter.jade;

import jade.Boot;
import jade.core.AID;
import jade.wrapper.PlatformController;
import jadex.commons.Properties;
import jadex.service.IServiceContainer;

/**
 *  Built-in JADE platform.
 */
public class Platform extends jadex.base.Platform
{
	//-------- constants --------

	/** The fallback configuration for basic services. */
	public static final String FALLBACK_SERVICES_CONFIGURATION = "jadex/adapter/jade/services_conf.xml";

	/** The fallback configuration for standard components (cms/df/jcc). */
	public static final String FALLBACK_STANDARDCOMPONENTS_CONFIGURATION = "jadex/adapter/jade/platformcomponents_conf.xml";

	//-------- attributes --------
	
	/** The platform agent. */
	protected AID platformagent;
	
	//-------- constructors --------

	/**
	 *  Create a new Platform.
	 */
	public Platform(String conffile, ClassLoader classloader) throws Exception
	{
		this(new String[]{conffile}, classloader);
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(String[] conffiles, ClassLoader classloader) throws Exception
	{
		this(readProperties(conffiles, classloader));
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties[] configurations)
	{
		this(configurations, null);
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties[] configurations, IServiceContainer parent)
	{
		super(configurations, parent);
	}

	//-------- methods --------
	
	/**
	 *  Start the platform.
	 */
	public void start()
	{
		super.start();
		
		// Start Jade platform with platform agent
		// This agent make accessible the platform controller
		new Boot(new String[]{"-gui", "platform:jadex.adapter.jade.PlatformAgent"});
		// Hack! Busy waiting for platform agent init finished.
		while(platformagent==null)
		{
			System.out.print(".");
			try
			{
				Thread.currentThread().sleep(100);
			}
			catch(Exception e)
			{
			}
		}
	}
	
	/**
	 *  Keep platform from being garbage collected, when created using main().
	 *  Useful for debugging, profiling etc.
	 */
	private static Platform	platform;
	
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String[] conffiles;
		if(args.length>0 && args[0].equals("-"+CONFIGURATION))
		{
			conffiles = new String[args.length-1];
			System.arraycopy(args, 1, conffiles, 0, args.length-1);
		}
		else if(args.length>0)
		{
			conffiles = args;
		}
		else
		{
			conffiles = new String[]
			{
				FALLBACK_SERVICES_CONFIGURATION,
				FALLBACK_STANDARDCOMPONENTS_CONFIGURATION,
				FALLBACK_APPLICATION_CONFIGURATION,
				FALLBACK_BDI_CONFIGURATION,
				FALLBACK_MICRO_CONFIGURATION,
				FALLBACK_BPMN_CONFIGURATION,
				FALLBACK_BDIBPMN_CONFIGURATION
			};
		}
		
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = Platform.class.getClassLoader();
		platform = new Platform(conffiles, cl);
		platform.start();
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
	
	//-------- Static part --------

	/** The container controller. */
	protected PlatformController controller;
	
	/**
	 *  Get the container.
	 *  @return The container.
	 */
	public PlatformController getPlatformController()
	{
		return controller;
	}

	/**
	 *  Set the container.
	 *  @param container The container.
	 */
	public void setPlatformController(PlatformController controller)
	{
		this.controller = controller;
//		System.out.println("platform controller available: "+controller);
	}
	
	/**
	 *  Set the platformagent.
	 *  @param platformagent The platform agent.
	 */
	public void setPlatformAgent(AID platformagent)
	{
		this.platformagent = platformagent;
	}
	
	/**
	 *  Get the platform agent.
	 *  @return The platform agent.
	 */
	public AID getPlatformAgent()
	{
		return this.platformagent;
	}
	
	/**
	 *  Set the platformagent.
	 *  @param platformagent The platform agent.
	 * /
	public void setPlatformAgentController(AgentController pacontroller)
	{
		this.pacontroller = pacontroller;
	}*/
	
	/**
	 *  Get the platform agent.
	 *  @return The platform agent.
	 * /
	public AgentController getPlatformAgentController()
	{
		return this.pacontroller;
	}*/

	/**
	 *  Get platform.
	 *  @param name The name.
	 *  @return The platform
	 *  // todo: make 
	 */
	public static Platform getPlatform()
	{
		return platform;
	}

	/**
	 *  Set the platform.
	 *  @param platform The platform.
	 * /
	public static void setPlatform(Platform platform)
	{
		Platform.platform = platform;
	}*/
}



