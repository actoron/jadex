package jadex.base;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class RootComponentConfiguration
{
	/** ======== Arguments used by starter unless supplied from command line. ======== **/
	
	/** The name of the platform component (null for auto generation). To use a custom prefix name 
	      and an auto generated postfix the name should end with _* (3 digits) or with _ and an arbitrary number of +, e.g. _++++.  **/	
	public static final String PLATFORM_NAME = "platformname"; // class: String default: "jadex"
	/** The configuration to use. **/	
	public static final String CONFIGURATION_NAME = "configname"; // class: String default: "auto"
	/** Automatically shut down the platform when no user agents are running anymore. **/	
	public static final String AUTOSHUTDOWN = "autoshutdown"; // class: boolean default: false
	/** Tell the starter to use the default platform component implementation (usually no need to change). **/	
	public static final String PLATFORM_COMPONENT = "platformcomponent"; // class: Class default: jadex.platform.service.cms.PlatformComponent.class
	/** Tell starter to print welcome message. **/
	public static final String WELCOME = "welcome"; // class: boolean default: true
	/** The saved program arguments. **/
	public static final String PROGRAM_ARGUMENTS = "programarguments"; // class: String[] default: emptyvalue
	
	// ----- arguments set by starter for root or platform component -----
	
	/** The IPlatformComponentAccess instance **/
	public static final String	PLATFORM_ACCESS	= "platformaccess";
	/** The component factory instance. */
	public static final String	COMPONENT_FACTORY	= PlatformConfiguration.COMPONENT_FACTORY;
	
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
	
	
	
	// ----- arguments handled by starter AND root component -----

	private Map<String, Object>	rootargs;
	
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

	public void setProgramArguments(Object args)
	{
		rootargs.put(PROGRAM_ARGUMENTS, args);
	}

	public void setValue(String name, Object val)
	{
		rootargs.put(name, val);
	}

	public Map<String, Object> getArgs()
	{
		return rootargs;
	}

	public void setLoggingLevel(Level level)
	{
		setValue(LOGGING_LEVEL, level);
	}

	public void setWsPublish(boolean value)
	{
		setValue(WSPUBLISH, value);
	}

	public void setRsPublish(boolean value)
	{
		setValue(RSPUBLISH, value);
	}

	public void setBinaryMessages(boolean value)
	{
		setValue(BINARYMESSAGES, value);
	}

	public void setAutoShutdown(boolean value)
	{
		setValue(AUTOSHUTDOWN, value);
	}

	public void setSaveOnExit(boolean value)
	{
		setValue(SAVEONEXIT, value);
	}

	public void setGui(boolean value)
	{
		setValue(GUI, value);
	}

	public void setChat(boolean value)
	{
		setValue(CHAT, value);
	}
	
	

}
