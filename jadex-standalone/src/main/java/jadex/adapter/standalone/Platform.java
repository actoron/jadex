package jadex.adapter.standalone;

import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IApplicationFactory;
import jadex.bridge.ILibraryService;
import jadex.bridge.IPlatformService;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.xml.AbstractInfo;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

	/** The application factory. */
	public static final String APPLICATION_FACTORY = "application_factory";
	
	/** The agent factory. */
	public static final String MESSAGETYPE = "messagetype";

	/** A daemon agent. */
	public static final String DAEMONAGENT = "daemonagent";

	/** An application agent. */
	public static final String AGENT = "agent";
	
	/** An agent argument. */
	public static final String ARGUMENT = "argument";

	/** An agent argument set. */
	public static final String ARGUMENTSET = "argumentset";
	
	/** An agent model. */
	public static final String MODEL = "model";
	
	/** An agent config. */
	public static final String CONFIG = "config";
	
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
	public static final String FALLBACK_CONFIGURATION = "jadex/adapter/standalone/standalone_conf.xml";

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

		this.agentfactory = createAgentFactory(platconf, fetcher);
		
		this.appfactory = createApplicationFactory(platconf, fetcher);
		
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
				createAgent(props[i].getName(), props[i].getValue(), null, null, true);
			}
			Properties[] subprops = platconf.getSubproperties(DAEMONAGENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createAgent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, true);
			}
	
			// Create application agents.
			props = platconf.getProperties(AGENT);
			for(int i = 0; i < props.length; i++)
			{
				createAgent(props[i].getName(), props[i].getValue(), null, null, false);
			}
			subprops = platconf.getSubproperties(AGENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createAgent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, false);
			}
		}
		
		platconf = null;
	}
	
	/**
	 *  Create the agent factory.
	 */
	public IAgentFactory createAgentFactory(Properties platconf, SimpleValueFetcher fetcher)
	{
		Properties[] kernel_props = platconf.getSubproperties(KERNEL);

		List factories = new ArrayList();
		for(int i=0; i<kernel_props.length; i++)
		{
			Property af = kernel_props[i].getProperty(AGENT_FACTORY);
			if(af == null)
				throw new RuntimeException("Agent factory property not configured for kernel.");
			fetcher.setValue("$props", kernel_props[i]);
			IAgentFactory fac = (IAgentFactory)af.getJavaObject(fetcher);
			factories.add(fac);
		}
		return new MetaAgentFactory(factories);
	}
	
	/**
	 *  Create the application factory.
	 */
	public IApplicationFactory createApplicationFactory(Properties platconf, SimpleValueFetcher fetcher)
	{
		Property af = platconf.getProperty(APPLICATION_FACTORY);
		IApplicationFactory ret = (IApplicationFactory)af.getJavaObject(fetcher);
		return ret;
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
//		Properties configuration = XMLPropertiesReader.readProperties(SUtil.getResource(conffile, cl), cl);
		Properties configuration = (Properties)getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		platform = new Platform(configuration);
		platform.start();
		startAgents(args, platform);
		
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
	
	public static Set typeinfos;	
	static
	{
		typeinfos = new HashSet();
		typeinfos.add(new TypeInfo(null, "properties", Properties.class, null, null, 
			new BeanAttributeInfo[]{new BeanAttributeInfo("schemaLocation", null, AttributeInfo.IGNORE_READWRITE)}, null, null,
			new SubobjectInfo[]
			{
				new SubobjectInfo(new BeanAttributeInfo("property", "properties")), 
				new SubobjectInfo(new BeanAttributeInfo("properties", "subproperties"))
			}
		));
		
		typeinfos.add(new TypeInfo(null, "property", Property.class, null, new BeanAttributeInfo(null, "value")));
	}
	public static jadex.commons.xml.writer.Writer writer;
	public static jadex.commons.xml.reader.Reader reader;
	
	/**
	 *  Get the xml properties writer.
	 */
	public static jadex.commons.xml.writer.Writer getPropertyWriter()
	{
		if(writer==null)
		{
			writer = new jadex.commons.xml.writer.Writer(new BeanObjectWriterHandler(typeinfos));
		}
		return writer;
	}
	
	/**
	 *  Get the xml properties reader.
	 */
	public static jadex.commons.xml.reader.Reader getPropertyReader()
	{
		if(reader==null)
		{
			reader = new jadex.commons.xml.reader.Reader(new BeanObjectReaderHandler(), typeinfos);
		}
		return reader;
	}
}

