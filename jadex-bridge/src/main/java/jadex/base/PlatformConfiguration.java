package jadex.base;

import jadex.base.RootComponentConfiguration.AWAMECHANISM;
import jadex.base.RootComponentConfiguration.KERNEL;
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
import java.util.logging.Level;

/**
 * Configuration of the platform setup. 
 */
public class PlatformConfiguration
{
	/** ======== Arguments used by starter. ======== **/
	
	/** The name of the platform component (null for auto generation). To use a custom prefix name 
	      and an auto generated postfix the name should end with _* (3 digits) or with _ and an arbitrary number of +, e.g. _++++.  **/	
	public static final String PLATFORM_NAME = "platformname"; // class: String default: "jadex"
	/** The configuration to use. **/	
	public static final String CONFIGURATION_NAME = "configname"; // class: String default: "auto"
	/** Automatically shut down the platform when no user agents are running anymore. **/	
	public static final String AUTOSHUTDOWN = "autoshutdown"; // class: boolean default: false
	/** Tell the starter to use the default platform component implementation (usually no need to change). **/	
	public static final String PLATFORM_COMPONENT = "platformcomponent"; // class: Class default: jadex.platform.service.cms.PlatformComponent.class
	
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
	public static final String DHT_PROVIDE = "dhtprovide";
	
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
		RESERVED.add(CONFIGURATION_NAME);
		RESERVED.add(PLATFORM_NAME);
		RESERVED.add(COMPONENT_FACTORY);
		RESERVED.add(PLATFORM_COMPONENT);
		RESERVED.add(AUTOSHUTDOWN);
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
	
	/** Platform model. Used to extract default values. */
	private IModelInfo	model;
	/** Name of the configured configuration **/
	private ConfigurationInfo	configurationInfo;


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
	 * Returns a PlatformConfiguration with the default parameters.
	 * @return
	 */
	public static PlatformConfiguration getDefault()
	{
		PlatformConfiguration config = new PlatformConfiguration();
//		config.setPlatformName("jadex");
		config.setPlatformName(null);
		config.setConfigurationName("auto");
		config.setAutoShutdown(false);
		config.setPlatformComponent(SReflect.classForName0("jadex.platform.service.cms.PlatformComponent", null));
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setWelcome(true);
		rootConfig.setGui(true);
		rootConfig.setCliConsole(true);
		rootConfig.setSaveOnExit(true);
		rootConfig.setJccPlatforms(null);
		rootConfig.setLogging(false);
		rootConfig.setLoggingLevel(Level.SEVERE);
		rootConfig.setThreadpoolDefer(true);
		rootConfig.setPersist(false);
		rootConfig.setUniqueIds(true);
		
		rootConfig.setChat(true);
		
		rootConfig.setAwareness(true);
		rootConfig.setAwaMechanisms(AWAMECHANISM.broadcast, AWAMECHANISM.multicast, AWAMECHANISM.message, AWAMECHANISM.relay, AWAMECHANISM.local);
		rootConfig.setAwaDelay(20000);
		rootConfig.setAwaIncludes("");
		rootConfig.setAwaExcludes("");
		
		rootConfig.setBinaryMessages(true);
		rootConfig.setStrictCom(false);
		rootConfig.setPrintPass(true);
		
		rootConfig.setLocalTransport(true);
		rootConfig.setTcpTransport(false);
		rootConfig.setTcpPort(0);
		rootConfig.setNioTcpTransport(true);
		rootConfig.setNioTcpPort(0);
		rootConfig.setRelayTransport(true);
//		rootConfig.setRelayAddress("jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS");
//			rootConfig.setRelaySecurity(true);
		rootConfig.setSslTcpTransport(false);
		rootConfig.setSslTcpPort(0);
		
		rootConfig.setWsPublish(false);
		rootConfig.setRsPublish(false);
		rootConfig.setKernels(KERNEL.multi);
		rootConfig.setMavenDependencies(false);
		rootConfig.setSensors(false);
		rootConfig.setThreadpoolClass(null);
		rootConfig.setContextServiceClass(null);
		
		rootConfig.setMonitoringComp(true);
		rootConfig.setDf(true);
		rootConfig.setClock(true);
		rootConfig.setMessage(true);
		rootConfig.setSimul(true);
		rootConfig.setFiletransfer(true);
		rootConfig.setMarshal(true);
		rootConfig.setSecurity(true);
		rootConfig.setLibrary(true);
		rootConfig.setSettings(true);
		rootConfig.setContext(true);
		rootConfig.setAddress(true);
		rootConfig.setDhtProvide(false);
		return config;
	}

	/**
	 * Returns a PlatformConfiguration with the default parameters.
	 * @return
	 */
	public static PlatformConfiguration getAndroidDefault()
	{
		PlatformConfiguration config = getDefault();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setGui(false);
		rootConfig.setChat(false);
		rootConfig.setKernels(KERNEL.component, KERNEL.micro, KERNEL.bpmn, KERNEL.v3);
		rootConfig.setLoggingLevel(Level.INFO);
//		config.setDebugFutures(true);
		return config;
	}
	
	/**
	 * Returns a minimal platform configuration without any network connectivity.
	 * @return
	 */
	public static PlatformConfiguration getMinimal()
	{
		PlatformConfiguration config = getDefault();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setWelcome(false);
		rootConfig.setGui(false);
		rootConfig.setCliConsole(false);
		
		rootConfig.setChat(false);
		
		rootConfig.setAwareness(false);
		rootConfig.setAwaMechanisms();
		
		rootConfig.setLocalTransport(true); // needed by message
		rootConfig.setNioTcpTransport(false);
		rootConfig.setRelayTransport(false);
		rootConfig.setSslTcpTransport(false);
		
		rootConfig.setKernels(KERNEL.micro);
//		rootConfig.setThreadpoolClass(null);
//		rootConfig.setContextServiceClass(null);
		
		rootConfig.setMonitoringComp(false);
		rootConfig.setDf(false);
		rootConfig.setClock(true);
		rootConfig.setMessage(true); // needed by rms
		rootConfig.setSimul(false);
		rootConfig.setFiletransfer(false);
		rootConfig.setMarshal(true);
		rootConfig.setSecurity(false);
		rootConfig.setLibrary(true); // needed by micro
		rootConfig.setSettings(true);
		rootConfig.setContext(true);
		rootConfig.setAddress(true);
		rootConfig.setDhtProvide(false);
		return config;
	}
	
	/**
	 * Returns a minimal platform configuration that communicates via relay.
	 * @return
	 */
	public static PlatformConfiguration getMinimalRelayAwareness()
	{
		PlatformConfiguration config = getMinimal();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		
		rootConfig.setAwareness(true);
		rootConfig.setAwaMechanisms(AWAMECHANISM.relay);
		rootConfig.setRelayTransport(true);
		
		rootConfig.setKernels(KERNEL.micro);
		
		return config;
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
	private PlatformConfiguration(String[] args)
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
	 * Sets the platform model to extract configuration values from it.
	 * @param model
	 */
	public void setPlatformModel(IModelInfo model)
	{
		this.model = model;
		configurationInfo = getConfigurationInfo(model);
		this.rootconfig.setModel(model);
	}
	
	/**
	 * Generic setter for cmd args.
	 * @param key
	 * @param value
	 */
	public void setValue(String key, Object value) {
		this.cmdargs.put(key, value);
	}
	
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
	public Object getValue(String key) {
		
		Object val = cmdargs.get(key);
		if(val==null && model != null && configurationInfo != null)
		{
			val = getArgumentValueFromModel(key);
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
		
		return val;
	}
	
	// ---- getter/setter for individual properties ----
	
	/** Get the platform name. */
	public String getPlatformName()
	{
		return (String)getValue(PLATFORM_NAME);
	}
	/** Set the platform name. */
	public void setPlatformName(String value)
	{
		setValue(PLATFORM_NAME, value);
	}

	/** Get the configuration name. */
	public String getConfigurationName()
	{
		return (String)getValue(CONFIGURATION_NAME);
	}
	/** Set the configuration name. */
	public void setConfigurationName(String value)
	{
		setValue(CONFIGURATION_NAME, value);
	}

	/** Get autoshutdown flag. */
	public boolean getAutoShutdown()
	{
		return Boolean.TRUE.equals(getValue(AUTOSHUTDOWN));
	}
	/** Set autoshutdown flag. */
	public void setAutoShutdown(boolean value)
	{
		setValue(AUTOSHUTDOWN, value);
	}
	
	/** Get platform component. */
	public Class getPlatformComponent()
	{
		return (Class)getValue(PLATFORM_COMPONENT);
	}
	/** Set platform component. */
	public void setPlatformComponent(Class value)
	{
		setValue(PLATFORM_COMPONENT, value);
	}
	
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
	 * @param value Path to configuration file
	 */
	public void setConfigurationFile(String value)
	{
		setValue(CONFIGURATION_FILE, value);
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
		setValue(MONITORING, level);
	}
	/**
	 * Get the monitoring level.
	 * @return
	 */
	public PublishEventLevel getMonitoring() {
		return (PublishEventLevel)getValue(MONITORING);
	}
	
	/**
	 * Set the persist flag.
	 * @param value
	 */
	public void setPersist(boolean value) {
		setValue(PERSIST, value);
	}
	/**
	 * Get the persist flag.
	 * @return boolean
	 */
	public boolean getPersist() {
		return (Boolean)getValue(PERSIST);
	}
	
	/**
	 * Set the debug futures flag.
	 * @param value
	 */
	public void setDebugFutures(boolean value) {
		setValue(DEBUGFUTURES, value);
	}
	/**
	 * Get the debug futures flag.
	 * @return
	 */
	public boolean getDebugFutures() {
		return Boolean.TRUE.equals(getValue(DEBUGFUTURES));
	}
	
	/**
	 * Set the debug services flag.
	 * @param value
	 */
	public void setDebugServices(boolean value) {
		setValue(DEBUGSERVICES, value);
	}
	/**
	 * Get the debug services flag.
	 * @return
	 */
	public boolean getDebugServices() {
		return Boolean.TRUE.equals(getValue(DEBUGSERVICES));
	}
	
	/**
	 * Set the debug steps flag.
	 * @param value
	 */
	public void setDebugSteps(boolean value) {
		setValue(DEBUGSTEPS, value);
	}
	/**
	 * Get the debug steps flag.
	 * @return
	 */
	public boolean getDebugSteps() {
		return Boolean.TRUE.equals(getValue(DEBUGSTEPS));
	}
	
	/**
	 * Set the no stack compaction flag.
	 * @param value
	 */
	public void setNoStackCompaction(boolean value) {
		setValue(NOSTACKCOMPACTION, value);
	}
	/**
	 * Get the no stack compaction flag.
	 * @return
	 */
	public boolean getNoStackCompaction() {
		return Boolean.TRUE.equals(getValue(NOSTACKCOMPACTION));
	}
	
	/**
	 * Set the OPENGL flag.
	 * @param value
	 */
	public void setOpenGl(boolean value) {
		setValue(OPENGL, value);
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
		return Boolean.TRUE.equals(getValue(OPENGL));
	}
	
	/**
	 * Set the DHT flag.
	 * @param value
	 */
	public void setDht(boolean value) {
		setValue(DHT, value);
	}
	/**
	 * Get the DHT flag.
	 * @return
	 */
	public boolean getDht() {
		return Boolean.TRUE.equals(getValue(DHT));
	}
	
	/**
	 * Set the provide DHT flag.
	 * @param value
	 */
	public void setDhtProvide(boolean value) {
//		setValue(DHT_PROVIDE, value);
		rootconfig.setDhtProvide(value);
	}
	/**
	 * Get the provide DHT flag.
	 * @return
	 */
	public boolean getDhtProvide() {
//		return Boolean.TRUE.equals(getValue(DHT_PROVIDE));
		return rootconfig.getDhtProvide();
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
	
	/**  Puts parsed value into component args to be available at instance. */
//	public void putArgumentValue(String name, Object val)
//	{
//		rootconfig.setValue(name, val);
//	}

	private Object getArgumentValueFromModel(String name)
	{
		Object val = null;
				
		boolean	found	= false;
		// first try to get the value from choosen configuration
		if(configurationInfo!=null)
		{
			UnparsedExpression[]	upes	= configurationInfo.getArguments();
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
	private ConfigurationInfo	getConfigurationInfo(IModelInfo model)
	{
		String	configname	= getConfigurationName();//(String)cmdargs.get(CONFIGURATION_NAME);
		if(configname==null)
		{
			Object	val	= null;
			IArgument	arg	= model.getArgument(CONFIGURATION_NAME);
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

	
	// -----------------------------
	// 		Argument Parsing
	// -----------------------------
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return PlatformConfiguration
	 */
//	public static PlatformConfiguration processArgs(Map<String, String> args)
//	{
//		PlatformConfiguration config = new PlatformConfiguration(args);
//		if(args!=null)
//		{
//			for(Map.Entry<String, String> entry: args.entrySet())
//			{
//				parseArg(entry.getKey(), entry.getValue(), config);
//			}
//		}
//		return config;
//	}

	/**
	 *  Create a platform configuration.
	 *  @param args The command line arguments.
	 *  @return PlatformConfiguration
	 */
	public static PlatformConfiguration processArgs(String args)
	{
		return processArgs(args.split("\\s+"));
	}

	/**
	 *  Create a platform configuration.
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
			// if not reserved, value is parsed and written to root config.
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
		else if (MONITORING.equals(key)) 
		{
			Object tmpmoni = config.getValue(PlatformConfiguration.MONITORING);
			PublishEventLevel moni = PublishEventLevel.OFF;
			if(tmpmoni instanceof Boolean)
			{
				moni = ((Boolean)tmpmoni).booleanValue()? PublishEventLevel.FINE: PublishEventLevel.OFF;
			}
			else if(tmpmoni instanceof String)
			{
				moni = PublishEventLevel.valueOf((String)tmpmoni);
			}
			else if(tmpmoni instanceof PublishEventLevel)
			{
				moni = (PublishEventLevel)tmpmoni;
			}
			config.setMonitoring(moni);
		}
		else
		{
			config.setValue(key, value);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");

		Set<Map.Entry<String, Object>> entries = cmdargs.entrySet();

		for (Map.Entry<String, Object> arg: entries) {
			if (!(arg.getValue() == null || arg.getValue().equals(false))) {
				sb.append(arg.getKey());
				sb.append(": ");
				sb.append(arg.getValue());
				sb.append("\n");
			}
		}

		entries = rootconfig.getArgs().entrySet();

		for (Map.Entry<String, Object> arg: entries) {
			if (!(arg.getValue() == null || arg.getValue().equals(false))) {
				sb.append(arg.getKey());
				sb.append(": ");
				sb.append(arg.getValue());
				sb.append("\n");
			}
		}

		sb.append(rootconfig.toString());
		return sb.toString();
	}

	/**
	 * Enhance this config with given other config.
	 * Will overwrite all values that are set in the other config.
	 * @param other
	 */
	public void enhanceWith(PlatformConfiguration other) {
		for (Map.Entry<String, Object> entry : other.cmdargs.entrySet()) {
			this.setValue(entry.getKey(), entry.getValue());
		}
		rootconfig.enhanceWith(other.rootconfig);
	}

	/**
	 * Checks this configuration for consistency errors.
	 */
	protected void checkConsistency() {
		rootconfig.checkConsistency();
	}
}
