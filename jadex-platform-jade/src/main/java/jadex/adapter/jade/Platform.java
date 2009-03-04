package jadex.adapter.jade;

import jade.Boot;
import jade.core.AID;
import jade.wrapper.AgentController;
import jade.wrapper.PlatformController;
import jadex.adapter.base.DefaultResultListener;
import jadex.adapter.base.ISimulationService;
import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.SimulationService;
import jadex.adapter.base.ThreadPoolService;
import jadex.adapter.base.agr.MAGRSpaceType;
import jadex.adapter.base.appdescriptor.ApplicationFactory;
import jadex.adapter.base.clock.ClockService;
import jadex.adapter.base.clock.SystemClock;
import jadex.adapter.base.execution.IExecutionService;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IDF;
import jadex.adapter.base.fipa.SFipa;
import jadex.adapter.base.libraryservice.LibraryService;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationFactory;
import jadex.bridge.IClockService;
import jadex.bridge.ILibraryService;
import jadex.bridge.IMessageService;
import jadex.bridge.IPlatform;
import jadex.bridge.IPlatformService;
import jadex.bridge.MessageType;
import jadex.commons.ICommand;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
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
	
	/** The application factory. */
	protected IApplicationFactory appfactory;
	
	/** The logger. */
	protected Logger logger;
	
	/** The platform agent. */
	protected AID platformagent;
	
	/** The platform agent controller. */
	protected AgentController pacontroller;
	
	
	/**
	 *  Create a new Platform.
	 */
	public Platform()
	{
		// Hack!!!
		platform = this;
		
		this.threadpool = ThreadPoolFactory.createThreadPool();
		this.logger = Logger.getLogger("JADE_Platform");
		this.services = new LinkedHashMap();
		this.appfactory = new ApplicationFactory(this, new Map[]{MAGRSpaceType.getXMLMapping()});
		services.put(ILibraryService.class, new LibraryService());
		services.put(ThreadPoolService.class, new ThreadPoolService(threadpool));
		services.put(IAMS.class, new AMS(this));
		services.put(IDF.class, new DF(this));
		services.put(IClockService.class, new ClockService(new SystemClock("system", 1000, threadpool), this));
		services.put(ISimulationService.class, new SimulationService(this));
		services.put(IMessageService.class, new MessageService(this));
		// Dummy execution service required for simulation service.
		services.put(IExecutionService.class, new IExecutionService()
		{
			public void addIdleCommand(ICommand command)
			{
				// nop
			}
			
			public void removeIdleCommand(ICommand command)
			{
				// nop
			}
			
			public boolean isIdle()
			{
				return true; // Hack!!!
			}
			
			public void cancel(IExecutable task, IResultListener listener)
			{
				throw new UnsupportedOperationException(); 
			}
			
			public void execute(IExecutable task)
			{
				throw new UnsupportedOperationException(); 
			}
			
			public void shutdown(IResultListener listener)
			{
				// nop
				if(listener!=null)
					listener.resultAvailable(null);
			}
			
			public void start()
			{
				// nop
			}
			
			public void stop(IResultListener listener)
			{
				// nop
				if(listener!=null)
					listener.resultAvailable(null);
			}
		});
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
		
		final IAMS ams = (IAMS)getService(IAMS.class);
		ams.createAgent("jcc", "jadex/tools/jcc/JCC.agent.xml", null, null, new DefaultResultListener(getLogger())
		{
			public void resultAvailable(Object result)
			{
				ams.startAgent((IAgentIdentifier)result, null);
			}
		}, null);
		
		for(Iterator it=services.keySet().iterator(); it.hasNext(); )
		{
			IPlatformService	service	= (IPlatformService) services.get(it.next());
			service.start();
		}
	}
	
	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return platformagent.getHap();
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
				Object exe = execon.newInstance(new Object[]{threadpool});
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

	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	// Todo: remove from external platform interface
	public IApplicationFactory getApplicationFactory()
	{
		return appfactory;
	}
	
	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	// Todo: move to message service?
	public MessageType getMessageType(String type)
	{
		return SFipa.FIPA_MESSAGE_TYPE.getName().equals(type)? SFipa.FIPA_MESSAGE_TYPE: null;
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
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return this.logger;
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

