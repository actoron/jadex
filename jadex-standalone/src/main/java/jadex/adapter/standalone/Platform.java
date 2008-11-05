package jadex.adapter.standalone;

import jadex.adapter.base.JadexMetaAgentFactory;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.IJadexAgentFactory;
import jadex.bridge.IPlatformService;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.bridge.XMLPropertiesReader;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.javaparser.SimpleValueFetcher;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  Built-in standalone agent platform, with onyl basic features.
 */
public class Platform extends AbstractPlatform
{
	//-------- constants --------

	/** The platform type identifier. */
	public static final String PLATFORM = "platform_standalone";

	/** The platform service(s). */
	public static final String SERVICES = "services";

	/** The platform kernel(s). */
	public static final String KERNEL = "kernel";

	/** The agent factory. */
	public static final String AGENT_FACTORY = "agent_factory";

	/** The agent factory. */
	public static final String MESSAGETYPE = "messagetype";

	/** A daemon agent. */
	public static final String DAEMONAGENT = "daemonagent";

	/** An application agent. */
	public static final String AGENT = "agent";

//	/** The allowed command-line options. */
//	public static final Set COMMAND_LINE_OPTIONS;

	/** The configuration file. */
	public static final String TRANSPORT = "transport";

//	/** Starting with antother df class. */
//	public static final String DF = "df";

//	/** The allowed command-line flags. */
//	public static final Set COMMAND_LINE_FLAGS;

//	/** Starting without gui. */
//	public static final String NOGUI = "nogui";

	/** Flag for creating no ams agent. */
	//	public static final String NOAMSAGENT = "noamsagent";
	/** Flag for creating no df agent. */
	//	public static final String NODFAGENT = "nodfagent";
	/** The ams agent file. */
	//	public static final String AMSAGENTFILE = "amsagentfile";
	/** The df agent file. */
	//	public static final String DFAGENTFILE = "dfagentfile";
	/** Start the platform without transport mechanism. */
	//public static final String NOTRANSPORT = "notransport";

	/** Shut down the platform, when the last agent is killed. */
	public static final String AUTOSHUTDOWN = "autoshutdown";

	/** The platform name. */
	public static final String PLATFORMNAME = "platformname";

	/** The fallback configuration. */
	public static final String FALLBACK_CONFIGURATION = "standalone_conf.xml";

	/** Configuration entry for platform shutdown delay (time for agents to terminate gracefully). */
	public static String PLATFORM_SHUTDOWN_TIME = "platform_shutdown_time";


	/** The platform config. */
	protected Properties platconf;
	
//	static
//	{
//		COMMAND_LINE_OPTIONS = SCollection.createHashSet();
//		COMMAND_LINE_OPTIONS.add("-" + Configuration.PLATFORMNAME);
//		COMMAND_LINE_OPTIONS.add("-" + CONFIGURATION);
//		COMMAND_LINE_OPTIONS.add("-" + DF);

//		COMMAND_LINE_FLAGS = SCollection.createHashSet();
//		COMMAND_LINE_FLAGS.add("-" + NOGUI);
		//		COMMAND_LINE_FLAGS.add("-"+NOAMSAGENT);
		//		COMMAND_LINE_FLAGS.add("-"+NODFAGENT);
//		COMMAND_LINE_FLAGS.add("-" + NOTRANSPORT);
//		COMMAND_LINE_FLAGS.add("-" + AUTOSHUTDOWN);
//	}

	//-------- attributes --------

	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties configuration)
	{
		//    	long freeStartupMemory = Runtime.getRuntime().freeMemory();
		//    	long startupTime = System.currentTimeMillis();
		//    	try
		//    	{
		//	    	FileOutputStream fos = new FileOutputStream("debug.txt", false);
		//	    	PrintStream ps = new PrintStream(fos);
		//	    	 System.setErr(ps);
		//	    	 System.setOut(ps);
		//    	}
		//    	catch(Exception e)
		//    	{
		//    		e.printStackTrace();
		//    	}
		//    	Logger.info("Free Memory: " + freeStartupMemory + " bytes ("+Runtime.getRuntime().totalMemory()+" bytes)\n");


		// Save the platform name in the configuration for static access.
		//    	if(Configuration.getConfiguration().getProperty(Configuration.PLATFORMNAME)==null)
		//    		Configuration.getConfiguration().setProperty(Configuration.PLATFORMNAME, getName());

		// Save start time.
		//    	Configuration.getConfiguration().setProperty(Configuration.STARTTIME, ""+starttime);

		this.platconf = configuration.getSubproperty(PLATFORM);
		
		this.services = new LinkedHashMap();
		this.messagetypes = new LinkedHashMap();
		this.shutdowntime = platconf.getLongProperty(PLATFORM_SHUTDOWN_TIME);
		this.platformname = (String)((Property)platconf.getProperty(PLATFORMNAME)).getValue();
		if(platformname == null)
		{
			try
			{
				InetAddress iaddr = InetAddress.getLocalHost();
				//ret = iaddr.getCanonicalHostName().toLowerCase(); // works for 1.4 only.
				platformname = iaddr.getHostName().toLowerCase(); // todo: can this cause problems due to name conflicts?
			}
			catch(UnknownHostException e)
			{
				platformname = "localhost";
			}
		}
		
		SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
		fetcher.setValue("$platform", this);
		fetcher.setValue("$platformname", this.platformname);
		
		// Initialize message types.
		Property[] props = platconf.getProperties(MESSAGETYPE);
		for(int i = 0; i < props.length; i++)
		{
			messagetypes.put(props[i].getName(), props[i].getJavaObject(fetcher));
		}
		
		// Initialize services.
		props = platconf.getSubproperty(SERVICES).getProperties();
		for(int i = 0; i < props.length; i++)
		{
			Class type;
			if(props[i].getType()==null)
				type = IPlatformService.class;
			else
				type = SReflect.classForName0(props[i].getType(), null);
			if(type==null)
				throw new RuntimeException("Could not resolve service type: "+props[i].getType());
			addService(type, props[i].getName(), (IPlatformService)props[i].getJavaObject(fetcher));
		}

		this.agentfactory = createJadexAgentFactory(platconf, fetcher);
		
		this.logger = Logger.getLogger("Platform_" + getName());

		
		// Logger.info("Free Memory: " + Runtime.getRuntime().freeMemory() + " bytes");
		// Runtime.getRuntime().gc();
		// Logger.info("Free Memory: " + Runtime.getRuntime().freeMemory() + " bytes (after GC)");
		// Logger.info("Jadex-footprint:"+(freeStartupMemory-Runtime.getRuntime().freeMemory())+" bytes\n");

		//logger.info("Startup took " + (System.currentTimeMillis() - startupTime)+ " ms\n");
	}

	//-------- IPlatform methods --------

	/**
	 *  Start the platform.
	 */
	public void start()
	{
		// Start the services.
		for(Iterator it=services.keySet().iterator(); it.hasNext(); )
		{
			Object key = it.next();
			Map tmp = (Map)services.get(key);
			if(tmp!=null)
			{
				for(Iterator it2=tmp.keySet().iterator(); it2.hasNext(); )
				{
					Object key2 = it2.next();
					IPlatformService service = (IPlatformService)tmp.get(key2);
					service.start();
				}
			}
		}
		
		IAMS ams = (IAMS)getService(IAMS.class);
		if(ams!=null)
		{
			// Add ams listener if auto shutdown.
			if(platconf.getBooleanProperty(AUTOSHUTDOWN))
			{
				ams.addAMSListener(new IAMSListener()
				{
					public void agentAdded(IAMSAgentDescription desc)
					{
					}
	
					public void agentRemoved(IAMSAgentDescription desc)
					{
						((IAMS)getService(IAMS.class)).getAgentCount(new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								if(((Integer)result).intValue() <= daemonagents.size())
									shutdown(null);
							}
	
							public void exceptionOccurred(Exception exception)
							{
								getLogger().severe("Exception occurred: " + exception);
							}
						});
					}
				});
			}
	
			// Create daemon agents.
			this.daemonagents = SCollection.createLinkedHashSet();
			Property[] props = platconf.getProperties(DAEMONAGENT);
			for(int i = 0; i < props.length; i++)
			{
				ams.createAgent(props[i].getName(), props[i].getValue(), null, null, new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						AgentIdentifier agent = (AgentIdentifier)result;
						daemonagents.add(agent);
						((IAMS)getService(IAMS.class)).startAgent(agent, null);
					}
	
					public void exceptionOccurred(Exception exception)
					{
						System.err.println("Exception occurred: " + exception);
					}
				});
			}
	
			// Create application agents.
			props = platconf.getProperties(AGENT);
			for(int i = 0; i < props.length; i++)
			{
				ams.createAgent(props[i].getName(), props[i].getValue(), null, null, new IResultListener()
				{
					public void resultAvailable(Object result)
					{
						AgentIdentifier agent = (AgentIdentifier)result;
						((IAMS)getService(IAMS.class)).startAgent(agent, null);
					}
	
					public void exceptionOccurred(Exception exception)
					{
						System.err.println("Exception occurred: " + exception);
					}
				});
			}
		}
		
		platconf = null;
	}
	
	/**
	 *  Create the Jadex agent factory.
	 */
	public IJadexAgentFactory createJadexAgentFactory(Properties platconf, SimpleValueFetcher fetcher)
	{
		Properties[] kernel_props = platconf.getSubproperties(KERNEL);

		List factories = new ArrayList();
		for(int i = 0; i < kernel_props.length; i++)
		{
			Property af = kernel_props[i].getProperty(AGENT_FACTORY);
			if(af == null)
				throw new RuntimeException("Agent factory property not configured for kernel.");
			fetcher.setValue("$props", kernel_props[i]);
			IJadexAgentFactory fac = (IJadexAgentFactory)af.getJavaObject(fetcher);
			factories.add(fac);
		}
		return new JadexMetaAgentFactory(factories);
	}

	//-------- Static part --------

	/**
	 *  Start a platform with the agents specified
	 *  by the arguments in the form "name:model" or just "model".
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String conffile = FALLBACK_CONFIGURATION;
		if(args.length>0 && args[0].equals("-"+CONFIGURATION))
		{
			conffile = args[1];
			String[] tmp= new String[args.length-2];
			System.arraycopy(args, 2, tmp, 0, args.length-2);
			args = tmp;
		}
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = Platform.class.getClassLoader();
		Properties configuration = XMLPropertiesReader.readProperties(SUtil.getResource(conffile, cl), cl);
		final Platform platform = new Platform(configuration);
		platform.start();
		startAgents(args, platform);
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
	}

	/**
	 *  Parse options from command line arguments.
	 *  @param args	The arguments to scan.
	 *  @param props	The properties to be read in from options.
	 *  @return The remaining arguments, which are not options.
	 * /
	protected static String[] parseOptions(String[] args, java.util.Properties props)
	{
		int i = 0;

		// todo: fix me

		while(i<args.length && args[i].startsWith("-"))
		{
			if(COMMAND_LINE_OPTIONS.contains(args[i]))
			{
				props.setProperty(args[i++].substring(1), args[i++]);
			}
			else if(COMMAND_LINE_FLAGS.contains(args[i]))
			{
				props.setProperty(args[i++].substring(1), "true");
			}
			else
			{
				System.out.println("Argument could not be understood: "+args[i++]);
				//throw new IllegalArgumentException("Unknown argument: "+args[i]);
			}
		}

		// Return remaining arguments, i.e. agents to start.
		String[] ret = new String[args.length - i];
		if(ret.length > 0)
			System.arraycopy(args, i, ret, 0, ret.length);
		return ret;
	}*/
}

