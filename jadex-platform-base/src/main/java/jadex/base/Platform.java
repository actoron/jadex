package jadex.base;

import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.SUtil;
import jadex.commons.ThreadSuspendable;
import jadex.commons.concurrent.DefaultResultListener;
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

	/** The configuration file. */
	public static final String CONFIGURATION = "conf";
	
	/** The separator. */
	public static final String SEPARATOR = ".";
	
	/** The platform type identifier. */
	public static final String PLATFORM = "platform_standalone";

	/** The platform service(s). */
	public static final String SERVICES = PLATFORM+SEPARATOR+"services";

	/** A lib path. */
	public static final String LIBPATH = PLATFORM+SEPARATOR+"libpath";
	
	/** An application component. */
	public static final String COMPONENT = PLATFORM+SEPARATOR+"component";

	/** An component argument. */
	public static final String ARGUMENT = "argument";

	/** An component argument set. */
	public static final String ARGUMENTSET = "argumentset";
	
	/** An component model. */
	public static final String MODEL = "model";
	
	/** An component config. */
	public static final String CONFIG = "config";
	
	/** An component number. */
	public static final String NUMBER = "number";
	
	/** An component master flag. */
	public static final String MASTER = "master";
	
	/** An component daemon flag. */
	public static final String DAEMON = "daemon";
	
	/** An component suspend flag. */
	public static final String SUSPEND = "suspend";
	
	/** Shut down the platform, when the last component is killed. */
	public static final String AUTOSHUTDOWN = PLATFORM+SEPARATOR+"autoshutdown";

	/** The platform name. */
	public static final String PLATFORMNAME = PLATFORM+SEPARATOR+"platformname";

	/** Configuration entry for platform shutdown delay (time for components to terminate gracefully). */
	public static String PLATFORM_SHUTDOWN_TIME = PLATFORM+SEPARATOR+"platform_shutdown_time";

	/** The fallback configuration for basic services. */
	public static final String FALLBACK_SERVICES_CONFIGURATION = "jadex/standalone/services_conf.xml";

	/** The fallback configuration for standard components (cms/df/jcc). */
	public static final String FALLBACK_STANDARDCOMPONENTS_CONFIGURATION = "jadex/standalone/platformcomponents_conf.xml";

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
	
	/** The auto shutdown flag. */
	protected boolean autoshutdown;

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
		this.autoshutdown = Properties.getBooleanProperty(configurations, AUTOSHUTDOWN);
		
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
	public IFuture start()
	{
		// Start the services. ??? why not call super?
	
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
			ILibraryService ls = (ILibraryService)getService(ILibraryService.class).get(new ThreadSuspendable());	// Hack!!!
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
		
		getService(IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService	ces	= (IComponentManagementService)result;
				getService(ILibraryService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Create components.
						Property[] props = Properties.getProperties(configurations, COMPONENT);
						for(int i = 0; i < props.length; i++)
						{
							ces.createComponent(props[i].getName(), props[i].getValue(), null, null);
						}
						Properties[] subprops = Properties.getSubproperties(configurations, COMPONENT);
						for(int i = 0; i < subprops.length; i++)
						{
							Map args = getArguments(subprops[i], (ILibraryService)result);
							Property model = subprops[i].getProperty(MODEL);
							Property config = subprops[i].getProperty(CONFIG);
							int number = subprops[i].getIntProperty(NUMBER);
							boolean master = subprops[i].getBooleanProperty(MASTER);
							boolean suspend = subprops[i].getBooleanProperty(SUSPEND);
							boolean daemon = subprops[i].getBooleanProperty(DAEMON);
							
							CreationInfo cinfo = new CreationInfo(config!=null? config.getValue(): null, args, null, suspend, master, daemon);
							if(number>1)
							{
								for(int j=0; j<number; j++)
								{
									ces.createComponent(null, model.getValue(), cinfo, null);
								}
							}
							else
							{
								ces.createComponent(subprops[i].getName(), model.getValue(), cinfo, null);
							}
						}
					}
				});
			}
		});
		
		configurations = null;
		
		return null; // todo: hack
	}
	
	/**
	 *  Get a component's arguments.
	 *  @param props The argument properties.
	 *  @return The map of arguments.
	 */
	protected Map getArguments(Properties props, ILibraryService ls)
	{
		Map arguments = new HashMap();
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$platform", this);
		
		Property[] args = props.getProperties(ARGUMENT);
		for(int i = 0; i < args.length; i++)
		{
			Object arg = null;
			try
			{
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

	/**
	 *  Test if platform is in autoshutdown mode.
	 *  @return True, if autoshutdown.
	 */
	public boolean isAutoShutdown()
	{
//		System.out.println("test: "+Properties.getBooleanProperty(configurations, AUTOSHUTDOWN));
//		return Properties.getBooleanProperty(configurations, AUTOSHUTDOWN);
		return autoshutdown;
	}
}

