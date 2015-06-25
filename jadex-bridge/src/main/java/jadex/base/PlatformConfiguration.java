package jadex.base;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PlatformConfiguration
{
	//-------- platform data keys --------
	
	/** Flag if copying parameters for local service calls is allowed. */
	public static final String DATA_PARAMETERCOPY = "parametercopy";

	/**  Flag if local timeouts should be realtime (instead of clock dependent). */
	public static final String DATA_REALTIMETIMEOUT = "realtimetimeout";
	
	/** The local service registry data key. */
	public static final String DATA_SERVICEREGISTRY = "serviceregistry";
	
	/** The transport address book data key. */
	public static final String DATA_ADDRESSBOOK = "addressbook";
	
	/** Constant for local default timeout name. */
	public static final String DATA_DEFAULT_LOCAL_TIMEOUT = "default_local_timeout";

	/** Constant for remote default timeout name. */
	public static final String DATA_DEFAULT_REMOTE_TIMEOUT = "default_remote_timeout";
	
	/** Global platform data. */
	protected static final Map<IComponentIdentifier, Map<String, Object>> platformmem = new HashMap<IComponentIdentifier, Map<String, Object>>();
	
	//-------- constants --------

	/** The default platform configuration. */
//	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/Platform.component.xml";
	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/PlatformAgent.class";

	/** The default component factory to be used for platform component. */
//	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.component.ComponentComponentFactory";
	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.micro.MicroAgentFactory";

	/** The configuration file argument. */
	public static final String CONFIGURATION_FILE = "conf";
	
	/** The component factory classname argument. */
	public static final String COMPONENT_FACTORY = "componentfactory";
	
	/** The monitoring flag argument. */
	public static final String MONITORING = "monitoring";

	/** The component flag argument (for starting an additional component). */
	public static final String COMPONENT = "component";
	
	/** The persist flag argument. */
	public static final String PERSIST = "persist";

	/** The default timeout argument. */
	public static final String DEFTIMEOUT = "deftimeout";
	
	/** The debug futures flag argument. */
	public static final String DEBUGFUTURES = "debugfutures";
	
	/** The debug futures services argument. */
	public static final String DEBUGSERVICES = "debugservices";
	
	/** The debug futures services argument. */
	public static final String DEBUGSTEPS = "debugsteps";
	
	/** The stack compaction disable flag argument. */
	public static final String NOSTACKCOMPACTION = "nostackcompaction";
	
	/** The opengl disable flag argument. */
	public static final String OPENGL = "opengl";
	
	/** Flag to enable or disable dht features (service discovery). **/
	public static final String DHT = "dht";
	
	/** Flag to enable or disable dht providing features (service discovery). **/
	public static final String DHT_PROVIDE = "providedht";
	
	/** The reserved platform parameters. Those are (usually) not handled by the root component. */
	public static final Set<String> RESERVED;
	
	
	/** Constant for remote default timeout. */
	private static long DEFAULT_REMOTE_TIMEOUT = SReflect.isAndroid() ? 60000 : 30000;;

	/** Constant for local default timeout. */
	private static long DEFAULT_LOCAL_TIMEOUT = SReflect.isAndroid() ? 60000 : 30000;
	


	static
	{
		// Set deftimeout from environment, if set.
		String	dtoprop	= System.getProperty("jadex.deftimeout", System.getenv("jadex.deftimeout"));
		if(dtoprop!=null)
		{
			System.out.println("Property jadex.deftimeout is deprecated. Use jadex_deftimeout instead.");
		}
		else
		{
			dtoprop	= System.getProperty("jadex_deftimeout", System.getenv("jadex_deftimeout"));
		}
		if(dtoprop!=null)
		{
			DEFAULT_REMOTE_TIMEOUT = (Long.parseLong(dtoprop));
			DEFAULT_LOCAL_TIMEOUT = (Long.parseLong(dtoprop));
			System.out.println("Setting jadex_deftimeout: "+dtoprop);
		}
		
		RESERVED = new HashSet<String>();
		RESERVED.add(CONFIGURATION_FILE);
		RESERVED.add(RootComponentConfiguration.CONFIGURATION_NAME);
		RESERVED.add(RootComponentConfiguration.PLATFORM_NAME);
		RESERVED.add(COMPONENT_FACTORY);
		RESERVED.add(RootComponentConfiguration.PLATFORM_COMPONENT);
		RESERVED.add(RootComponentConfiguration.AUTOSHUTDOWN);
		RESERVED.add(MONITORING);
		RESERVED.add(RootComponentConfiguration.WELCOME);
		RESERVED.add(COMPONENT);
		RESERVED.add(DATA_PARAMETERCOPY);
		RESERVED.add(DATA_REALTIMETIMEOUT);
		RESERVED.add(PERSIST);
		RESERVED.add(DEBUGFUTURES);
		RESERVED.add(DEBUGSERVICES);
		RESERVED.add(DEBUGSTEPS);
		RESERVED.add(NOSTACKCOMPACTION);
		RESERVED.add(OPENGL);
		RESERVED.add(DEFTIMEOUT);
		RESERVED.add(DHT);
		RESERVED.add(DHT_PROVIDE);
	}

	/** Command line arguments. **/
	private Map<String, Object>	cmdargs;
	/** Components to start. **/
	private List<String>	components;
	/** Default platform timeout. **/
	private Long defaultTimeout;
	/** Configuration of the root component. **/
	private RootComponentConfiguration	rootconfig;


	/**
	 * Creates an empty configuration.
	 */
	public PlatformConfiguration()
	{
		cmdargs = new HashMap<String, Object>();	// Starter arguments (required for instantiation of root component)
		rootconfig = new RootComponentConfiguration();
		components = new ArrayList<String>();	// Additional components to start
	}
	
	/**
	 * Copy constructor.
	 */
	public PlatformConfiguration(PlatformConfiguration source) {
		this.cmdargs = new HashMap<String, Object>(cmdargs);
		this.rootconfig = new RootComponentConfiguration(source.rootconfig);
		this.components = new ArrayList<String>(source.components);
	}

	/**
	 * Constructor that creates a new Configuration based on an args object.
	 * (which can be a Map or a String array).
	 * @param args
	 */
	public PlatformConfiguration(Object args)
	{
		this();
		rootconfig.setProgramArguments(args);
	}
	
	/**
	 * Returns the configuration of the root component.
	 * @return RootComponentConfiguration
	 */
	public RootComponentConfiguration getRootConfig()
	{
		return rootconfig;
	}
	
	/**
	 * Generic setter for cmd args.
	 * @param key
	 * @param value
	 */
	public void setCmdArg(String key, Object value) {
		this.cmdargs.put(key, value);
	}
	
	/**
	 * Generic getter for cmd args.
	 * @param key
	 * @return
	 */
	public Object getCmdArg(String key) {
		return this.cmdargs.get(key);
	}
	
	// ---- getter/setter for individual properties ----
	
	/**
	 * Set the default timeout.
	 * @param to timeout in ms.
	 */
	public void setDefaultTimeout(long to)
	{
		defaultTimeout = to;
	}
	/**
	 * Gets the default timeout.
	 * @return timeout in ms.
	 */
	public Long getDefaultTimeout()
	{
		return defaultTimeout;
	}
	
	/**
	 * Get the default timeout for local calls.
	 * @return default timeout in ms.
	 */
	public long getLocalDefaultTimeout() {
		return (defaultTimeout != null) ? defaultTimeout : DEFAULT_LOCAL_TIMEOUT;
	}
	
	/**
	 * Get the default timeout for remote calls.
	 * @return default timeout in ms.
	 */
	public long getRemoteDefaultTimeout() {
		return (defaultTimeout != null) ? defaultTimeout : DEFAULT_REMOTE_TIMEOUT;
	}
	
	/**
	 * Add a component that is started after platform startup.
	 * @param path Path to the component.
	 */
	public void addComponent(String path)
	{
		components.add((String)path);
	}
	/**
	 * Set the list of components to be started at startup.
	 * @param newcomps List of components.
	 */
	public void setComponents(List<String> newcomps) 
	{
		components = newcomps;
	}
	/**
	 * Get the list of components to be started at startup.
	 * @return List of components
	 */
	public List<String> getComponents()
	{
		return components;
	}
	
	/**
	 * Get the component factory.
	 * @return name of component factory
	 */
	public String getComponentFactory()
	{
		return (String)cmdargs.get(COMPONENT_FACTORY)!=null? 
			(String)cmdargs.get(COMPONENT_FACTORY): FALLBACK_COMPONENT_FACTORY;
	}
	
	/**
	 * Set the main configuration file, e.g. path to PlatformAgent.
	 * @param Path to configuration file
	 */
	public void setConfigurationFile(String value)
	{
		setCmdArg(CONFIGURATION_FILE, value);
	}
	/**
	 * Get the main configuration file, e.g. path to PlatformAgent.
	 * @return Path to configuration file
	 */
	public String getConfigurationFile()
	{
		return (String)cmdargs.get(CONFIGURATION_FILE)!=null? 
			(String)cmdargs.get(CONFIGURATION_FILE): FALLBACK_PLATFORM_CONFIGURATION;
	}
	
	/**
	 * Set the monitoring level.
	 * @param level
	 */
	public void setMonitoring(PublishEventLevel level) {
		setCmdArg(MONITORING, level);
	}
	/**
	 * Get the monitoring level.
	 * @return
	 */
	public Object getMonitoring() {
		return getCmdArg(MONITORING);
	}
	
	/**
	 * Set the persist flag.
	 * @param value
	 */
	public void setPersist(boolean value) {
		// TODO unused?
		setCmdArg(PERSIST, value);
	}
	
	/**
	 * Get the persist flag.
	 * @return boolean
	 */
	public boolean getPersist() {
		return (Boolean)getCmdArg(PERSIST);
	}
	
	/**
	 * Set the debug futures flag.
	 * @param value
	 */
	public void setDebugFutures(boolean value) {
		setCmdArg(DEBUGFUTURES, value);
	}
	/**
	 * Get the debug futures flag.
	 * @return
	 */
	public boolean getDebugFutures() {
		return Boolean.TRUE.equals(getCmdArg(DEBUGFUTURES));
	}
	
	/**
	 * Set the debug services flag.
	 * @param value
	 */
	public void setDebugServices(boolean value) {
		setCmdArg(DEBUGSERVICES, value);
	}
	/**
	 * Get the debug services flag.
	 * @return
	 */
	public boolean getDebugServices() {
		return Boolean.TRUE.equals(getCmdArg(DEBUGSERVICES));
	}
	
	/**
	 * Set the debug steps flag.
	 * @param value
	 */
	public void setDebugSteps(boolean value) {
		setCmdArg(DEBUGSTEPS, value);
	}
	/**
	 * Get the debug steps flag.
	 * @return
	 */
	public boolean getDebugSteps() {
		return Boolean.TRUE.equals(getCmdArg(DEBUGSTEPS));
	}
	
	/**
	 * Set the no stack compaction flag.
	 * @param value
	 */
	public void setNoStackCompaction(boolean value) {
		setCmdArg(NOSTACKCOMPACTION, value);
	}
	/**
	 * Get the no stack compaction flag.
	 * @return
	 */
	public boolean getNoStackCompaction() {
		return Boolean.TRUE.equals(getCmdArg(NOSTACKCOMPACTION));
	}
	
	/**
	 * Set the OPENGL flag.
	 * @param value
	 */
	public void setOpenGl(boolean value) {
		setCmdArg(OPENGL, value);
		Class<?> p2d = SReflect.classForName0("jadex.extension.envsupport.observer.perspective.Perspective2D", Starter.class.getClassLoader());
		if(p2d!=null)
		{
			try
			{
				p2d.getField("OPENGL").set(null, Boolean.valueOf(value));
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	/**
	 * Get the OPENGL flag.
	 * @return
	 */
	public boolean getOpenGl() {
		return Boolean.TRUE.equals(getCmdArg(OPENGL));
	}
	
	/**
	 * Set the DHT flag.
	 * @param value
	 */
	public void setDht(boolean value) {
		setCmdArg(DHT, value);
	}
	/**
	 * Get the DHT flag.
	 * @return
	 */
	public boolean getDht() {
		return Boolean.TRUE.equals(getCmdArg(DHT));
	}
	
	/**
	 * Set the provide DHT flag.
	 * @param value
	 */
	public void setDhtProvide(boolean value) {
		setCmdArg(DHT_PROVIDE, value);
	}
	/**
	 * Get the provide DHT flag.
	 * @return
	 */
	public boolean getDhtProvide() {
		return Boolean.TRUE.equals(getCmdArg(DHT_PROVIDE));
	}
	
	/**
	 *  Get a global platform value.
	 *  @param platform The platform name.
	 *  @param key The key.
	 *  @return The value.
	 */
	public static synchronized Object getPlatformValue(IComponentIdentifier platform, String key)
	{
		Object ret = null;
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem!=null)
			ret = mem.get(key);
		return ret;
	}
	
	/**
	 *  Get a global platform value.
	 *  @param platform The platform name.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public static synchronized void putPlatformValue(IComponentIdentifier platform, String key, Object value)
	{
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem==null)
		{
			mem = new HashMap<String, Object>();
			platformmem.put(platform, mem);
		}
		mem.put(key, value);
	}
	
	/**
	 *  Get a global platform value.
	 *  @param platform The platform name.
	 *  @param key The key.
	 *  @return The value.
	 */
	public static synchronized boolean hasPlatformValue(IComponentIdentifier platform, String key)
	{
		boolean ret = false;
		Map<String, Object> mem = platformmem.get(platform.getRoot());
		if(mem!=null)
			ret = mem.containsKey(key);
		return ret;
	}
	
	/**
	 *  Get a global platform value.
	 *  @param platform The platform name.
	 *  @param key The key.
	 *  @param value The value.
	 */
	public static synchronized void removePlatformMemory(IComponentIdentifier platform)
	{
		platformmem.remove(platform.getRoot());
	}
	
	/**
	 *  Get the remote default timeout.
	 */
	public static synchronized long getRemoteDefaultTimeout(IComponentIdentifier platform)
	{
		if(platform==null)
			return DEFAULT_REMOTE_TIMEOUT;
		
		platform = platform.getRoot();
		return hasPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT)? ((Long)getPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT)).longValue(): DEFAULT_REMOTE_TIMEOUT;
	}

	/**
	 *  Get the scaled remote default timeout.
	 */
	public static synchronized long	getScaledRemoteDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		long ret = getRemoteDefaultTimeout(platform);
		return ret==-1 ? -1 : (long)(ret*scale);
	}

	/**
	 *  Get the local default timeout.
	 */
	public static synchronized long getLocalDefaultTimeout(IComponentIdentifier platform)
	{
		if(platform==null)
			return DEFAULT_LOCAL_TIMEOUT;
		
		platform = platform.getRoot();
		return hasPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT)? ((Long)getPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT)).longValue(): DEFAULT_LOCAL_TIMEOUT;
	}

	/**
	 *  Get the scaled local default timeout.
	 */
	public static synchronized long getScaledLocalDefaultTimeout(IComponentIdentifier platform, double scale)
	{
		long ret = getLocalDefaultTimeout(platform);
		return ret==-1 ? -1 : (long)(ret*scale);
	}

	/**
	 *  Set the remote default timeout.
	 */
	public static synchronized void setRemoteDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		putPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT, timeout);
	}

	/**
	 *  Set the local default timeout.
	 */
	public static synchronized void setLocalDefaultTimeout(IComponentIdentifier platform, long timeout)
	{
		putPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT, timeout);
	}
	
	/**
	 *  Check if the real time timeout flag is set for a platform.
	 */
	public static synchronized boolean	isRealtimeTimeout(IComponentIdentifier platform)
	{
		// Hack!!! Should default to false?
		return !Boolean.FALSE.equals(getPlatformValue(platform, PlatformConfiguration.DATA_REALTIMETIMEOUT));
	}
	
	/**
	 *  Check if the parameter copy flag is set for a platform.
	 */
	public static synchronized boolean	isParameterCopy(IComponentIdentifier platform)
	{
		// not equals false to make true the default.
		return !Boolean.FALSE.equals(getPlatformValue(platform, PlatformConfiguration.DATA_PARAMETERCOPY));
	}
	
	/**
	 *  Get an argument value from the command line or the model.
	 *  Also puts parsed value into component args to be available at instance.
	 */
	public Object getArgumentValue(String name, IModelInfo model)
	{
		
		Object val = cmdargs.get(name);
		if(val==null)
		{
			val = getArgumentValueFromModel(name, model);
		}
		else if(val instanceof String)
		{
			// Try to parse value from command line.
			try
			{
				Object newval	= SJavaParser.evaluateExpression((String)val, null);
				if(newval!=null)
				{
					val	= newval;
				}
			}
			catch(RuntimeException e)
			{
			}
		}
		rootconfig.setValue(name, val);
		return val;
	}

	private Object getArgumentValueFromModel(String name, IModelInfo model)
	{
		Object val = null;
		String	configname	= getConfigurationName(model);
		ConfigurationInfo	compConfig	= configname!=null
			? model.getConfiguration(configname) 
				: model.getConfigurations().length>0 ? model.getConfigurations()[0] : null;
				
		boolean	found	= false;
		if(compConfig!=null)
		{
			UnparsedExpression[]	upes	= compConfig.getArguments();
			for(int i=0; !found && i<upes.length; i++)
			{
				if(name.equals(upes[i].getName()))
				{
					found	= true;
					val	= upes[i];
				}
			}
		}
		if(!found)
		{
			 IArgument	arg	= model.getArgument(name);
			 if(arg!=null)
			 {
				val	= arg.getDefaultValue(); 
			 }
		}
		val	= SJavaParser.getParsedValue(val, model.getAllImports(), null, Starter.class.getClassLoader());
//			val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
		return val;
	}
	
	/**
	 * Get the configuration name.
	 */
	public String	getConfigurationName(IModelInfo model)
	{
		String	configname	= (String)cmdargs.get(RootComponentConfiguration.CONFIGURATION_NAME);
		if(configname==null)
		{
			Object	val	= null;
			IArgument	arg	= model.getArgument(RootComponentConfiguration.CONFIGURATION_NAME);
			if(arg!=null)
			{
				val	= arg.getDefaultValue();
			}
			val	= SJavaParser.getParsedValue(val, model.getAllImports(), null, Starter.class.getClassLoader());
//			val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
			configname	= val!=null ? val.toString() : null;
		}
		return configname;
	}

	
	// -----------------------------
	// 		Argument Parsing
	// -----------------------------
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return PlatformConfiguration
	 */
	public static PlatformConfiguration processArgs(Map<String, String> args)
	{
		PlatformConfiguration config = new PlatformConfiguration(args);
		if(args!=null)
		{
			for(Map.Entry<String, String> entry: args.entrySet())
			{
				parseArg(entry.getKey(), entry.getValue(), config);
			}
		}
		return config;
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return PlatformConfiguration
	 */
	public static PlatformConfiguration processArgs(String[] args)
	{
		PlatformConfiguration config = new PlatformConfiguration(args);
		if(args!=null)
		{
			for(int i=0; args!=null && i<args.length; i+=2)
			{
				parseArg(args[i], args[i+1], config);
			}
		}
		return config;
	}
	
	public static void parseArg(String okey, String val, PlatformConfiguration config)
	{
		String key = okey.startsWith("-")? okey.substring(1): okey;
		Object value = val;
		if(!RESERVED.contains(key))
		{
			try
			{
				value = SJavaParser.evaluateExpression(val, null);
			}
			catch(Exception e)
			{
				System.out.println("Argument parse exception using as string: "+key+" \""+val+"\"");
			}
			config.getRootConfig().setValue(key, value);
		}
		else if(COMPONENT.equals(key))
		{
			config.addComponent((String) val);
		}
		else if(DEBUGFUTURES.equals(key) && "true".equals(val))
		{
			config.setDebugFutures(true);
		}
		else if(DEBUGSERVICES.equals(key) && "true".equals(val))
		{
			config.setDebugServices(true);
		}
		else if(DEBUGSTEPS.equals(key) && "true".equals(val))
		{
			config.setDebugSteps(true);
		}
		else if(DEFTIMEOUT.equals(key))
		{
			value = SJavaParser.evaluateExpression(val, null);
//				BasicService.DEFTIMEOUT	= ((Number)val).longValue();
			long to	= ((Number)value).longValue();
//			setLocalDefaultTimeout(platform, to);
//			setRemoteDefaultTimeout(platform, to);
			config.setDefaultTimeout(to);
			
//			BasicService.setRemoteDefaultTimeout(to);
//			BasicService.setLocalDefaultTimeout(to);
//			System.out.println("timeout: "+BasicService.DEFAULT_LOCAL);
		}
		else if(NOSTACKCOMPACTION.equals(key) && "true".equals(val))
		{
			config.setNoStackCompaction(true);
		}
		else if(OPENGL.equals(key) && "false".equals(val))
		{
			config.setOpenGl(false);
			
		}
		// TODO Monitoring parse code here instead of starter?
		else
		{
			config.cmdargs.put(key, value);
		}
	}
}
