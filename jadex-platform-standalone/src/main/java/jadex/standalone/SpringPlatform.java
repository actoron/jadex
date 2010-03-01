package jadex.standalone;

import jadex.bridge.IComponentListener;
import jadex.commons.collection.SCollection;
import jadex.service.IService;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
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
	public static final String FALLBACK_CONFIGURATION = "jadex/standaloneone_springconf.xml";
	
	//-------- attributes --------
	
	/** The ams listener. */
	protected IComponentListener amslistener;
	
	/** The daemon components. */
	protected Map daemcomponents;
	
	/** The application components. */
	protected Map appcomponents;
	
	/** Flag indicating if autoshutdown. */
	protected boolean autoshutdown;
	
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
		String	name = platformname;

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
	}

	/**
	 *  Set the autoshutdown.
	 *  @param autoshutdown The autoshutdown to set.
	 */
	public void setAutoshutDown(boolean autoshutdown)
	{
		/*
		if(autoshutdown && !this.autoshutdown)
		{
			if(amslistener==null)
			{
				amslistener = new IComponentListener()
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
				};
			}
			
			if(getAMSService() != null)
				getAMSService().addComponentListener(amslistener);
		}
		else if(!autoshutdown && this.autoshutdown)
		{
			if(getAMSService() != null)
				getAMSService().removeComponentListener(amslistener);
		}
		
		this.autoshutdown = autoshutdown;
		*/
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
	 *  Set the daemon components.
	 *  @param daemoncomponents The daemon components.
	 */
	public void setDaemonComponents(Map daemoncomponents)
	{
		this.daemcomponents = daemoncomponents;
	}

	/**
	 *  Set the components.
	 *  @param components The components.
	 */
	public void setComponents(Map appcomponents)
	{
		this.appcomponents = appcomponents;
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
					IService service = (IService)tmp.get(key2);
					service.startService();
				}
			}
		}
		
		// Create daemon components.
		this.daemoncomponents = SCollection.createLinkedHashSet();
		if(daemcomponents != null)
		{
			for(Iterator it = daemcomponents.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				String model;
				String config = null;
				Map args = null;
				Object tmp = daemcomponents.get(name);
				if(tmp instanceof String)
				{
					model = (String)tmp;
				}
				else
				{
					args = (Map)tmp;
					model = (String)args.remove("model");
					config = (String)args.remove("config");
				}
				createComponent(name, model, config, args, true);
			}
		}
		
		// Create application components.
		if(appcomponents != null)
		{
			for(Iterator it = appcomponents.keySet().iterator(); it.hasNext();)
			{
				String name = (String)it.next();
				String model;
				String config = null;
				Map args = null;
				Object tmp = appcomponents.get(name);
				if(tmp instanceof String)
				{
					model = (String)tmp;
				}
				else
				{
					args = (Map)tmp;
					model = (String)args.remove("model");
					config = (String)args.remove("config");
				}
				createComponent(name, model, config, args, false);
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
	 *  Start a platform with the components specified
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
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}
