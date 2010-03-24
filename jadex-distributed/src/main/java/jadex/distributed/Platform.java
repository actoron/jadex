package jadex.distributed;

import jadex.standalone.AbstractPlatform; // bei dieser Platform zu importieren, da bei der 'normalen' Platform die AbstractPlatform im gleichen package ist
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;
import jadex.service.IService;
import jadex.service.IServiceContainer;
import jadex.service.PropertiesXMLHelper;
import jadex.service.library.ILibraryService;

import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

/**
 *  Built-in standalone component platform.
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

	/** A lib path. */
	public static final String LIBPATH = PLATFORM+SEPARATOR+"libpath";
	
	/** A daemon component. */
	public static final String DAEMONCOMPONENT = PLATFORM+SEPARATOR+"daemoncomponent";

	/** An application component. */
	public static final String COMPONENT = PLATFORM+SEPARATOR+"component";

	/** An application component. */
	public static final String APPLICATION = PLATFORM+SEPARATOR+"application";
	
	/** An component argument. */
	public static final String ARGUMENT = "argument";

	/** An component argument set. */
	public static final String ARGUMENTSET = "argumentset";
	
	/** An component model. */
	public static final String MODEL = "model";
	
	/** An component config. */
	public static final String CONFIG = "config";
	
	/** Shut down the platform, when the last component is killed. */
	public static final String AUTOSHUTDOWN = PLATFORM+SEPARATOR+"autoshutdown";

	/** The platform name. */
	public static final String PLATFORMNAME = PLATFORM+SEPARATOR+"platformname";

	/** Configuration entry for platform shutdown delay (time for components to terminate gracefully). */
	public static String PLATFORM_SHUTDOWN_TIME = PLATFORM+SEPARATOR+"platform_shutdown_time";

	/** The fallback configuration for basic services. */
	public static final String FALLBACK_SERVICES_CONFIGURATION = "jadex/standalone/services_conf.xml";

	/** The fallback configuration for standard agents (cms/df/jcc). */
	public static final String FALLBACK_AGENTS_CONFIGURATION = "jadex/standalone/platformagents_conf.xml";

	/** The fallback configuration for application kernel. */
	public static final String FALLBACK_APPLICATION_CONFIGURATION = "jadex/application/kernel_application_conf.xml";
	
	/** The fallback configuration for bdi kernel. */
	public static final String FALLBACK_BDI_CONFIGURATION = "jadex/bdi/kernel_bdi_conf.xml";
	
	/** The fallback configuration for micro kernel. */
	public static final String FALLBACK_MICRO_CONFIGURATION = "jadex/micro/kernel_micro_conf.xml";

	/** The fallback configuration for bpmn kernel. */
	public static final String FALLBACK_BPMN_CONFIGURATION = "jadex/bpmn/kernel_bpmn_conf.xml";

	/** The fallback configuration for bdibpmn kernel. */
	public static final String FALLBACK_BDIBPMN_CONFIGURATION = "jadex/bdibpmn/kernel_bdibpmn_conf.xml";

	/** The configuration. */
	protected Properties[] configurations;

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
	protected static Properties[] readProperties(String[] conffiles, ClassLoader classloader) throws Exception
	{
		Properties[] props = new Properties[conffiles.length];
		for(int i=0; i<props.length; i++)
		{
			props[i] = (Properties)PropertiesXMLHelper.getPropertyReader()
				.read(SUtil.getResource(conffiles[i], classloader), classloader, null);
		}
		
		return props;
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

		this.configurations = configurations;
		
//		this.services = new LinkedHashMap();
//		this.messagetypes = new LinkedHashMap();
		this.shutdowntime = Properties.getLongProperty(configurations, PLATFORM_SHUTDOWN_TIME);
		Property pname = (Property)Properties.getLatestProperty(configurations, PLATFORMNAME);
		String	name = pname!=null? pname.getValue(): null;
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
		
		// Initialize services.
		init(Properties.getSubproperties(configurations, SERVICES), fetcher, parent);

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
						service.startService();
					}
				}
			}
		}
		
		// Initialize the lib service with extra paths
		Property[] libpaths = Properties.getProperties(configurations, LIBPATH);
		if(libpaths.length>0)
		{
			ILibraryService ls = (ILibraryService)getService(ILibraryService.class);
			if(ls==null)
				throw new RuntimeException("No library service available for setting lib paths.");

			SimpleValueFetcher	fetcher	= new SimpleValueFetcher();
			fetcher.setValue("$platform", this);
			fetcher.setValue("$platformname", getName());
			
			for(int i=0; i<libpaths.length; i++)
			{
				Object entry = SJavaParser.evaluateExpression(libpaths[i].getValue(), fetcher);
//				System.out.println("Adding: "+entry);
				try
				{
					if(entry instanceof URL)
						ls.addURL((URL)entry);
					else //if(entry instanceof String)
						ls.addPath((String)entry);
				}
				catch(Exception e)
				{
					System.out.println("Could not add lib path: "+entry);
					e.printStackTrace();
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
	
			// Create daemon components.
			this.daemoncomponents = SCollection.createLinkedHashSet();
			Property[] props = Properties.getProperties(configurations, DAEMONCOMPONENT);
//			System.out.println("starting: "+props.length);
			for(int i = 0; i < props.length; i++)
			{
//				System.out.println("starting: "+props[i].getName());
				createComponent(props[i].getName(), props[i].getValue(), null, null, true);
			}
			Properties[] subprops = Properties.getSubproperties(configurations, DAEMONCOMPONENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, true);
			}
			
	
			// Create application components.
			props = Properties.getProperties(configurations, COMPONENT);
			for(int i = 0; i < props.length; i++)
			{
				createComponent(props[i].getName(), props[i].getValue(), null, null, false);
			}
			subprops = Properties.getSubproperties(configurations, COMPONENT);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, false);
			}
			
			// Create applications.
			props = Properties.getProperties(configurations, APPLICATION);
			for(int i = 0; i < props.length; i++)
			{
				createComponent(props[i].getName(), props[i].getValue(), null, null, false);
			}
			subprops = Properties.getSubproperties(configurations, APPLICATION);
			for(int i = 0; i < subprops.length; i++)
			{
				Map args = getArguments(subprops[i]);
				Property model = subprops[i].getProperty(MODEL);
				Property config = subprops[i].getProperty(CONFIG);
				createComponent(subprops[i].getName(), model.getValue(), config!=null? config.getValue(): null, args, false);
			}
		}
		
		configurations = null;
	}
	
	/**
	 *  Get a component's arguments.
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
	 *  Start a platform with the components specified
	 *  by the arguments in the form "name:model" or just "model".
	 */
	public static void main(String[] args) throws Exception
	{
		//System.out.println(System.getProperty("user.dir"));
		/* TODO der Pfad zur management.properties für JMX durch die JVM Option -D in der Client.launch
		      funktioniert zwar in diesem develope environment, muss aber noch für ein production
		      environment angepasst werden; das mit target/... ist einfach nur ugly
		      
		      mit diesem ugly weg gibt es zwei möglichkeiten der JVM die JMX config file zu geben
		       - -Dcom.sun.management.config.file=target/classes/jadex/distributed/config/jmx/management.properties
		       - -Dcom.sun.management.config.file=src/main/java/jadex/distributed/config/jmx/management.properties
		       
		       die management.properties kümmert sich um die komplette konfiguration von JMX,
		       inklusive -Dcom.sun.management.jmxremote.port=<port-number> und andere
		   
		   TODO die zweite:
		   man kann bei JMX die Passwortabfrage über SSL deaktivieren, was ich hier in dem
		   development environment nun auch getan habe
		*/
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
				FALLBACK_AGENTS_CONFIGURATION,
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

