package jadex.base;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import jadex.bridge.ClassInfo;
import jadex.bridge.ProxyFactory;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.javaparser.SJavaParser;

/**
 *  Platform configuration handler is a proxy handler that implements
 *  the logic for the configuration interfaces.
 */
public class PlatformConfigurationHandler implements InvocationHandler
{
	/** The map of values. */
	protected Map<String, Object> values = new HashMap<String, Object>();
	
	/** Used for mapping method names to property names (if they differ). */
	public static Map<String, String> namemappings = new HashMap<String, String>();
	
	static
	{
		namemappings.put("configurationfile", IStarterConfiguration.CONFIGURATION_FILE);
		namemappings.put("networkname", "networknames");
	}
	
	/**
	 *  Create a new handler.
	 */
	public PlatformConfigurationHandler()
	{
		values.put(IStarterConfiguration.COMPONENT_FACTORY, IStarterConfiguration.FALLBACK_COMPONENT_FACTORY);
		values.put(IStarterConfiguration.CONFIGURATION_FILE, IStarterConfiguration.FALLBACK_PLATFORM_CONFIGURATION);
		values.put("localdefaulttimeout", IStarterConfiguration.DEFAULT_LOCAL_TIMEOUT);
		values.put("remotedefaulttimeout", IStarterConfiguration.DEFAULT_REMOTE_TIMEOUT);
		values.put("components", new ArrayList<String>());
//		System.out.println("PlatformConfigurationHandler: "+values+" "+hashCode());
	}
	
	/**
	 *  Called on method invocation.
	 *  @param proxy The proxy.
	 *  @param method The method.
	 *  @param args The arguments.
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable
	{
		Object ret = null;
		
		String mname = method.getName();
		
		// from IPlatformConfiguration
		if(mname.equals("getRootConfig"))
		{
			ret = proxy;
		}
		// from IPlatformConfiguration
		else if(mname.equals("getStarterConfig"))
		{
			ret = proxy;
		}
		// Convert class to name.
		else if(mname.equals("addComponent") && args[0] instanceof Class<?>)
		{
			((IStarterConfiguration)proxy).addComponent(((Class<?>)args[0]).getName()+".class");
		}
		else if(mname.equals("setValue"))
		{
			values.put((String)args[0], args[1]);
		}
		else if(mname.equals("getValue"))
		{
			ret = values.get(args[0]);
		}
		else if(mname.equals("parseArg"))
		{
			parseArg((IPlatformConfiguration)proxy, (String)args[0], (String)args[1], args[2]);
		}
		else if(mname.equals("getArgs"))
		{
			ret = values;
		}
		else if(mname.equals("enhanceWith"))
		{
			Map<String, Object>	other	= ((PlatformConfigurationHandler)ProxyFactory.getInvocationHandler(args[0])).values;
	        for(Map.Entry<String, Object> entry : other.entrySet())
	        {
	            values.put(entry.getKey(), entry.getValue());
	        }
		}
		else if(mname.equals("clone"))
		{
			PlatformConfigurationHandler h = new PlatformConfigurationHandler();
			h.values = new HashMap<String, Object>(values);
			ret = getPlatformConfiguration(null, h);
		}
//		else if(mname.equals("getComponentFactory"))
//		{
//			 return (String)values.get(IStarterConfiguration.COMPONENT_FACTORY)!=null?
//				 (String)values.get(IStarterConfiguration.COMPONENT_FACTORY): IStarterConfiguration.FALLBACK_COMPONENT_FACTORY;
//		}
		
		else if(mname.startsWith("set"))
		{
			values.put(getKeyForMethodname(mname, 3), args[0]);
		}
		else if(mname.startsWith("add"))
		{
			String prop	= getKeyForMethodname(SUtil.getPlural(mname), 3);
			Collection<Object> vals = (Collection<Object>)values.get(prop);
			if(vals==null)
			{
				vals = new ArrayList<Object>();
				values.put(prop, vals);
			}
			vals.add(args[0]);
		}
		else if(mname.startsWith("get") || method.getName().startsWith("has"))
		{
//			ret = values.get(getKeyForMethodname(mname, 3));
			
			ret = getValue(getKeyForMethodname(mname, 3));
		}
		else if(method.getName().startsWith("is"))
		{
			ret = values.get(getKeyForMethodname(mname, 2));
		}
		else if(mname.startsWith("remove"))
		{
			String prop	= getKeyForMethodname(SUtil.getPlural(mname), 3);
			Collection<Object> vals = (Collection<Object>)values.get(prop);
			if(vals!=null)
				vals.remove(args[0]);
		}
		else
		{
			throw new UnsupportedOperationException(method.getName());
		}
		
		if(ret==null && method.getReturnType().equals(boolean.class))
			ret = false;
		
//		System.out.println("config: "+method.getName()+" "+hashCode()+" "+method.getDeclaringClass());
		
		return ret;
	}
	
	/**
	 *  Get the key for a method name.
	 *  @param mname The method name.
	 *  @param prefixlen The prefix length.
	 *  @return The key.
	 */
	protected String getKeyForMethodname(String mname, int prefixlen)
	{
		String ret = mname.substring(prefixlen).toLowerCase();
		if(namemappings.containsKey(ret))
			ret = namemappings.get(ret);
		return ret;
	}
	
	/**
     *  Parse an argument.
     *  @param key The key.
     *  @param strval The value.
     */
    public void parseArg(IPlatformConfiguration config, String key, String strval, Object value)
    {
        if(IStarterConfiguration.COMPONENT.equals(key))
        {
            config.addComponent((String)strval);
        }
        else if(IStarterConfiguration.DEBUGFUTURES.equals(key) && "true".equals(strval))
        {
        	config.setDebugFutures(true);
        }
        else if(IStarterConfiguration.DEBUGSERVICES.equals(key) && "true".equals(strval))
        {
        	config.setDebugServices(true);
        }
        else if(IStarterConfiguration.DEBUGSTEPS.equals(key) && "true".equals(strval))
        {
        	config.setDebugSteps(true);
        }
        else if(IStarterConfiguration.DEFTIMEOUT.equals(key))
        {
        	value = SJavaParser.evaluateExpression(strval, null);
//			BasicService.DEFTIMEOUT	= ((Number)stringValue).longValue();
            long to	= ((Number)value).longValue();
//			setLocalDefaultTimeout(platform, to);
//			setRemoteDefaultTimeout(platform, to);
            config.setDefaultTimeout(to);

//			BasicService.setRemoteDefaultTimeout(to);
//			BasicService.setLocalDefaultTimeout(to);
//			System.out.println("timeout: "+BasicService.DEFAULT_LOCAL);
        }
        else if(IStarterConfiguration.NOSTACKCOMPACTION.equals(key) && "true".equals(strval))
        {
        	config.setNoStackCompaction(true);
        }
        else if(IStarterConfiguration.OPENGL.equals(key) && "false".equals(strval))
        {
        	config.setOpenGl(false);
        }
        else if(IStarterConfiguration.MONITORING.equals(key))
        {
//            Object tmpmoni = getValue(IStarterConfiguration.MONITORING);
            Object tmpmoni = values.get(IStarterConfiguration.MONITORING);
            IMonitoringService.PublishEventLevel moni = IMonitoringService.PublishEventLevel.OFF;
            if(tmpmoni instanceof Boolean)
            {
                moni = ((Boolean)tmpmoni).booleanValue()? IMonitoringService.PublishEventLevel.FINE: IMonitoringService.PublishEventLevel.OFF;
            }
            else if(tmpmoni instanceof String)
            {
                moni = IMonitoringService.PublishEventLevel.valueOf((String)tmpmoni);
            }
            else if(tmpmoni instanceof IMonitoringService.PublishEventLevel)
            {
                moni = (IMonitoringService.PublishEventLevel)tmpmoni;
            }
            config.setMonitoring(moni);
        }
        else
        {
        	config.setValue(key, value);
        }
    }
    
    // for starter
    /**
     * Generic getter for configuration parameters.
     * Retrieves values in 3 stages:
     * 1. From given command line arguments.
     * 2. From given model configuration ("auto", "fixed", ...)
     * 3. From model default values.
     *
     * For retrieval from model, setPlatformModel has to be called before.
     * @param key
     * @return
     */
    protected Object getValue(String key) 
    {
        Object val = values.get(key);
        if(val==null && getModel()!= null && getConfigurationInfo(getModel()) != null)
        {
            val = getArgumentValueFromModel(key);
        }
        else if(val instanceof String)
        {
            // Try to parse value from command line.
            try
            {
                Object newval = SJavaParser.evaluateExpression((String)val, null);
                if(newval!=null)
                {
                    val	= newval;
                }
            }
            catch(RuntimeException e)
            {
            }
        }

        return val;
    }
    
    /**
     * 
     */
    protected IModelInfo getModel()
    {
    	return (IModelInfo)values.get("platformmodel");
    }
    
    /**
     *  Get the configuration name.
     */
    protected ConfigurationInfo	getConfigurationInfo(IModelInfo model)
    {
        String	configname	= getConfigurationName();//(String)cmdargs.get(CONFIGURATION_NAME);
        if(configname==null)
        {
            Object	val	= null;
            IArgument	arg	= model.getArgument(IStarterConfiguration.CONFIGURATION_NAME);
            if(arg!=null)
            {
                val	= arg.getDefaultValue();
            }
            val	= SJavaParser.getParsedValue(val, model.getAllImports(), null, Starter.class.getClassLoader());
//			val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
            configname	= val!=null ? val.toString() : null;
        }

        ConfigurationInfo	compConfig	= configname!=null
                ? model.getConfiguration(configname)
                : model.getConfigurations().length>0 ? model.getConfigurations()[0] : null;

        return compConfig;
    }
    
    /**
     * 
     */
    public String getConfigurationName()
    {
        return (String)values.get(IStarterConfiguration.CONFIGURATION_NAME);
    }
    
    /**
     * 
     * @param name
     * @return
     */
    protected Object getArgumentValueFromModel(String name)
    {
        Object val = null;

        boolean	found	= false;
        // first try to get the value from choosen configuration
        if(getConfigurationInfo(getModel())!=null)
        {
            UnparsedExpression[]	upes	= getConfigurationInfo(getModel()).getArguments();
            for(int i=0; !found && i<upes.length; i++)
            {
                if(name.equals(upes[i].getName()))
                {
                    found	= true;
                    val	= upes[i];
                }
            }
        }
        // if this fails, get default value.
        if(!found)
        {
            IArgument arg	= getModel().getArgument(name);
            if(arg!=null)
            {
                val	= arg.getDefaultValue();
            }
        }
        val	= SJavaParser.getParsedValue(val, getModel().getAllImports(), null, Starter.class.getClassLoader());
//		val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
        return val;
    }
    
//    /**
//     * 
//     */
//    public Map<String, Object> getCmdArgs()
//    {
//    	return (Map<String, Object>)values.get();
//    }
    
//	/**
//	 *  Get the default platform configuration.
//	 *  @return The default configuration.
//	 */
//	public static IPlatformConfiguration getDefaultPlatformConfiguration()
//	{
//		return getDefaultPlatformConfiguration(null);
//	}
//	
//	/**
//	 *  Get the default platform configuration.
//	 *  @param cl The classloader.
//	 *  @return The default configuration.
//	 */
//	public static IPlatformConfiguration getDefaultPlatformConfiguration(ClassLoader cl)
//	{
//		IPlatformConfiguration ret = (IPlatformConfiguration)ProxyFactory.newProxyInstance(cl, new Class[]{IPlatformConfiguration.class}, new PlatformConfigurationHandler());
//		return ret;
//	}
	
	/**
	 *  Get the default platform configuration.
	 *  @return The default configuration.
	 */
	public static IPlatformConfiguration getPlatformConfiguration()
	{
		return getPlatformConfiguration((ClassLoader)null);
	}
	
	/**
	 *  Get the default platform configuration.
	 *  @param cl The classloader.
	 *  @return The default configuration.
	 */
	public static IPlatformConfiguration getPlatformConfiguration(ClassLoader cl)
	{
		return getPlatformConfiguration(cl, new PlatformConfigurationHandler());
	}
	
	/**
	 *  Get the default platform configuration.
	 *  @param cl The classloader.
	 *  @return The default configuration.
	 */
	public static IPlatformConfiguration getPlatformConfiguration(ClassLoader cl, PlatformConfigurationHandler h)
	{
		cl = cl==null? (ClassLoader)IPlatformConfiguration.class.getClassLoader(): cl;
		IPlatformConfiguration ret = (IPlatformConfiguration)ProxyFactory.newProxyInstance(cl, new Class[]{IPlatformConfiguration.class}, h);
		return ret;
	}
	
	/**
	 *  Get the default platform configuration.
	 *  @return The default configuration.
	 */
	public static IPlatformConfiguration getPlatformConfiguration(String[] args)
	{
		return getPlatformConfiguration(args, null);
	}
	
	/**
	 *  Get the default platform configuration.
	 *  @param cl The classloader.
	 *  @return The default configuration.
	 */
	public static IPlatformConfiguration getPlatformConfiguration(String[] args, ClassLoader cl)
	{
		IPlatformConfiguration ret = (IPlatformConfiguration)ProxyFactory.newProxyInstance(cl, new Class[]{IPlatformConfiguration.class}, new PlatformConfigurationHandler());
		ret.setProgramArguments(args);
		return ret;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Set<String> createReserved() 
    {
		Set<String> RESERVED = new HashSet<String>();
        RESERVED = new HashSet<String>();
        RESERVED.add(IStarterConfiguration.CONFIGURATION_FILE);
        RESERVED.add(IStarterConfiguration.CONFIGURATION_NAME);
        RESERVED.add(IStarterConfiguration.PLATFORM_NAME);
        RESERVED.add(IStarterConfiguration.COMPONENT_FACTORY);
        RESERVED.add(IStarterConfiguration.PLATFORM_COMPONENT);
        RESERVED.add(IStarterConfiguration.AUTOSHUTDOWN);
        RESERVED.add(IStarterConfiguration.MONITORING);
        RESERVED.add(IRootComponentConfiguration.WELCOME);
        RESERVED.add(IStarterConfiguration.COMPONENT);
        RESERVED.add(IStarterConfiguration.PARAMETERCOPY);
        RESERVED.add(IStarterConfiguration.REALTIMETIMEOUT);
        RESERVED.add(IStarterConfiguration.PERSIST);
        RESERVED.add(IStarterConfiguration.DEBUGFUTURES);
        RESERVED.add(IStarterConfiguration.DEBUGSERVICES);
        RESERVED.add(IStarterConfiguration.DEBUGSTEPS);
        RESERVED.add(IStarterConfiguration.NOSTACKCOMPACTION);
        RESERVED.add(IStarterConfiguration.OPENGL);
        RESERVED.add(IStarterConfiguration.DEFTIMEOUT);
        RESERVED.add(IStarterConfiguration.PRINTEXCEPTIONS);
        return RESERVED;
	}
	
	/**
	 * 
	 * @return
	 */
	public static Long getEnvironmentDefaultTimeout()
	{
		// Set deftimeout from environment, if set.
	    String	dtoprop	= System.getProperty("jadex.deftimeout", System.getenv("jadex.deftimeout"));
	    if(dtoprop==null)
	    	dtoprop	= System.getProperty("jadex_deftimeout", System.getenv("jadex_deftimeout"));
	    if(dtoprop==null)
	    	dtoprop	= System.getProperty("jadex_timeout", System.getenv("jadex_timeout"));
//	    if(dtoprop!=null)
//	    {
//	        System.out.println("Property jadex.deftimeout is deprecated. Use jadex_deftimeout instead.");
//	    }
//	    else
//	    {
//	        dtoprop	= System.getProperty("jadex_deftimeout", System.getenv("jadex_deftimeout"));
//	    }
	    if(dtoprop!=null)
	    {
//	        DEFAULT_REMOTE_TIMEOUT = (Long.parseLong(dtoprop));
//	        DEFAULT_LOCAL_TIMEOUT = (Long.parseLong(dtoprop));
	        System.out.println("Setting jadex_timeout: "+dtoprop);
	    }
	    return dtoprop!=null? Long.parseLong(dtoprop): null;
	}
	
	/**
	 * 
	 */
	public static Long getDefaultTimeout()
	{
		Long ret = getEnvironmentDefaultTimeout();
		if(ret==null)
			ret = SReflect.isAndroid() ? 60000L : 30000;
		return ret;
	}
	
	/**
	 * Returns a PlatformConfiguration with the default parameters.
	 */
	public static IPlatformConfiguration getDefault()
	{
		IPlatformConfiguration config = getPlatformConfiguration();
		// config.setPlatformName("jadex");
		config.getStarterConfig().setPlatformName(null);
		config.getStarterConfig().setConfigurationName("auto");
		config.getStarterConfig().setAutoShutdown(false);
		config.getStarterConfig().setPlatformComponent(new ClassInfo("jadex.platform.service.cms.PlatformComponent"));
//		config.getStarterConfig().setPlatformComponent(new ClassInfo("jadex.platform.service.cms.PlatformComponent").getType(null));
		IRootComponentConfiguration rootconf = config.getRootConfig();
		rootconf.setWelcome(true);
		rootconf.setGui(true);
		rootconf.setCliConsole(true);
		rootconf.setSaveOnExit(true);
		rootconf.setJccPlatforms(null);
		rootconf.setLogging(false);
		rootconf.setLoggingLevel(Level.SEVERE);
		rootconf.setThreadpoolDefer(true);
//		rootconf.setPersist(false);
		rootconf.setUniqueIds(true);

		rootconf.setChat(true);

		rootconf.setAwareness(true);
//		rootconf.setAwaMechanisms(IRootComponentConfiguration.AWAMECHANISM.broadcast, IRootComponentConfiguration.AWAMECHANISM.multicast, IRootComponentConfiguration.AWAMECHANISM.message,
//			IRootComponentConfiguration.AWAMECHANISM.relay, IRootComponentConfiguration.AWAMECHANISM.local);
		rootconf.setAwaMechanisms(IRootComponentConfiguration.AWAMECHANISM_BROADCAST, IRootComponentConfiguration.AWAMECHANISM_MULTICAST, IRootComponentConfiguration.AWAMECHANISM_LOCAL);
		rootconf.setAwaDelay(20000);
		rootconf.setAwaIncludes("");
		rootconf.setAwaExcludes("");

		rootconf.setBinaryMessages(true);
		rootconf.setStrictCom(false);
		rootconf.setPrintPass(true);

		rootconf.setLocalTransport(true);
		rootconf.setTcpTransport(true);
		rootconf.setTcpPort(0);
		// rootConfig.setRelayTransport(true);
		// rootConfig.setRelayAddress("jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS");
		// rootConfig.setRelaySecurity(true);
		// rootConfig.setSslTcpTransport(false);
		// rootConfig.setSslTcpPort(0);

		rootconf.setWsPublish(false);
		rootconf.setRsPublish(false);
//		rootconf.setKernels(IRootComponentConfiguration.KERNEL.multi);
		rootconf.setKernels(IRootComponentConfiguration.KERNEL_MULTI);
		rootconf.setMavenDependencies(false);
		rootconf.setSensors(false);
		rootconf.setThreadpoolClass(null);
		rootconf.setContextServiceClass(null);

		rootconf.setMonitoringComp(true);
		rootconf.setDf(true);
		rootconf.setClock(true);
		// rootConfig.setMessage(true);
		rootconf.setSimul(true);
		rootconf.setFiletransfer(true);
		rootconf.setMarshal(true);
		rootconf.setSecurity(true);
		rootconf.setLibrary(true);
		rootconf.setSettings(true);
		rootconf.setContext(true);
		rootconf.setAddress(true);
		// rootConfig.setRegistrySync(false);
		return config;
	}

	/**
	 * Returns a PlatformConfiguration with the default parameters but without gui.
	 */
	public static IPlatformConfiguration getDefaultNoGui()
	{
		IPlatformConfiguration config = getDefault();
		config.getRootConfig().setGui(false);
		return config;
	}

	/**
	 * Returns a PlatformConfiguration with the default parameters.
	 */
	public static IPlatformConfiguration getAndroidDefault()
	{
		IPlatformConfiguration config = getDefault();
		IRootComponentConfiguration rootconf = config.getRootConfig();
		rootconf.setGui(false);
		rootconf.setChat(false);
		rootconf.setKernels(IRootComponentConfiguration.KERNEL_COMPONENT, 
			IRootComponentConfiguration.KERNEL_MICRO, IRootComponentConfiguration.KERNEL_BPMN, IRootComponentConfiguration.KERNEL_BDIV3);
		rootconf.setLoggingLevel(Level.INFO);
		// config.setDebugFutures(true);
		return config;
	}

	/**
	 * Returns a minimal platform configuration without any network
	 * connectivity.
	 */
	public static IPlatformConfiguration getMinimal()
	{
		IPlatformConfiguration config = getDefault();
		IRootComponentConfiguration rootconf = config.getRootConfig();
		rootconf.setWelcome(false);
		rootconf.setGui(false);
		rootconf.setCli(false);
		rootconf.setCliConsole(false);

		rootconf.setChat(false);

		rootconf.setAwareness(false);
		rootconf.setAwaMechanisms();

		rootconf.setLocalTransport(true); // needed by message
		rootconf.setTcpTransport(false);
		rootconf.setWsTransport(false);
		rootconf.setRelayTransport(false);
		// rootConfig.setSslTcpTransport(false);

		rootconf.setKernels(IRootComponentConfiguration.KERNEL_MICRO);
		// rootConfig.setThreadpoolClass(null);
		// rootConfig.setContextServiceClass(null);

		rootconf.setMonitoringComp(false);
		rootconf.setDf(false);
		rootconf.setClock(true);
		// rootConfig.setMessage(true); // needed by rms
		rootconf.setSimul(false);
		rootconf.setFiletransfer(false);
		rootconf.setMarshal(true);
		rootconf.setSecurity(false);
		rootconf.setLibrary(true); // needed by micro
		rootconf.setSettings(true);
		rootconf.setContext(true);
		rootconf.setAddress(true);

		rootconf.setSuperpeer(false);
		rootconf.setSuperpeerClient(false);
		rootconf.setSupersuperpeer(false);

		return config;
	}

	/**
	 * Returns a minimal platform configuration that communicates via relay.
	 */
	public static IPlatformConfiguration getMinimalRelayAwareness()
	{
		IPlatformConfiguration config = getMinimal();
		IRootComponentConfiguration rootconf = config.getRootConfig();

		rootconf.setAwareness(true);
//		rootconf.setAwaMechanisms(IRootComponentConfiguration.AWAMECHANISM_RELAY);
		rootconf.setAwaFast(true); // Make sure awareness finds other
										// platforms quickly
		// rootConfig.setRelayTransport(true);

		rootconf.setSecurity(true); // enable security when remote comm.

		throw new RuntimeException("Sorry, no relay available.");
//		return config;
	}
}
