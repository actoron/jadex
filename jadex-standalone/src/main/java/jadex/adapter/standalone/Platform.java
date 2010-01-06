package jadex.adapter.standalone;

import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.PropertiesXMLHelper;
import jadex.service.library.ILibraryService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Built-in standalone agent platform.
 */
public class Platform extends AbstractPlatform
{
	//-------- constants --------

	/** The separator. */
	public static final String SEPARATOR = ".";
	
	/** The platform type identifier. */
	public static final String PLATFORM = "platform_standalone";

	/** The platform service(s). */
	public static final String SERVICES = PLATFORM+SEPARATOR+"services";
	
	/** The platform agent(s). */
//	public static final String AGENTS = PLATFORM+SEPARATOR+"agents";

//	/** The platform kernel(s). */
//	public static final String KERNEL = "platform_standalone.kernel";

//	/** The agent factory. */
//	public static final String AGENT_FACTORY = "agent_factory";

//	/** The application factory. */
//	public static final String APPLICATION_FACTORY = "application_factory";
	
//	/** The agent factory. */
//	public static final String MESSAGETYPE = "messagetype";

	/** A daemon agent. */
	public static final String DAEMONAGENT = PLATFORM+SEPARATOR+"daemonagent";

	/** An application agent. */
	public static final String AGENT = PLATFORM+SEPARATOR+"agent";

	/** An application agent. */
	public static final String APPLICATION = PLATFORM+SEPARATOR+"application";
	
	/** An agent argument. */
	public static final String ARGUMENT = "argument";

	/** An agent argument set. */
	public static final String ARGUMENTSET = "argumentset";
	
	/** An agent model. */
	public static final String MODEL = "model";
	
	/** An agent config. */
	public static final String CONFIG = "config";
	
	/** Shut down the platform, when the last agent is killed. */
	public static final String AUTOSHUTDOWN = PLATFORM+SEPARATOR+"autoshutdown";

	/** The platform name. */
	public static final String PLATFORMNAME = PLATFORM+SEPARATOR+"platformname";

	/** Configuration entry for platform shutdown delay (time for agents to terminate gracefully). */
	public static String PLATFORM_SHUTDOWN_TIME = PLATFORM+SEPARATOR+"platform_shutdown_time";

	/** The fallback configuration. */
	public static final String FALLBACK_CONFIGURATION = "jadex/adapter/standalone/standalone_conf.xml";

	/** The configuration. */
	protected Properties conf;

	//-------- attributes --------

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
	 *  Read several property files and merge them.
	 */
	protected static Properties readProperties(String[] conffiles, ClassLoader classloader) throws Exception
	{
		Properties[] props = new Properties[conffiles.length];
		for(int i=0; i<props.length; i++)
		{
			props[i] = (Properties)PropertiesXMLHelper.getPropertyReader()
				.read(SUtil.getResource(conffiles[i], classloader), classloader, null);
		}
		
		// Unify properties
		Properties ret = props[0];
		for(int i=1; i<props.length; i++)
		{
			ret.addProperties(props[i]);
		}
		
		return ret;
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties configuration)
	{
		this(configuration, null);
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties configuration, IServiceContainer parent)
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

		this.conf = configuration;//;.getSubproperty(PLATFORM);
		
//		this.services = new LinkedHashMap();
//		this.messagetypes = new LinkedHashMap();
		this.shutdowntime = conf.getLongProperty(PLATFORM_SHUTDOWN_TIME);
		String	name = (String)((Property)conf.getProperty(PLATFORMNAME)).getValue();
		if(name == null)
		{
			try
			{
				InetAddress iaddr = InetAddress.getLocalHost();
				//ret = iaddr.getCanonicalHostName().toLowerCase(); // works for 1.4 only.
				name = iaddr.getHostName().toLowerCase(); // todo: can this cause problems due to name conflicts?
			}
			catch(UnknownHostException e)
			{
				name = "localhost";
			}
		}
		setName(name);
		
		SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
		fetcher.setValue("$platform", this);
		fetcher.setValue("$platformname", name);
		
//		// Initialize message types.
//		Property[] props = platconf.getProperties(MESSAGETYPE);
//		for(int i = 0; i < props.length; i++)
//		{
//			messagetypes.put(props[i].getName(), SJavaParser.evaluateExpression(props[i].getValue(), fetcher));
//		}
		
		// Initialize services.
//		props = platconf.getSubproperty(SERVICES).getProperties();
		init(conf.getSubproperties(SERVICES), fetcher, parent);
		
//		for(int i = 0; i < props.length; i++)
//		{
//			Class type;
//			if(props[i].getType()==null)
//				type = IService.class;
//			else
//				type = SReflect.classForName0(props[i].getType(), null);
//			if(type==null)
//				throw new RuntimeException("Could not resolve service type: "+props[i].getType());
//			addService(type, props[i].getName(), (IService)SJavaParser.evaluateExpression(props[i].getValue(), fetcher));
//		}

//		this.agentfactory = createAgentFactory(platconf, fetcher);
		
//		this.appfactory = createApplicationFactory(platconf, fetcher);
		
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
		if(services!=null)
		{
			for(Iterator it=services.keySet().iterator(); it.hasNext(); )
			{
				Object key = it.next();
				Map tmp = (Map)services.get(key);
				if(tmp!=null)
				{
					for(Iterator it2=tmp.keySet().iterator(); it2.hasNext(); )
					{
						Object key2 = it2.next();
						IService service = (IService)tmp.get(key2);
						service.start();
					}
				}
			}
		}
		
//		IAMS ams = (IAMS)getService(IAMS.class);
//		if(getAMSService()!=null)
		{
			/*
			// Add ams listener if auto shutdown.
			if(platconf.getBooleanProperty(AUTOSHUTDOWN))
			{
				getAMSService().addComponentListener(new IComponentListener()
				{
					public void componentAdded(Object desc)
					{
					}
	
					public void componentRemoved(Object desc)
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
			*/
	
			// Create daemon agents.
			this.daemonagents = SCollection.createLinkedHashSet();
			Property[] props = conf.getProperties(DAEMONAGENT);
//			System.out.println("starting: "+props.length);
			for(int i = 0; i < props.length; i++)
			{
//				System.out.println("starting: "+props[i].getName());
				createComponent(props[i].getName(), props[i].getValue(), null, null, true);
			}
			Properties[] subprops = conf.getSubproperties(DAEMONAGENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, true);
			}
	
			// Create application agents.
			props = conf.getProperties(AGENT);
			for(int i = 0; i < props.length; i++)
			{
				createComponent(props[i].getName(), props[i].getValue(), null, null, false);
			}
			subprops = conf.getSubproperties(AGENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, false);
			}
			
			// Create applications.
			props = conf.getProperties(APPLICATION);
			for(int i = 0; i < props.length; i++)
			{
				createComponent(props[i].getName(), props[i].getValue(), null, null, false);
			}
			subprops = conf.getSubproperties(APPLICATION);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, false);
			}
		}
		
		conf = null;
	}
	
	/**
	 *  Get an agent's arguments.
	 *  @param props The argument properties.
	 *  @return The map of arguments.
	 */
	protected Map getArguments(Properties props)
	{
		Map arguments = new HashMap();
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$platform", platform);
		
		Property[] args = props.getProperties(ARGUMENT);
		for(int i = 0; i < args.length; i++)
		{
			Object arg = null;
			try
			{
				ILibraryService ls = (ILibraryService)getService(ILibraryService.class);
				arg = new JavaCCExpressionParser().parseExpression(args[i].getValue(), null, null, ls.getClassLoader()).getValue(fetcher);
				arguments.put(args[i].getName(), arg);
			}
			catch(Exception e)
			{
				System.out.println("Argument could not be parsed: "+args[i].getValue());
			}
		}
		Property[] argsets = props.getProperties(ARGUMENTSET);
		for(int i = 0; i < argsets.length; i++)
		{
			Object arg = null;
			try
			{
				ILibraryService ls = (ILibraryService)getService(ILibraryService.class);
				arg = new JavaCCExpressionParser().parseExpression(argsets[i].getValue(), null, null, ls.getClassLoader()).getValue(fetcher);
				arguments.put(argsets[i].getName(), arg);
			}
			catch(Exception e)
			{
				System.out.println("Argument could not be parsed: "+argsets[i].getValue());
			}
		}
		
		return arguments.size()>0? arguments: null;
	}

	//-------- Static part --------
	
	/**
	 *  Keep platform from being garbage collected, when created using main().
	 *  Useful for debugging, profiling etc.
	 */
	private static Platform	platform;

	/**
	 *  Start a platform with the agents specified
	 *  by the arguments in the form "name:model" or just "model".
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String[] conffiles;
		if(args[0].equals("-"+CONFIGURATION))
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
			conffiles = new String[]{FALLBACK_CONFIGURATION};
		}
		
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = Platform.class.getClassLoader();
		platform = new Platform(conffiles, cl);
		platform.start();
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
		
//		Thread	gc	= new Thread(new Runnable()
//		{
//			public void run()
//			{
//				while(true)
//				{
//					try
//					{
//						Thread.sleep(5000);
//						System.gc();
//						Thread.sleep(1000);
//						System.runFinalization();
//					}
//					catch(Exception e){}
//				}
//			}
//		});
//		gc.setDaemon(true);
//		gc.start();
	}
}

