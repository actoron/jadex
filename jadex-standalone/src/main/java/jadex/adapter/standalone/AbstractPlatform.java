package jadex.adapter.standalone;

import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IAgentFactory;
import jadex.bridge.IPlatform;
import jadex.bridge.IPlatformService;
import jadex.bridge.MessageType;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.concurrent.ThreadPoolFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;


/**
 *  Abstract base class for standalone platform.
 */
public abstract class AbstractPlatform implements IPlatform
{
	//-------- constants --------

	/** The configuration file. */
	public static final String CONFIGURATION = "conf";

	/** The maximum shutdown time. */
	public static final long MAX_SHUTDOWM_TIME = 3000;

	//-------- attributes --------

	/** The map of platform services. */
	protected Map services;

	/** The optional system agents (ams, df). */
	protected Set daemonagents;

	/** The logger. */
	protected Logger logger;

	/** The shutdown flag. */
	protected boolean shuttingdown;

	/** The agent factory. */
	protected IAgentFactory agentfactory;

	/** The platform name. */
	protected String platformname;

	/** The message types. */
	protected Map messagetypes;

	/** The shutdown time. */
	protected long shutdowntime;

	//-------- methods --------

	/**
	 *  Add a service to the platform.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void addService(Class type, String name, IPlatformService service)
	{
//		System.out.println("Adding service: " + name + " " + type + " " + service);
		Map tmp = (Map)services.get(type);
		if(tmp == null)
		{
			tmp = new HashMap();
			services.put(type, tmp);
		}
		tmp.put(name, service);
	}

	/**
	 *  Add a service to the platform.
	 *  @param name The name.
	 *  @param service The service.
	 */
	public void removeService(Class type, IPlatformService service)
	{
		//		System.out.println("Removing service: " + type + " " + service);
		Map tmp = (Map)services.get(type);
		if(tmp == null || (service != null && !tmp.containsValue(service)))
			throw new RuntimeException("Service not found: " + service);

		boolean removed = false;
		if(service == null && tmp.size() == 1)
		{
			tmp.remove(tmp.keySet().iterator().next());
			removed = true;
		}
		else
		{
			for(Iterator it = tmp.keySet().iterator(); it.hasNext();)
			{
				Object key = it.next();
				if(tmp.get(key).equals(service))
				{
					tmp.remove(key);
					removed = true;
				}
			}
		}

		if(!removed)
			throw new RuntimeException("Service not found: " + service);

		if(tmp.size() == 0)
			services.remove(type);
	}

	/**
	 *  Get a platform service.
	 *  @param type The class.
	 *  @return The corresponding platform services.
	 */
	public Collection getServices(Class type)
	{
		Collection	ret	= null;
		Map tmp = (Map)services.get(type);
		if(tmp != null)
			ret	= tmp.values();
		else
			ret	= Collections.EMPTY_SET;
//			throw new RuntimeException("No services found of type: " + type);

		return ret;
	}

	/**
	 *  Get a platform service.
	 *  @param name The name.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type, String name)
	{
		Object ret	= null;
		Map tmp = (Map)services.get(type);
		if(tmp != null)
		 ret = tmp.get(name);
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		return ret;
	}

	/**
	 *  Get the first declared platform service of a given type.
	 *  @param type The type.
	 *  @return The corresponding platform service.
	 */
	public Object getService(Class type)
	{
		Object ret	= null;
		Map tmp = (Map)services.get(type);
		if(tmp != null && !tmp.isEmpty())
			ret = tmp.values().iterator().next();
//		if(ret == null)
//			throw new RuntimeException("Service not found");
		return ret;
	}

	/**
	 *  Get the name of the platform
	 *  @return The name of this platform.
	 */
	public String getName()
	{
		return platformname;
	}

	/**
	 *  Get the message type.
	 *  @param type The type name.
	 *  @return The message type.
	 */
	protected MessageType getMessageType(String type)
	{
		return (MessageType)messagetypes.get(type);
	}

	/**
	 *  Get the agent factory.
	 *  @return The agent factory.
	 */
	public IAgentFactory getAgentFactory()
	{
		return agentfactory;
	}

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
	public void shutdown(final IResultListener listener)
	{
		//System.out.println("Shutting down the platform: "+getName());
		// Hack !!! Should be sysnchronized with AMS.
		synchronized(this)
		{
			if(shuttingdown)
				return;

			this.shuttingdown = true;

			final IAMS ams = (IAMS)getService(IAMS.class);
			ams.getAgentIdentifiers(new IResultListener()
			{
				public void resultAvailable(Object result)
				{
					IAgentIdentifier[] agents = (IAgentIdentifier[])result;
					for(int i = 0; i < agents.length; i++)
					{
						try
						{
							// Do not kill ams and df agents immediately.
							if(!daemonagents.contains(agents[i]))
							{
								ams.destroyAgent(agents[i], null);
								//System.out.println("Killing normal agent: "+agents[i]);
							}
						}
						catch(RuntimeException e)
						{
							// Due to race conditions, agent may have killed itself already.
						}
					}
				}

				public void exceptionOccurred(Exception exception)
				{
				}
			});
		}

		// Necessary because shutdown method should return
		new Thread(new Runnable()
		{
			public void run()
			{
				long shutdown = System.currentTimeMillis() + MAX_SHUTDOWM_TIME;
				try
				{
					if(shutdowntime != 0)
						shutdown = System.currentTimeMillis() + shutdowntime;
				}
				catch(NumberFormatException e)
				{
				}

				// Wait until agents have died.
				// Hack!!! Should not poll AMS?
				final boolean[] wait = new boolean[1];
				IAMS ams = (IAMS)getService(IAMS.class);
				while(wait[0] && System.currentTimeMillis() < shutdown)
				{
					ams.getAgentCount(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							wait[0] = ((Integer)result).intValue() > daemonagents.size();
						}

						public void exceptionOccurred(Exception exception)
						{
						}
					});
					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
					}
				}

				// Hack!! Should not need to rely on preconfigured system agents. 
				// Should instead use agent types? 
				AgentIdentifier[] sagents = (AgentIdentifier[])daemonagents.toArray(new AgentIdentifier[daemonagents.size()]);
				for(int i = 0; i < sagents.length; i++)
				{
					try
					{
						//System.out.println("Killing system agent: "+sagents[i]);
						final boolean[] finished = new boolean[1];
						ams.destroyAgent(sagents[i], new IResultListener()
						{
							public void resultAvailable(Object result)
							{
								finished[0] = true;
							}

							public void exceptionOccurred(Exception exception)
							{
								finished[0] = true;
							}
						});
						while(!finished[0])
						{
							try
							{
								Thread.sleep(100);
							}
							catch(Exception e)
							{
							}
						}
					}
					catch(RuntimeException e)
					{
						e.printStackTrace();
						// Due to race conditions, agent may have killed itself already.
					}
				}

				// Hack!!! Should not poll AMS?
				long start = System.currentTimeMillis();

				// Wait until agents have died.
				// Hack!!! Should not poll AMS?
				wait[0] = true;
				while(wait[0] && start + 1000 > System.currentTimeMillis())
				{
					ams.getAgentCount(new IResultListener()
					{
						public void resultAvailable(Object result)
						{
							wait[0] = ((Integer)result).intValue() > daemonagents.size();
						}

						public void exceptionOccurred(Exception exception)
						{
						}
					});
					try
					{
						Thread.sleep(100);
					}
					catch(InterruptedException e)
					{
					}
				}

				// Use list for shutdown in inverted insertion order. (Hack!!! is there a better way?)
				/*List servicelist = new ArrayList();
				for(Iterator it = services.values().iterator(); it.hasNext();)
				{
					servicelist.add(it.next());
				}
				// Todo: use result listener?
				while(!servicelist.isEmpty())
				{
					((IPlatformService)servicelist.remove(servicelist.size() - 1)).shutdown(null);
				}*/

				// Start the services.
				for(Iterator it = services.keySet().iterator(); it.hasNext();)
				{
					Object key = it.next();
					Map tmp = (Map)services.get(key);
					if(tmp != null)
					{
						for(Iterator it2 = tmp.keySet().iterator(); it2.hasNext();)
						{
							Object key2 = it2.next();
							IPlatformService service = (IPlatformService)tmp.get(key2);
//							System.out.println("Service shutdown: " + service);
							service.shutdown(null); // Todo: use result listener?
						}
					}
				}

				//				if(listener!=null)
				//					listener.resultAvailable(null);
				//				System.exit(0);

				ThreadPoolFactory.getThreadPool(getName()).dispose();
			}
		}).start();
	}

	//-------- static part --------

	/**
	 *  Start command line agents.
	 *  @param args The command line arguments.
	 *  @param platform The platform.
	 */
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
								AgentIdentifier aid = (AgentIdentifier)result;
								IAMS ams = (IAMS)platform.getService(IAMS.class);
								ams.startAgent(aid, null);
							}

							public void exceptionOccurred(Exception exception)
							{
								System.err.println("Exception occurred: " + exception);
							}
						});
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
	}
}
