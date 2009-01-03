package jadex.adapter.jade;

import jade.Boot;
import jade.core.AgentContainer;
import jade.core.Runtime;
import jade.wrapper.ContainerController;
import jade.wrapper.PlatformController;
import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.clock.ClockService;
import jadex.adapter.base.clock.SystemClock;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.libraryservice.LibraryService;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IClockService;
import jadex.bridge.ILibraryService;
import jadex.bridge.IPlatform;
import jadex.bridge.IPlatformService;
import jadex.bridge.Properties;
import jadex.bridge.Property;
import jadex.bridge.XMLPropertiesReader;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;
import jadex.javaparser.SimpleValueFetcher;
import jadex.javaparser.javaccimpl.JavaCCExpressionParser;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  Built-in standalone agent platform, with onyl basic features.
 */
public class Platform implements IPlatform
{
	/** The services. */
	protected Map services;
	
	/** The threadpool. */
	protected IThreadPool threadpool;
	
	/** The agent factory. */
	protected IAgentFactory agentfactory;
	
	/** The logger. */
	protected Logger logger;
	
	/**
	 *  Create a new Platform.
	 */
	public Platform()
	{
		this.logger = Logger.getLogger("Platform_" + getName());
		this.threadpool = ThreadPoolFactory.createThreadPool();
		this.services = new HashMap();
		services.put(ILibraryService.class, new LibraryService());
		services.put(IAMS.class, new AMS(this));
		services.put(IDF.class, new DF(this));
		services.put(IClockService.class, new ClockService(new SystemClock("system", 1000, threadpool), this));
	}

	//-------- IPlatform methods --------

	/**
	 *  Start the platform.
	 */
	public void start()
	{
		// Start Jade platform with platform agent
		// This agent make accessible the platform controller
		new Boot(new String[]{"-gui", "platform:jadex.adapter.jade.PlatformAgent"});
		IAMS ams = (IAMS)getService(IAMS.class);
		ams.createAgent("jcc", "jadex/tools/jcc/JCC.agent.xml", null, null, null);
	}
	
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return "todo";
	}
	
	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 * /
	public Collection getServices(Class type)
	{
	}*/
	
	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type, String name)
	{
		// Hack!
		return services.get(type);
	}
	
	/**
	 *  Get a platform service.
	 *  @param type The service interface/type.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type)
	{
		return services.get(type);
	}
		
	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	// Todo: remove from external platform interface
	public IAgentFactory getAgentFactory()
	{
		if(agentfactory==null)
		{
			List facs = new ArrayList();
		
			ILibraryService ls = (ILibraryService)getService(ILibraryService.class); 
			try
			{
				Class bdifac = SReflect.findClass("jadex.bdi.interpreter.BDIAgentFactory", 
					null, ls.getClassLoader());
				Constructor con = bdifac.getConstructor(new Class[]{Map.class, IPlatform.class});
				Class execl = SReflect.findClass("jadex.bdi.runtime.JavaStandardPlanExecutor", 
					null, ls.getClassLoader());
				Constructor execon = execl.getConstructor(new Class[]{IThreadPool.class});
				Object exe = execon.newInstance(new Object[]{this.getService(jadex.adapter.base.ThreadPoolService.class)});
				Map args = SUtil.createHashMap(
					new String[]
					{
						"messagetype_fipa", 
						"planexecutor_standard", 
						"standard.timeout", 
						"tooladapter.introspector",
						"microplansteps"
					},
					new Object[]
					{
						new jadex.adapter.base.fipa.FIPAMessageType(), 
						exe,
						new java.lang.Long(10000),
						SReflect.findClass("jadex.tools.introspector.IntrospectorAdapter", null, ls.getClassLoader()),
						Boolean.TRUE
					}
				);
				
				IAgentFactory af = (IAgentFactory)con.newInstance(new Object[]{args, this});
				facs.add(af);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		
			agentfactory = new MetaAgentFactory(facs);
		}
		
		return agentfactory;
	}

	//-------- Static part --------

	/** The container controller. */
	protected PlatformController controller;
	
	/**
	 *  Keep platform from being garbage collected, when created using main().
	 *  Useful for debugging, profiling etc.
	 */
	private static Platform	platform;

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
		System.out.println("platform controller available: "+controller);
	}
	
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
	 */
	public static void setPlatform(Platform platform)
	{
		Platform.platform = platform;
	}
	
	/**
	 *  Start a platform with the agents specified
	 *  by the arguments in the form "name:model" or just "model".
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		platform = new Platform();
		platform.start();
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
				
		Thread	gc	= new Thread(new Runnable()
		{
			public void run()
			{
				while(true)
				{
					try
					{
						Thread.sleep(1100);
						System.gc();
						Thread.sleep(1100);
						System.runFinalization();
					}
					catch(Exception e){}
				}
			}
		});
		gc.setDaemon(true);
		gc.start();
	}
}

