package jadex.base;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Configuration of the root platform component. 
 */
public class RootComponentConfiguration
{
	/** Tell starter to print welcome message. **/
	public static final String WELCOME = "welcome"; // class: boolean default: true
	
	// ----- arguments set by starter for root or platform component -----

	/** The IPlatformComponentAccess instance **/
	public static final String	PLATFORM_ACCESS	= "platformaccess";
	/** The component factory instance. */
	public static final String	COMPONENT_FACTORY	= PlatformConfiguration.COMPONENT_FACTORY;
	/** The saved program arguments. **/
	public static final String PROGRAM_ARGUMENTS = "programarguments"; // class: String[] default: emptyvalue
	
	/** ======== Arguments used by platform components. ======== **/
	
	/** Start the JCC agent to open the platform GUI? **/
	public static final String GUI = "gui"; // class: boolean default: true
	/** Start the platform with command line interface (cli) activated?  (requires Jadex Pro add-on) **/
	public static final String CLI = "cli"; // class: boolean default: true
	/** Start cli with console in or not **/
	public static final String CLICONSOLE = "cliconsole"; // class: boolean default: true
	/** Save platform settings on shutdown? **/
	public static final String SAVEONEXIT = "saveonexit"; // class: boolean default: true
	/** Open JCC for specific remote platforms. **/
	public static final String JCCPLATFORMS = "jccplatforms"; // class: String default: null
	/** Enable verbose logging (shortcut for setting logging_level to info). **/
	public static final String LOGGING = "logging"; // class: boolean default: false
	/** Logging level for platform. **/
	public static final String LOGGING_LEVEL = "logging_level"; // class: java.util.logging.Level default: java.util.logging.Level.SEVERE
	/** Use simulation execution mode? **/
	public static final String SIMULATION = "simulation"; // class: Boolean default: emptyvalue
	/** Use asynchronous execution mode? **/
	public static final String ASYNCEXECUTION = "asyncexecution"; // class: Boolean default: emptyvalue
	/** Flag to enable component persistence. **/
	public static final String PERSIST = "persist"; // class: boolean default: false
	/** Flag if CIDs may be reused (true for not). **/
	public static final String UNIQUEIDS = "uniqueids"; // class: boolean default: true
	/** Flag for deferring thread creation/deletion in thread pool **/
	public static final String THREADPOOLDEFER = "threadpooldefer"; // class: boolean default: true
	
	
	/** Additional library paths (classpath entries) for loading agent models and classes. **/
	public static final String LIBPATH = "libpath"; // class: String default: 
	/** The base classloader. **/
	public static final String BASECLASSLOADER = "baseclassloader"; // class: ClassLoader default: 
			
	/** Start the chat agent for instant messaging and file transfer with user Jadex users. **/
	public static final String CHAT = "chat"; // class: boolean default: true
	
	/** Start the awareness agent (awa) for automatic platform discovery. **/
	public static final String AWARENESS = "awareness"; // class: boolean default: true
	/** Specify the awareness agent discovery mechanisms (comma separated). **/
	public static final String AWAMECHANISMS = "awamechanisms"; // class: String default: "Broadcast, Multicast, Message, Relay, Local"
	/** The awareness delay in milliseconds **/
	public static final String AWADELAY = "awadelay"; // class: long default: 20000
	/** Include entries for awareness agent. **/
	public static final String AWAINCLUDES = "awaincludes"; // class: String default: ""
	/** Exclude entries for awareness agent. **/
	public static final String AWAEXCLUDES = "awaexcludes"; // class: String default: ""
	
	/** Use a compact binary message format instead of XML by default. **/
	public static final String BINARYMESSAGES = "binarymessages"; // class: boolean default: true
	
	/** Fail on recoverable message decoding errors instead of ignoring. **/
	public static final String STRICTCOM = "strictcom"; // class: boolean default: false
			
	/** Flag if the platform should be protected with password.
	      If a value is provided this value overrides the settings value. **/
	public static final String USEPASS = "usepass"; // class: Boolean default: emptyvalue
	/** Flag if the platform password should be printed to the console. **/
	public static final String PRINTPASS = "printpass"; // class: boolean default: true
	/** Flag if trusted lan should be used. **/
	public static final String TRUSTEDLAN = "trustedlan"; // class: Boolean default: emptyvalue
	/** Network name. **/
	public static final String NETWORKNAME = "networkname"; // class: String default: emptyvalue
	/** Network pass. **/
	public static final String NETWORKPASS = "networkpass"; // class: String default: emptyvalue
	/** Virtual names that are used for authentication **/
	public static final String VIRTUALNAMES = "virtualnames"; // class: java.util.Map default: emptyvalue
	/** The message validity duration (in minutes) **/
	public static final String VALIDITYDURATION = "validityduration"; // class: long default: 
	
	/** Flag if local transport is enabled. **/
	public static final String LOCALTRANSPORT = "localtransport"; // class: boolean default: true
	/** Flag if tcp transport is enabled. **/
	public static final String TCPTRANSPORT = "tcptransport"; // class: boolean default: false
	/** Port for TCP transport. **/
	public static final String TCPPORT = "tcpport"; // class: int default: 9876
	/** Flag if niotcp transport is enabled. **/
	public static final String NIOTCPTRANSPORT = "niotcptransport"; // class: boolean default: true
	/** Port for NIOTCP transport. **/
	public static final String NIOTCPPORT = "niotcpport"; // class: int default: 8765
	/** Flag if relay transport is enabled. **/
	public static final String RELAYTRANSPORT = "relaytransport"; // class: boolean default: true
	/** Address(es) for relay transport (one or more addresses separated by commas). **/
	public static final String RELAYADDRESS = "relayaddress"; // class: String default: jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS
	/** Flag if relay should use HTTPS for receiving messages. **/
	public static final String RELAYSECURITY = "relaysecurity"; // class: boolean default: $args.relayaddress.indexOf("https://")==-1 ? false : true
	/** Flag if only awareness messages should be sent through relay. **/
	public static final String RELAYAWAONLY = "relayawaonly"; // class: boolean default: false
	/** Flag if ssltcp transport should enabled (requires Jadex Pro add-on). **/
	public static final String SSLTCPTRANSPORT = "ssltcptransport"; // class: boolean default: false
	/** Port for SSL TCP transport. **/
	public static final String SSLTCPPORT = "ssltcpport"; // class: int default: 44334
	
	/** Flag if web service publishing is enabled. **/
	public static final String WSPUBLISH = "wspublish"; // class: boolean default: false
	
	/** Flag if rest service publishing is enabled. **/
	public static final String RSPUBLISH = "rspublish"; // class: boolean default: false
	
	/** The name(s) of kernel(s) to load (separated by comma).
	      Currently supports 'component', 'micro', 'bpmn', 'bdi', 'gpmn' and 'application' kernel.
	      Alternatively, the 'multi' can be used to start any available kernels on demand.
	      Specifying 'all' will start all available kernels directly. **/
	public static final String KERNELS = "kernels"; // class: String default: "multi"
	
	/** <argument name="platform_shutdown_time">1000</argument> **/
	
	/** Flag to enable the Maven dependency service (requires Jadex Pro add-on). **/
	public static final String MAVEN_DEPENDENCIES = "maven_dependencies"; // class: boolean default: false
	
	/** Flag if global monitoring is turned on.  **/
	public static final String MONITORINGCOMP = "monitoringcomp"; // class: boolean default: true
	
	/** Flag if sensors are turned on.  **/
	public static final String SENSORS = "sensors"; // class: boolean default: false
	
	/** Optionally provide alternative thread pool implementation.  **/
	public static final String THREADPOOLCLASS = "threadpoolclass"; // class: String default: null
	
	/** Optionally provide alternative context service implementation.  **/
	public static final String CONTEXTSERVICECLASS = "contextserviceclass"; // class: String default: null
	
	
	/** Flag if df component and service should be started.  **/
	public static final String DF = "df"; // class: boolean default: true
	/** Flag if clock component and service should be started.  **/
	public static final String CLOCK = "clock"; // class: boolean default: true
	/** Flag if message component and service should be started.  **/
	public static final String MESSAGE = "message"; // class: boolean default: true
	/** Flag if simulation component and service should be started.  **/
	public static final String SIMUL = "simul"; // class: boolean default: true
	/** Flag if filetransfer component and service should be started.  **/
	public static final String FILETRANSFER = "filetransfer"; // class: boolean default: true
	/** Flag if marshal component and service should be started.  **/
	public static final String MARSHAL = "marshal"; // class: boolean default: true
	/** Flag if security component and service should be started.  **/
	public static final String SECURITY = "security"; // class: boolean default: true
	/** Flag if library component and service should be started.  **/
	public static final String LIBRARY = "library"; // class: boolean default: true
	/** Flag if settings component and service should be started.  **/
	public static final String SETTINGS = "settings"; // class: boolean default: true
	/** Flag if context component and service should be started.  **/
	public static final String CONTEXT = "context"; // class: boolean default: true
	/** Flag if address component and service should be started.  **/
	public static final String ADDRESS = "address"; // class: boolean default: true
	/** Flag if dht storage ring should be provided. **/
	public static final String DHT_PROVIDE = PlatformConfiguration.DHT_PROVIDE; // class: boolean default: false
	
	/**
	 * Kernel names enum.
	 */
	public enum KERNEL {
		component,
		micro,
		bpmn,
		v3,
		bdi,
		bdibpmn,
		multi
	}
	
	/**
	 * Discovery names enum.
	 */
	public enum AWAMECHANISM {
		broadcast,
		multicast,
		message,
		relay,
		local
	}

	/** All configured parameters as map. **/
	private Map<String, Object>	rootargs;
	
	/** The activated kernels. **/
	private KERNEL[]	kernels;

	/** The activated awareness machanisms. **/
	private AWAMECHANISM[]	awamechanisms;
	
	/**
	 * Create a new configuration.
	 */
	public RootComponentConfiguration()
	{
		rootargs = new HashMap<String, Object>();	// Arguments of root component (platform)
	}
	
	/**
	 * Copy constructor
	 * @param source
	 */
	public RootComponentConfiguration(RootComponentConfiguration source)
	{
		rootargs = new HashMap<String, Object>(source.rootargs);
	}

	/**
	 * Set a value in the root component configuration
	 * @param key a key from the constants in this class.
	 * @param val the value
	 */
	public void setValue(String key, Object val)
	{
		rootargs.put(key, val);
	}
	
	/**
	 * Returns a value of a given configuration parameter.
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(String key) {
		return rootargs.get(key);
	}

	public Map<String, Object> getArgs()
	{
		return rootargs;
	}
	
	/**
	 * Set program arguments to be available at runtime.
	 * @param args
	 */
	protected void setProgramArguments(String[] args)
	{
		setValue(PROGRAM_ARGUMENTS, args);
	}
	
//	// internal
	public  boolean getWelcome()
	{
		return Boolean.TRUE.equals(getValue(WELCOME));
	}
	public  void setWelcome(boolean value)
	{
		setValue(WELCOME, value);
	}

	//	// internal
//	public  IPlatformComponentAccess getPlatformAccess()
//	{
//		return (IPlatformComponentAccess)getValue(PLATFORM_ACCESS);
//	}
	/**
	 * Set the platform access.
	 * @param value
	 */
	protected  void setPlatformAccess(IPlatformComponentAccess value)
	{
		setValue(PLATFORM_ACCESS, value);
	}
//
//	// internal
//	public  IComponentFactory getComponentFactory()
//	{
//		return (IComponentFactory)getValue(COMPONENT_FACTORY);
//	}
	/**
	 * Set the component factory.
	 * @param value
	 */
	protected  void setComponentFactory(IComponentFactory value)
	{
		setValue(COMPONENT_FACTORY, value);
	}


	// individual getters/setters

	public boolean getGui()
	{
		return Boolean.TRUE.equals(getValue(GUI));
	}

	public void setGui(boolean value)
	{
		setValue(GUI, value);
	}

	public boolean getCli()
	{
		return Boolean.TRUE.equals(getValue(CLI));
	}

	public void setCli(boolean value)
	{
		setValue(CLI, value);
	}

	public boolean getCliConsole()
	{
		return Boolean.TRUE.equals(getValue(CLICONSOLE));
	}

	public void setCliConsole(boolean value)
	{
		setValue(CLICONSOLE, value);
	}

	public boolean getSaveOnExit()
	{
		return Boolean.TRUE.equals(getValue(SAVEONEXIT));
	}

	public void setSaveOnExit(boolean value)
	{
		setValue(SAVEONEXIT, value);
	}

	public String getJccPlatforms()
	{
		return (String)getValue(JCCPLATFORMS);
	}

	public void setJccPlatforms(String value)
	{
		setValue(JCCPLATFORMS, value);
	}

	public boolean getLogging()
	{
		return Boolean.TRUE.equals(getValue(LOGGING));
	}

	public void setLogging(boolean value)
	{
		setValue(LOGGING, value);
	}

	public Level getLoggingLevel()
	{
		return (Level)getValue(LOGGING_LEVEL);
	}

	public void setLoggingLevel(Level value)
	{
		setValue(LOGGING_LEVEL, value);
	}

	public boolean getSimulation()
	{
		return Boolean.TRUE.equals(getValue(SIMULATION));
	}

	public void setSimulation(boolean value)
	{
		setValue(SIMULATION, value);
	}

	public boolean getAsyncExecution()
	{
		return Boolean.TRUE.equals(getValue(ASYNCEXECUTION));
	}

	public void setAsyncExecution(boolean value)
	{
		setValue(ASYNCEXECUTION, value);
	}

	public boolean getPersist()
	{
		return Boolean.TRUE.equals(getValue(PERSIST));
	}

	public void setPersist(boolean value)
	{
		setValue(PERSIST, value);
	}

	public boolean getUniqueIds()
	{
		return Boolean.TRUE.equals(getValue(UNIQUEIDS));
	}

	public void setUniqueIds(boolean value)
	{
		setValue(UNIQUEIDS, value);
	}

	public boolean getThreadpoolDefer()
	{
		return Boolean.TRUE.equals(getValue(THREADPOOLDEFER));
	}

	public void setThreadpoolDefer(boolean value)
	{
		setValue(THREADPOOLDEFER, value);
	}

	public String getLibPath()
	{
		return (String)getValue(LIBPATH);
	}

	public void setLibPath(String value)
	{
		setValue(LIBPATH, value);
	}

	public ClassLoader getBaseClassloader()
	{
		return (ClassLoader)getValue(BASECLASSLOADER);
	}

	public void setBaseClassloader(ClassLoader value)
	{
		setValue(BASECLASSLOADER, value);
	}

	public boolean getChat()
	{
		return Boolean.TRUE.equals(getValue(CHAT));
	}

	public void setChat(boolean value)
	{
		setValue(CHAT, value);
	}

	public boolean getAwareness()
	{
		return Boolean.TRUE.equals(getValue(AWARENESS));
	}

	public void setAwareness(boolean value)
	{
		setValue(AWARENESS, value);
	}

	public AWAMECHANISM[] getAwaMechanisms()
	{
		return awamechanisms;
	}

	public void setAwaMechanisms(AWAMECHANISM... values)
	{
		awamechanisms = values;
		StringBuilder sb = new StringBuilder();
		String semi = "";
		for(int i = 0; i < values.length; i++)
		{
			sb.append(semi);
			sb.append(values[i].name());
			semi = ",";
		}
		setValue(AWAMECHANISMS, sb.toString());
	}

	public long getAwaDelay()
	{
		return (Long)getValue(AWADELAY);
	}

	public void setAwaDelay(long value)
	{
		setValue(AWADELAY, value);
	}

	public String getAwaIncludes()
	{
		return (String)getValue(AWAINCLUDES);
	}

	public void setAwaIncludes(String value)
	{
		setValue(AWAINCLUDES, value);
	}

	public String getAwaExcludes()
	{
		return (String)getValue(AWAEXCLUDES);
	}

	public void setAwaExcludes(String value)
	{
		setValue(AWAEXCLUDES, value);
	}

	public boolean getBinaryMessages()
	{
		return Boolean.TRUE.equals(getValue(BINARYMESSAGES));
	}

	public void setBinaryMessages(boolean value)
	{
		setValue(BINARYMESSAGES, value);
	}

	public boolean getStrictCom()
	{
		return Boolean.TRUE.equals(getValue(STRICTCOM));
	}

	public void setStrictCom(boolean value)
	{
		setValue(STRICTCOM, value);
	}

	public boolean getUsePass()
	{
		return Boolean.TRUE.equals(getValue(USEPASS));
	}

	public void setUsePass(boolean value)
	{
		setValue(USEPASS, value);
	}

	public boolean getPrintPass()
	{
		return Boolean.TRUE.equals(getValue(PRINTPASS));
	}

	public void setPrintPass(boolean value)
	{
		setValue(PRINTPASS, value);
	}

	public boolean getTrustedLan()
	{
		return Boolean.TRUE.equals(getValue(TRUSTEDLAN));
	}

	public void setTrustedLan(boolean value)
	{
		setValue(TRUSTEDLAN, value);
	}

	public String getNetworkName()
	{
		return (String)getValue(NETWORKNAME);
	}

	public void setNetworkName(String value)
	{
		setValue(NETWORKNAME, value);
	}

	public String getNetworkPass()
	{
		return (String)getValue(NETWORKPASS);
	}

	public void setNetworkPass(String value)
	{
		setValue(NETWORKPASS, value);
	}

	public Map getVirtualNames()
	{
		return (Map)getValue(VIRTUALNAMES);
	}

	public void setVirtualNames(Map value)
	{
		setValue(VIRTUALNAMES, value);
	}

	public long getValidityDuration()
	{
		return (Long)getValue(VALIDITYDURATION);
	}

	public void setValidityDuration(long value)
	{
		setValue(VALIDITYDURATION, value);
	}

	public boolean getLocalTransport()
	{
		return Boolean.TRUE.equals(getValue(LOCALTRANSPORT));
	}

	public void setLocalTransport(boolean value)
	{
		setValue(LOCALTRANSPORT, value);
	}

	public boolean getTcpTransport()
	{
		return Boolean.TRUE.equals(getValue(TCPTRANSPORT));
	}

	public void setTcpTransport(boolean value)
	{
		setValue(TCPTRANSPORT, value);
	}

	public int getTcpPort()
	{
		return (Integer)getValue(TCPPORT);
	}

	public void setTcpPort(int value)
	{
		setValue(TCPPORT, value);
	}

	public boolean getNioTcpTransport()
	{
		return Boolean.TRUE.equals(getValue(NIOTCPTRANSPORT));
	}

	public void setNioTcpTransport(boolean value)
	{
		setValue(NIOTCPTRANSPORT, value);
	}

	public int getNioTcpPort()
	{
		return (Integer)getValue(NIOTCPPORT);
	}

	public void setNioTcpPort(int value)
	{
		setValue(NIOTCPPORT, value);
	}

	public boolean getRelayTransport()
	{
		return Boolean.TRUE.equals(getValue(RELAYTRANSPORT));
	}

	public void setRelayTransport(boolean value)
	{
		setValue(RELAYTRANSPORT, value);
	}

	public String getRelayAddress()
	{
		return (String)getValue(RELAYADDRESS);
	}

	public void setRelayAddress(String value)
	{
		setValue(RELAYADDRESS, value);
	}

	public boolean getRelaySecurity()
	{
		return Boolean.TRUE.equals(getValue(RELAYSECURITY));
	}

	public void setRelaySecurity(boolean value)
	{
		setValue(RELAYSECURITY, value);
	}

	public boolean getSSLTCPTRANSPORT()
	{
		return Boolean.TRUE.equals(getValue(SSLTCPTRANSPORT));
	}

	public void setSslTcpTransport(boolean value)
	{
		setValue(SSLTCPTRANSPORT, value);
	}

	public int getSslTcpPort()
	{
		return (Integer)getValue(SSLTCPPORT);
	}

	public void setSslTcpPort(int value)
	{
		setValue(SSLTCPPORT, value);
	}

	public boolean getWsPublish()
	{
		return Boolean.TRUE.equals(getValue(WSPUBLISH));
	}

	public void setWsPublish(boolean value)
	{
		setValue(WSPUBLISH, value);
	}

	public boolean getRsPublish()
	{
		return Boolean.TRUE.equals(getValue(RSPUBLISH));
	}

	public void setRsPublish(boolean value)
	{
		setValue(RSPUBLISH, value);
	}

	public KERNEL[] getKernels()
	{
		return kernels;
	}

	public void setKernels(KERNEL... value)
	{
		kernels = value;
		// String[] oldVal = new String[kernels.length];
		StringBuilder sb = new StringBuilder();
		String semi = "";
		for(int i = 0; i < kernels.length; i++)
		{
			sb.append(semi);
			sb.append(kernels[i].name());
			semi = ",";
		}
		setValue(KERNELS, sb.toString());
	}

	public boolean getMavenDependencies()
	{
		return Boolean.TRUE.equals(getValue(MAVEN_DEPENDENCIES));
	}

	public void setMavenDependencies(boolean value)
	{
		setValue(MAVEN_DEPENDENCIES, value);
	}

	public boolean getMonitoringComp()
	{
		return Boolean.TRUE.equals(getValue(MONITORINGCOMP));
	}

	public void setMonitoringComp(boolean value)
	{
		setValue(MONITORINGCOMP, value);
	}

	public boolean getSensors()
	{
		return Boolean.TRUE.equals(getValue(SENSORS));
	}

	public void setSensors(boolean value)
	{
		setValue(SENSORS, value);
	}

	public String getThreadpoolClass()
	{
		return (String)getValue(THREADPOOLCLASS);
	}

	public void setThreadpoolClass(String value)
	{
		setValue(THREADPOOLCLASS, value);
	}

	public String getContextServiceClass()
	{
		return (String)getValue(CONTEXTSERVICECLASS);
	}

	public void setContextServiceClass(String value)
	{
		setValue(CONTEXTSERVICECLASS, value);
	}

	public boolean getDf()
	{
		return Boolean.TRUE.equals(getValue(DF));
	}

	public void setDf(boolean value)
	{
		setValue(DF, value);
	}

	public boolean getClock()
	{
		return Boolean.TRUE.equals(getValue(CLOCK));
	}

	public void setClock(boolean value)
	{
		setValue(CLOCK, value);
	}

	public boolean getMessage()
	{
		return Boolean.TRUE.equals(getValue(MESSAGE));
	}

	public void setMessage(boolean value)
	{
		setValue(MESSAGE, value);
	}

	public boolean getSimul()
	{
		return Boolean.TRUE.equals(getValue(SIMUL));
	}

	public void setSimul(boolean value)
	{
		setValue(SIMUL, value);
	}

	public boolean getFiletransfer()
	{
		return Boolean.TRUE.equals(getValue(FILETRANSFER));
	}

	public void setFiletransfer(boolean value)
	{
		setValue(FILETRANSFER, value);
	}

	public boolean getMarshal()
	{
		return Boolean.TRUE.equals(getValue(MARSHAL));
	}

	public void setMarshal(boolean value)
	{
		setValue(MARSHAL, value);
	}

	public boolean getSecurity()
	{
		return Boolean.TRUE.equals(getValue(SECURITY));
	}

	public void setSecurity(boolean value)
	{
		setValue(SECURITY, value);
	}

	public boolean getLibrary()
	{
		return Boolean.TRUE.equals(getValue(LIBRARY));
	}

	public void setLibrary(boolean value)
	{
		setValue(LIBRARY, value);
	}

	public boolean getSettings()
	{
		return Boolean.TRUE.equals(getValue(SETTINGS));
	}

	public void setSettings(boolean value)
	{
		setValue(SETTINGS, value);
	}

	public boolean getContext()
	{
		return Boolean.TRUE.equals(getValue(CONTEXT));
	}

	public void setContext(boolean value)
	{
		setValue(CONTEXT, value);
	}

	public boolean getAddress()
	{
		return Boolean.TRUE.equals(getValue(ADDRESS));
	}

	public void setAddress(boolean value)
	{
		setValue(ADDRESS, value);
	}

	public boolean getDhtProvide()
	{
		return Boolean.TRUE.equals(getValue(DHT_PROVIDE));
	}

	public void setDhtProvide(boolean value)
	{
		setValue(DHT_PROVIDE, value);
	}


}
