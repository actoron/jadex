package jadex.adapter.standalone;

import jadex.adapter.base.MetaAgentFactory;
import jadex.adapter.base.fipa.IAMS;
import jadex.adapter.base.fipa.IAMSAgentDescription;
import jadex.adapter.base.fipa.IAMSListener;
import jadex.adapter.standalone.fipaimpl.AgentIdentifier;
import jadex.bridge.IPlatformService;
import jadex.commons.collection.SCollection;
import jadex.commons.concurrent.IResultListener;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 *  Spring version of the standalone platform.
 */
public class SpringPlatform extends AbstractPlatform
{
	//-------- constants --------

	/** The fallback configuration. */
	public static final String FALLBACK_CONFIGURATION = "jadex/adapter/standalone/standalone_springconf.xml";
	
	//-------- attributes --------
	
	/** The ams listener. */
	protected IAMSListener amslistener;
	
	/** The daemon agents. */
	protected Map daemagents;
	
	/** The application agents. */
	protected Map appagents;
	
	//-------- constructors --------

	/**
	 *  Create a new Platform.
	 */
	public SpringPlatform()
	{
		this.logger = Logger.getLogger("Platform_" + getName());
	}

	/**
	 *  Set the platformname.
	 *  @param platformname The platformname to set.
	 */
	public void setPlatformName(String platformname)
	{
//		System.out.println("setPlatformName: "+platformname);
		this.platformname = platformname;

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
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoshutDown(boolean autoshutdown)
	{
		if(autoshutdown && !this.autoshutdown)
		{
			if(amslistener==null)
			{
				amslistener = new IAMSListener()
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
				};
			}
			
			IAMS ams = (IAMS)getService(IAMS.class);
			if(ams != null)
				ams.addAMSListener(amslistener);
		}
		else if(!autoshutdown && this.autoshutdown)
		{
			IAMS ams = (IAMS)getService(IAMS.class);
			if(ams != null)
				ams.removeAMSListener(amslistener);
		}
		
		this.autoshutdown = autoshutdown;
	}

	/**
	 *  Set the shutdowntime.
	 *  @param shutdowntime The shutdowntime to set.
	 */
	public void setShutdownTime(long shutdowntime)
	{
		this.shutdowntime = shutdowntime;
	}

	/**
	 *  Set the messagetypes.
	 *  @param messagetypes The messagetypes to set.
	 */
	public void setMessageTypes(Map messagetypes)
	{
		this.messagetypes = messagetypes;
	}

	/**
	 *  Set the agent factories.
	 *  @param factories The agent factories.
	 */
	public void setAgentFactories(List factories)
	{
		this.agentfactory = new MetaAgentFactory(factories);
	}

	/**
	 *  Set the daemon agents.
	 *  @param daemonagents The daemon agents.
	 */
	public void setDaemonAgents(Map daemonagents)
	{
		this.daemagents = daemonagents;
	}

	/**
	 *  Set the agents.
	 *  @param agents The agents.
	 */
	public void setAgents(Map appagents)
	{
		this.appagents = appagents;
	}

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
		
		// Create daemon agents.
		this.daemonagents = SCollection.createLinkedHashSet();
		if(daemagents != null)
		{
			IAMS ams = (IAMS)getService(IAMS.class);
			for(Iterator it = daemagents.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				ams.createAgent(name, (String)daemagents.get(name), null, null, new IResultListener()
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
		}
		
		// Create application agents.
		if(appagents != null)
		{
			IAMS ams = (IAMS)getService(IAMS.class);
			for(Iterator it = appagents.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				ams.createAgent(name, (String)appagents.get(name), null, null, new IResultListener()
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
	}
	
	/**
	 *  Set the platform services.
	 *  @param services The services.
	 */
	public void setServices(Map services)
	{
		this.services = services;
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
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[]{conffile});
		SpringPlatform platform = (SpringPlatform)context.getBean("platform");
		platform.start();
		startAgents(args, platform);
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}
