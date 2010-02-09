package jadex.adapter.standalone;

import jadex.adapter.base.DefaultResultListener;
import jadex.bridge.IComponentExecutionService;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.IThreadPool;
import jadex.service.PropertyServiceContainer;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.clock.ITimer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;


/**
 *  Abstract base class for standalone platform.
 */
public abstract class AbstractPlatform extends PropertyServiceContainer
{
	//-------- constants --------

	/** The configuration file. */
	public static final String CONFIGURATION = "conf";

	/** The maximum shutdown time. */
	public static final long MAX_SHUTDOWM_TIME = 3000;

	//-------- attributes --------

	/** The optional system agents (ams, df). */
	protected Set daemonagents;

	/** The logger. */
	protected Logger logger;

	/** The shutdown flag. */
	protected boolean shuttingdown;

	/** The agent factory. */
//	protected IAgentFactory agentfactory;

	/** The application factory. */
//	protected IApplicationFactory appfactory;

	/** The message types. */
//	protected Map messagetypes;

	/** The shutdown time. */
	protected long shutdowntime;
	
	/** The threadpool. */
	protected IThreadPool threadpool;

	//-------- methods --------
	
	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 * /
	public MessageType getMessageType(String type)
	{
		return (MessageType)messagetypes.get(type);
	}*/

	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 * /
	public IAgentFactory getAgentFactory()
	{
		return agentfactory;
	}*/
	
	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 * /
	// Todo: remove from external platform interface
	public IApplicationFactory getApplicationFactory()
	{
		return appfactory;
	}*/
	
	/**
	 *  Check if the platform is currently shutting down.
	 */
	public boolean isShuttingDown() // todo: make protected?
	{
		return shuttingdown;
	}

	/**
	 *  Get the platform logger.
	 *  @return The platform logger.
	 */
	public Logger getLogger()
	{
		return logger;
	}

	/**
	 *  Shutdown the platform.
	 */
	public void shutdown(IResultListener rl)
	{
		if(rl==null)
			rl	= DefaultResultListener.getInstance();
		final IResultListener	listener	= rl;
		
		//System.out.println("Shutting down the platform: "+getName());
		// Hack !!! Should be synchronized with CES.
		synchronized(this)
		{
			if(shuttingdown)
				return;

			this.shuttingdown = true;
		}
		
		// Step 1: Find existing components.
		final IComponentExecutionService	ces	= (IComponentExecutionService)getService(IComponentExecutionService.class);
		ces.getComponentIdentifiers(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				// Step 2: Kill existing components excepts daemons.
				final List comps = new ArrayList(Arrays.asList((IComponentIdentifier[])result));
				for(Iterator it=daemonagents.iterator(); it.hasNext(); )
				{
					comps.remove(it.next());
				}
				killComponents(comps, shutdowntime!=0 ? shutdowntime : MAX_SHUTDOWM_TIME, new IResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						// Step 3: Find remaining components.
						ces.getComponentIdentifiers(new IResultListener()
						{
							public void resultAvailable(Object source, Object result)
							{
								// Step 4: Kill remaining components.
								killComponents(Arrays.asList((IComponentIdentifier[])result), shutdowntime!=0 ? shutdowntime : MAX_SHUTDOWM_TIME, new IResultListener()
								{
									public void resultAvailable(Object source, Object result)
									{
										// Step 5: Stop the services.
										AbstractPlatform.super.shutdown(listener);
									}
									public void exceptionOccurred(Object source, Exception exception)
									{
										listener.exceptionOccurred(source, exception);
									}
								});
							}

							public void exceptionOccurred(Object source, Exception exception)
							{
								listener.exceptionOccurred(source, exception);
							}
						});		
					}
					
					public void exceptionOccurred(Object source, Exception exception)
					{
						listener.exceptionOccurred(source, exception);
					}
				});
			}

			public void exceptionOccurred(Object source, Exception exception)
			{
				listener.exceptionOccurred(source, exception);
			}
		});		
	}
	
	/**
	 *  Create a component.
	 */
	protected void createComponent(String name, String model, String config, Map args, final boolean daemon)
	{
		IComponentExecutionService	ces	= (IComponentExecutionService)getService(IComponentExecutionService.class);
		ces.createComponent(name, model, config, args, false, null, null, null);
	}

	/**
	 *  Kill the given components within the specified timeout.
	 *  @param comps	The component ids.
	 *  @param timeout	The time after which to inform the listener anyways.
	 *  @param listener	The result listener.
	 */
	protected void killComponents(final List comps, long timeout, final IResultListener listener)
	{
		if(comps.isEmpty())
			listener.resultAvailable(this, null);
		
		// Timer entry to notify lister after timeout.
		final	boolean	notified[]	= new boolean[1];
		IClockService clock	= (IClockService)getService(IClockService.class);
		final ITimer	killtimer	= clock.createTimer(timeout, new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				boolean	notify	= false;
				synchronized(notified)
				{
					if(!notified[0])
					{
						notify	= true;
						notified[0]	= true;
					}
				}
				if(notify)
				{
					listener.resultAvailable(this, null);
				}
			}
		});
		
		// Kill the given components.
		IResultListener	rl	= new IResultListener()
		{
			int cnt	= 0;
			public void resultAvailable(Object source, Object result)
			{
				testFinished();
			}
			public void exceptionOccurred(Object source, Exception exception)
			{
				testFinished();
			}
			protected synchronized void testFinished()
			{
				cnt++;
				if(cnt==comps.size())
				{
					killtimer.cancel();
					boolean	notify	= false;
					synchronized(notified)
					{
						if(!notified[0])
						{
							notify	= true;
							notified[0]	= true;
						}
					}
					if(notify)
					{
						listener.resultAvailable(this, null);
					}
				}
			}
		};
		IComponentExecutionService	ces	= (IComponentExecutionService)getService(IComponentExecutionService.class);
		for(int i=0; i < comps.size(); i++)
		{
			//System.out.println("Killing component: "+comps.get(i));
			ces.destroyComponent((IComponentIdentifier)comps.get(i), rl);
		}
	}

	/**
	 *  Create an application.
	 * /
	protected void createApplication(String name, String model, String config, Map args)
	{
		try
		{
			Collection facts = getServices(IComponentFactory.class);
			if(facts!=null)
			{
				for(Iterator it=facts.iterator(); it.hasNext(); )
				{
					IComponentFactory fac = (IComponentFactory)it.next();
					
					if(it instanceof IApplicationFactory)
					{
						IApplicationFactory afac = (IApplicationFactory)fac;
						if(afac.isLoadable(model))
						{
							afac.createApplication(name, model, config, args);
							break;
						}
					}
				}
			}
//			getApplicationFactory().createApplication(name, model, config, args);
		}
		catch(Exception e)
		{
			System.err.println("Exception occurred while creating application: ");
			e.printStackTrace();
		}
	}*/
	
	/**
	 * 
	 * /
	protected IAMS getAMSService()
	{
		IAMS ret = null;
		Collection exes = getServices(IComponentExecutionService.class);
		if(exes!=null)
		{
			for(Iterator it=exes.iterator(); it.hasNext() && ret==null; )
			{
				IComponentExecutionService es = (IComponentExecutionService)it.next();
				if(es instanceof IAMS)
					ret = (IAMS)es;
			}
		}
		return ret;
	}*/
	
	//-------- static part --------

	/**
	 *  Start command line agents.
	 *  @param args The command line arguments.
	 *  @param platform The platform.
	 * /
	protected static void startAgents(String[] args, final IPlatform platform)
	{
		// Create agents on the platform.
		// Syntax: <name>:<model>(<config>,<arg1name=arg1>,...,<argNname=argN>)
		// e.g. hello:jadex.examples.helloworld.HelloWorld(default,msg=\"HEY!\")
		// <name>			corresponds to the name the agent is given by the platform
		// <model>			fully-qualified name of the agent-model (as defined in the agent's ADF)
		// <config>			configuration of the agent. Configurations are defined in the agent's ADF
		// <argX>			(list of) argument(s) for the agent. For every argument there has to be an
		//                  exported belief with the corresponding name.
		for(int i = 0; i < args.length; i++)
		{
			try
			{
				//System.out.println("arg:"+i+"="+args[i]);
				int index = args[i].indexOf(":");
				if(index != -1)
				{
					String name = args[i].substring(0, index);
					String model = args[i].substring(index + 1, args[i].length());
					String config = null;
					Map argsmap = null;
					index = model.indexOf("(");
					if(index != -1)
					{
						if(model.lastIndexOf(")") != model.length() - 1)
							throw new RuntimeException("Syntax-Error. Missing ')' at the end of agent's (command-line) argument: " + model);
						String agentargs = model.substring(index + 1, model.length() - 1);
						model = model.substring(0, index);
						StringTokenizer stok = new StringTokenizer(agentargs, ",");
						if(stok.hasMoreTokens())
						{
							String tmp = stok.nextToken();
							if(!tmp.equals("null"))
								config = tmp;
						}

						// Parse the arguments.
						argsmap = SCollection.createHashMap();
						while(stok.hasMoreTokens())
						{
							String tmp = stok.nextToken();
							int idx = tmp.indexOf("=");
							if(idx != -1)
							{
								String argname = tmp.substring(0, idx).trim();
								String argvalstr = tmp.substring(idx + 1).trim();
								//System.out.println("Found arg: "+argname+" = "+argvalstr);
								argsmap.put(argname, argvalstr);
							}
						}

						// Hack! define constants!
						if(argsmap.size() > 0)
							argsmap.put("evaluation_language", "Java"); // Hack?!
					}

					IAMS ams = (IAMS)platform.getService(IAMS.class);
					if(ams != null)
					{
						ams.createAgent(name, model, config, argsmap, new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								ComponentIdentifier aid = (ComponentIdentifier)result;
								IAMS ams = (IAMS)platform.getService(IAMS.class);
								ams.startAgent(aid, null);
							}

							public void exceptionOccurred(Exception exception)
							{
								System.err.println("Exception occurred: " + exception);
							}
						}, null);
					}
					else
					{
						System.out.println("Cannot create agent due to missing ams service.");
					}
				}
				else
				{
					System.out.println("Illegal agent specification: " + args[i]);
					System.out.println("Syntax: <name>:<model> ... e.g. hello:jadex.examples.helloworld.HelloWorld");
					System.out.println("Syntax: <name>:<model>(<initialstate>,<arg1name=arg1>,...,<argNname=argNvalue>) ... \ne.g. hello:jadex.examples.helloworld.HelloWorld(default,msg=\"HEY!\")");
				}
			}
			catch(RuntimeException e)
			{
				e.printStackTrace();
			}
		}
	}*/

}
