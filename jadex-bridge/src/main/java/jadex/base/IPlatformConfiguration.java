package jadex.base;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import jadex.bridge.modelinfo.IModelInfo;

/**
 *  Interface for platform configuration.
 */
public interface IPlatformConfiguration //extends IStarterConfiguration, IRootComponentConfiguration
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

    //-------- constants --------

    /** The default platform configuration. */
//	String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/Platform.component.xml";
    public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/PlatformAgent.class";
//    String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/PlatformNGAgent.class";

    /** The default component factory to be used for platform component. */
//	String FALLBACK_COMPONENT_FACTORY = "jadex.component.ComponentComponentFactory";
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

//    /** Flag to enable or disable the platform as superpeer. **/
//    public static final String SUPERPEER = "superpeer";
//    
//    /** Flag to enable or disable the platform as supersuperpeer. **/
//    public static final String SUPERSUPERPEER = "supersuperpeer";
//
//    /** Flag to enable or disable the platform as superpeern client. **/
//    public static final String SUPERPEERCLIENT = "superpeerclient";

    /** Flag if exceptions should be printed. */
    public static final String PRINTEXCEPTIONS = "printexceptions";
    
    /** Flag if copying parameters for local service calls is allowed. */
    public static final String PARAMETERCOPY = "parametercopy";

    /**  Flag if local timeouts should be realtime (instead of clock dependent). */
    public static final String REALTIMETIMEOUT = "realtimetimeout";
        
//    /** Constant for local default timeout. */
//    public static long DEFAULT_LOCAL_TIMEOUT = PlatformConfigurationHandler.getDefaultTimeout();
//
//    /** Constant for remote default timeout. */
//    public static long DEFAULT_REMOTE_TIMEOUT = DEFAULT_LOCAL_TIMEOUT;
//
//    /** The reserved platform parameters. Those are (usually) not handled by the root component. */
//    public static final Set<String> RESERVED = PlatformConfigurationHandler.createReserved();
    
    //-------- Kernel constants. --------
  	public static final String KERNEL_COMPONENT = "component";
  	public static final String KERNEL_APPLICATION = "application";
  	public static final String KERNEL_MICRO = "micro";
  	public static final String KERNEL_BPMN = "bpmn";
  	public static final String KERNEL_BDIV3 = "bdiv3";
  	public static final String KERNEL_BDI = "bdi";
  	public static final String KERNEL_BDIBPMN = "bdibpmn";
  	public static final String KERNEL_MULTI = "multi";

  	//-------- Awareness mechanisms. --------
  	/** Uses IPv4 broadcast to announce awareness infos in local networks.
  	 *  Default Port used is 55670. */
  	public static final String AWAMECHANISM_BROADCAST = "broadcast";
  	/** Uses IPv4 Multicast to find other platforms.
  	 *  Default multicast address used is 224.0.0.0, port 5567. */
  	public static final String AWAMECHANISM_MULTICAST = "multicast";
  	/** Message discovery allows detecting other platforms upon message reception.
  	 *  This helps especially if network connection is asymetric, e.g. one platform
  	 *  can find the other (and send messages) but not vice versa.*/
//  	public static final String AWAMECHANISM_MESSAGE = "message";
  	/** The Relay discovery is the most robust discovery variant. It uses an external
  	 *  web server as relay where each platform registers. It is possible to set-up a
  	 *  self-hosted relay server, but per default, https://activecomponents.org/relay is used. */
//  	public static final String AWAMECHANISM_RELAY = "relay";
  	/** The local discovery uses a file-based mechanism to detect platforms running on the same host. */
  	public static final String AWAMECHANISM_LOCAL = "local";
  	/** The Registry mechanism implements a master-slave mechanism, where one
  	 *  platform is the registry. Other platforms that have this mechanism enabled
  	 *  register themselves and the registry distributes awareness info to all
  	 *  registered platforms. All RegistryDiscoveryAgents have to be parameterized
  	 *  with the same ip address (of the registry). */
  	public static final String AWAMECHANISM_REGISTRY = "registry";
  	/** The IP-Scanner discovery mechanism sends out awareness infos to all IP
  	 *  addresses within the local network (using port 55668) */
  	public static final String AWAMECHANISM_SCANNER = "scanner";

      /** Tell starter to print welcome message. **/
      public static final String	WELCOME				= "welcome";								// class:
      // boolean
      // default:
      // true

      // ----- arguments set by starter for root or platform component -----

//      /** The IPlatformComponentAccess instance **/
//      public static final String	PLATFORM_ACCESS		= "platformaccess";

//      /** The component factory instance. */
//      public static final String	COMPONENT_FACTORY	= IStarterConfiguration.COMPONENT_FACTORY;

      public static final String	PROGRAM_ARGUMENTS	= "programarguments";						// class:
      // String[]
      // default:
      // emptyvalue

      /** ======== Arguments used by platform components. ======== **/

      /** Start the JCC agent to open the platform GUI? **/
      public static final String	GUI					= "gui";									// class:
      // boolean
      // default:
      // true

      /**
       * Start the platform with command line interface (cli) activated? (requires
       * Jadex Pro add-on)
       **/
      public static final String	CLI					= "cli";									// class:
      // boolean
      // default:
      // true

      /** Start cli with console in or not **/
      public static final String	CLICONSOLE			= "cliconsole";								// class:
      // boolean
      // default:
      // true

      /** Save platform settings on shutdown? **/
      public static final String	SAVEONEXIT			= "saveonexit";								// class:
      // boolean
      // default:
      // true

      /** Open JCC for specific remote platforms. **/
      public static final String	JCCPLATFORMS		= "jccplatforms";							// class:
      // String
      // default:
      // null

      /** Enable verbose logging (shortcut for setting logging_level to info). **/
      public static final String	LOGGING				= "logging";								// class:
      // boolean
      // default:
      // false

      /** Logging level for platform. **/
      public static final String	LOGGING_LEVEL		= "logging_level";							// class:
      // java.util.logging.Level
      // default:
      // java.util.logging.Level.SEVERE

      /** Use simulation execution mode? **/
      public static final String	SIMULATION			= "simulation";								// class:
      // Boolean
      // default:
      // emptyvalue

      /** Use asynchronous execution mode? **/
      public static final String	ASYNCEXECUTION		= "asyncexecution";							// class:
      // Boolean
      // default:
      // emptyvalue

//      /** Flag to enable component persistence. **/
//      public static final String	PERSIST				= "persist";								// class:
//      // boolean
//      // default:
//      // false

      /** Flag if CIDs may be reused (true for not). **/
      public static final String	UNIQUEIDS			= "uniqueids";								// class:
      // boolean
      // default:
      // true

      /** Flag for deferring thread creation/deletion in thread pool **/
      public static final String	THREADPOOLDEFER		= "threadpooldefer";						// class:
      // boolean
      // default:
      // true

      /**
       * Additional library paths (classpath entries) for loading agent models and
       * classes.
       **/
      public static final String	LIBPATH				= "libpath";								// class:
      // String
      // default:

      /** The base classloader. **/
      public static final String	BASECLASSLOADER		= "baseclassloader";						// class:
      // ClassLoader
      // default:

      /**
       * Start the chat agent for instant messaging and file transfer with user
       * Jadex users.
       **/
      public static final String	CHAT				= "chat";									// class:
      // boolean
      // default:
      // true

      /** Start the awareness agent (awa) for automatic platform discovery. **/
      public static final String	AWARENESS			= "awareness";								// class:
      // boolean
      // default:
      // true

      /** Specify the awareness agent discovery mechanisms (comma separated). **/
      public static final String	AWAMECHANISMS		= "awamechanisms";							// class:
      // String
      // default:
      // "Broadcast,
      // Multicast,
      // Message,
      // Relay,
      // Local"

      /** The awareness delay in milliseconds **/
      public static final String	AWADELAY			= "awadelay";								// class:
      // long
      // default:
      // 20000

      /** Include entries for awareness agent. **/
      public static final String	AWAINCLUDES			= "awaincludes";							// class:
      // String
      // default:
      // ""

      /** Exclude entries for awareness agent. **/
      public static final String	AWAEXCLUDES			= "awaexcludes";							// class:
      // String
      // default:
      // ""

      /** Use a compact binary message format instead of XML by default. **/
      public static final String	BINARYMESSAGES		= "binarymessages";							// class:
      // boolean
      // default:
      // true

      /** Fail on recoverable message decoding errors instead of ignoring. **/
      public static final String	STRICTCOM			= "strictcom";								// class:
      // boolean
      // default:
      // false

//      /**
//       * Flag if the platform should be protected with password. If a value is
//       * provided this value overrides the settings value.
//       **/
//      public static final String	USEPASS				= "usepass";								// class:
//      // Boolean
//      // default:
//      // emptyvalue
//
//      /** Flag if the platform password should be printed to the console. **/
//      public static final String	PRINTPASS			= "printpass";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if trusted lan should be used. **/
//      public static final String	TRUSTEDLAN			= "trustedlan";								// class:
//      // Boolean
//      // default:
//      // emptyvalue
//
//      /** Network name. **/
//      public static final String	NETWORKNAME			= "networkname";							// class:
//      // String
//      // default:
//      // emptyvalue
//
//      /** Network pass. **/
//      public static final String	NETWORKPASS			= "networkpass";							// class:
//      // String
//      // default:
//      // emptyvalue
//
//      /** Virtual names that are used for authentication **/
//      public static final String	VIRTUALNAMES		= "virtualnames";							// class:
//      // java.util.Map
//      // default:
//      // emptyvalue
//
//      /** The message validity duration (in minutes) **/
//      public static final String	VALIDITYDURATION	= "validityduration";						// class:
//      // long
//      // default:

      /** Flag if local transport is enabled. **/
      public static final String	LOCALTRANSPORT		= "localtransport";							// class:
      // boolean
      // default:
      // true

      /** Flag if tcp transport is enabled. **/
      public static final String	TCPTRANSPORT		= "tcptransport";							// class:
      // boolean
      // default:
      // true

      /** Port for TCP transport. **/
      public static final String	TCPPORT				= "tcpport";								// class:
      // int
      // default:
      // 8765

      /** Flag if relay transport is enabled. **/
      public static final String	RELAYTRANSPORT		= "relaytransport";							// class:
      // boolean
      // default:
      // true

      /**
       * Address(es) for relay transport (one or more addresses separated by
       * commas).
       **/
      public static final String	RELAYADDRESSES		= "relayaddresses";							// class:
      // String
      // default:
      // jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS

//      /** Flag if relay should use HTTPS for receiving messages. **/
//      public static final String	RELAYSECURITY		= "relaysecurity";							// class:
//      // boolean
//      // default:
//      // $args.relayaddress.indexOf("https://")==-1
//      // ?
//      // false
//      // :
//      // true

//      /** Flag if only awareness messages should be sent through relay. **/
//      public static final String	RELAYAWAONLY		= "relayawaonly";							// class:
//      // boolean
//      // default:
//      // false

//      /** Flag if ssltcp transport should enabled (requires Jadex Pro add-on). **/
//      public static final String	SSLTCPTRANSPORT		= "ssltcptransport";						// class:
//      // boolean
//      // default:
//      // false
  //
//      /** Port for SSL TCP transport. **/
//      public static final String	SSLTCPPORT			= "ssltcpport";								// class:
//      // int
//      // default:
//      // 44334

      /** Flag if web service publishing is enabled. **/
      public static final String	WSPUBLISH			= "wspublish";								// class:
      // boolean
      // default:
      // false

      /** Flag if rest service publishing is enabled. **/
      public static final String	RSPUBLISH			= "rspublish";								// class:
      // boolean
      // default:
      // false

      /** Optionally provide alternative rs publish implementation. **/
      public static final String	RSPUBLISHCOMPONENT	= "rspublishcomponent";						// class:
      // String
      // default:
      // First
      // component
      // available
      // as
      // defined
      // in
      // IPublishService...

      /**
       * The name(s) of kernel(s) to load (separated by comma). Currently supports
       * 'component', 'micro', 'bpmn', 'bdi', 'gpmn' and 'application' kernel.
       * Alternatively, the 'multi' can be used to start any available kernels on
       * demand. Specifying 'all' will start all available kernels directly.
       **/
      public static final String	KERNELS				= "kernels";								// class:
      // String
      // default:
      // "multi"

      /** <argument name="platform_shutdown_time">1000</argument> **/

      /**
       * Flag to enable the Maven dependency service (requires Jadex Pro add-on).
       **/
      public static final String	MAVEN_DEPENDENCIES	= "maven_dependencies";						// class:
      // boolean
      // default:
      // false
      
      /** Optionally provide alternative thread pool implementation. **/
      public static final String	THREADPOOLCLASS		= "threadpoolclass";						// class:
      // String
      // default:
      // null

      /** Optionally provide alternative context service implementation. **/
      public static final String	CONTEXTSERVICECLASS	= "contextserviceclass";					// class:
      // String
      // default:
      // null

      
      
//      /** Flag if global monitoring is turned on. **/
//      public static final String	MONITORINGCOMP		= "mon";									// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if sensors are turned on. **/
//      public static final String	SENSORS				= "sensors";								// class:
//      // boolean
//      // default:
//      // false
//
//      /** Flag if df component and service should be started. **/
//      public static final String	DF					= "df";										// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if clock component and service should be started. **/
//      public static final String	CLOCK				= "clock";									// class:
//      // boolean
//      // default:
//      // true
//
////      /** Flag if message component and service should be started. **/
////      public static final String	MESSAGE				= "message";								// class:
////      // boolean
////      // default:
////      // true
//
//      /** Flag if simulation component and service should be started. **/
//      public static final String	SIMUL				= "simul";									// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if filetransfer component and service should be started. **/
//      public static final String	FILETRANSFER		= "filetransfer";							// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if marshal component and service should be started. **/
//      public static final String	MARSHAL				= "marshal";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if security component and service should be started. **/
//      public static final String	SECURITY			= "security";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if library component and service should be started. **/
//      public static final String	LIBRARY				= "library";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if settings component and service should be started. **/
//      public static final String	SETTINGS			= "settings";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if context component and service should be started. **/
//      public static final String	CONTEXT				= "context";								// class:
//      // boolean
//      // default:
//      // true
//
//      /** Flag if address component and service should be started. **/
//      public static final String	ADDRESS				= "address";								// class:
//      // boolean
//      // default:
//      // true

      public static final String WSPORT = "wsport";

      public static final String WSTRANSPORT = "wstransport";

      public static final String RELAYFORWARDING = "relayforwarding";

      public static final String PLATFORMARGS = "$platformargs";
      
      public static final String PLATFORMCONFIG = "$platformconfig";
      
      public static final String PLATFORMMODEL = "$platformmodel";
      
      /**
       * This is used for consistency checks and includes all argument names which refer to boolean
       * arguments.
       */
      public static final String[] BOOLEAN_ARGS = {
              WELCOME, GUI, CLI, CLICONSOLE, SAVEONEXIT, LOGGING, SIMULATION, ASYNCEXECUTION,
//              PERSIST,
              UNIQUEIDS, THREADPOOLDEFER, CHAT, AWARENESS, BINARYMESSAGES, STRICTCOM,
//              USEPASS, PRINTPASS, TRUSTEDLAN,
              LOCALTRANSPORT, TCPTRANSPORT,
              RELAYTRANSPORT,
//              RELAYSECURITY, RELAYAWAONLY,
//              SSLTCPTRANSPORT,
              WSPUBLISH, RSPUBLISH, MAVEN_DEPENDENCIES,
//              MONITORINGCOMP, SENSORS, DF, CLOCK,
//              MESSAGE,
//              SIMUL, FILETRANSFER, MARSHAL, SECURITY,
//              LIBRARY, SETTINGS, CONTEXT, ADDRESS, 
//              SUPERPEER, SUPERPEERCLIENT
      };
    
      /**
       *  Get the extended platform configuration.
       *  @return The extended platform configuration.
       */
      public IExtendedPlatformConfiguration getExtendedPlatformConfiguration();
      
      /**
       *  Get all values of the configuration as map.
       *  @return The values.
       */
      public Map<String, Object> getValues();
   
      /**
       *   Enhance this config with given other config. Will overwrite all values
       *  that are set in the other config.
       */
      public void enhanceWith(IPlatformConfiguration other);
	
	/**
	 *  Clone this configuration.
	 */
	public IPlatformConfiguration clone();
	
	/**
	 *  Set the readonly state.
	 *  @param readonly The readonly state.
	 */
	public void setReadOnly(boolean readonly);
	
	/**
	 *  Get the readonly state.
	 *  @return The readonly state.
	 */
	public boolean isReadOnly();
	
	/**
	 *  Get a value per key.
	 *  @param key The key.
	 *  @return The value.
	 */
	public Object getValue(String key, IModelInfo model);
	
	/**
	 *  Set a value per key.
	 *  @param key The key.
	 *  @return The value.
	 */
	public void setValue(String key, Object value);
	
	/**
	 *  Get the platform name.
	 *  @return The platform name.
	 */
	public String getPlatformName();
	
	/**
	 *  Set the platform name.
	 *  @param value The name.
	 */
	public void setPlatformName(String value);
	
	/**
	 *  Get the configuration name.
	 *  @return The configuration name.
	 */
	public String getConfigurationName();
	
	/**
	 *  Set the configuration name.
	 *  @param value The configuration name.
	 */
	public void setConfigurationName(String value);
	
	/**
	 *  Add a component via class.
	 *  @param clazz The classinfo of the component.
	 */
	public void addComponent(Class<?> clazz);
	
	/**
	 *  Add a component via file.
	 *  @param path The file path.
	 */
	public void addComponent(String path);
	
	/**
	 *  Set multiple components as list.
	 *  @param newcomps The list of components.
	 */
	public void setComponents(List<String> newcomps);
	
	/**
	 *  Get the components as list.
	 *  @return The components.
	 */
	public List<String> getComponents();
	
	/**
	 *  Shall print exceptions.
	 *  @return Flag is exceptions should be printed.
	 */
	public boolean isPrintExceptions();
	
	/**
	 *  Set print exceptions flag.
	 *  @return Flag is exceptions should be printed.
	 */
	public void setPrintExceptions(boolean printex);
	
	/**
	 *  Set the default timeout.
	 *  @param to The timeout.
	 */
	public void setDefaultTimeout(long to);
	
	/**
	 *  Get the default timeout.
	 *  @return The default timeout.
	 */
	public Long getDefaultTimeout();
	
    /**
     *  Get the welcome flag.
     *  @return True means print welcome message.
     */
    public boolean getWelcome();

    /**
     *  Tell starter to print welcome message.
     *  @param value
     */
    public void setWelcome(boolean value);
    
    /**
     *  Get the flag if gui is opened.
     *  @return True means start with gui.
     */
    public boolean getGui();

    /**
     *  Set the gui flag.
     *  @param value True for starting with gui.
     */
    public void setGui(boolean value);
    
    /**
     *  Get the logging flag.
     *  @return The logging flag.
     */
    public boolean getLogging();

    /**
     *  Set the logging flag.
     *  @param value The logging flag.
     */
    public void setLogging(boolean value);
    
    /**
     *  Get the logging level.
     *  @return The logging level.
     */
    public Level getLoggingLevel();

    /**
     *  Set the logging level.
     *  @param value The logging level.
     */
    public void setLoggingLevel(Level value);
    
    /**
     *  Get the flag for starting with awareness.
     *  @return True for starting with awareness.
     */
    public boolean getAwareness();

    /**
     *  Set the flag for starting with awareness.
     *  @param value True for starting with awareness.
     */
    public void setAwareness(boolean value);

    /**
     *  Get the sensors flag.
     *  @return The sensors flag.
     */
    public boolean getSensors();

    /**
     *  Set the sensors flag.
     *  @param value The sensors flag.
     */
    public void setSensors(boolean value);
    
    /**
     *  Get the superpeer flag.
     *  @return The superpeer flag.
     */
    public boolean getSuperpeer();

    /**
     *  Set the superpeer flag.
     *  @param value The superpeer flag.
     */
    public void setSuperpeer(boolean value);

    /**
     *  Get the superpeer flag.
     *  @return The superpeer flag.
     */
    public boolean getSupersuperpeer();

    /**
     *  Set the supersuperpeer flag.
     *  @param value The supersuperpeer flag.
     */
    public void setSupersuperpeer(boolean value);
    
    /**
     *  Get the superpeer client flag.
     *  @return The superpeer client flag.
     */
    public boolean getSuperpeerClient();

    /**
     *  Set the superpeer client flag.
     *  @param value The superpeer client flag.
     */
    public void setSuperpeerClient(boolean value);
    
//  /**
//  *  Get the kernel names.
//  *  @return The kernel names.
//  */
//// public IRootComponentConfiguration.KERNEL[] getKernels();
    public String[] getKernels();
//
// /**
//  *  Set the kernel names.
//  *  @param value The kernel names.
//  */
    public void setKernels(String... value);
//
//// public void setKernels(IRootComponentConfiguration.KERNEL... value);
	
	 /**
     *  Get the network name (used at startup).
     *  @return The network name.
     */
    public String[] getNetworkNames();

    /**
     *  Set the network name (used at startup).
     *  @param value The network name.
     */
    public void setNetworkNames(String... value);
    
//    /**
//     *  Add the network name (used at startup).
//     *  @param value The network name.
//     */
//    public void addNetworkName(String value);
    
//    /**
//     *  Set the network names (used at startup).
//     *  @param value The network name.
//     */
//    public void setNetworkNames(String[] values);

    // ---- Security Settings ----
    
    /**
     *  Returns if the security service should use a platform secret for authentication.
     *  If false only networks are considered for authentication.
     *  
     *  @return True if the security service should use a platform secret.
     */
    public boolean isUseSecret();
    
    /**
     *  Sets if the security service should use a platform secret for authentication.
     *  If false only networks are considered for authentication.
     *  
     *  @param usesecret Set true (default) if the security service should use a platform secret.
     */
    public void setUseSecret(boolean usesecret);
    
    /**
     *  Returns if the security service should print the platform secret during start.
     *  
     *  @return True if the security service should print the platform secret during start.
     */
    public boolean isPrintSecret();
    
    /**
     *  Returns if the security service should print the platform secret during start.
     *  
     *  @param Set true (default) if the security service should print the platform secret during start.
     */
    public void setPrintSecret(boolean printsecret);
    
    /**
     *  Returns if the security service allows unauthenticated connections.
     *  
     *  @return True, if unauthenticated connections are refused.
     */
    public boolean isRefuseUnauth();
    
    /**
     *  Configure the security service to whether to allow unauthenticated connections.
     *  This must be set to false if Security.UNRESTRICTED services are used or offered.
     *  Default is false.
     *  
     *  @param refuseunauth Flag if unauthenticated platforms are refused.
     */
    public void setRefuseUnauth(boolean refuseunauth);
    
    /**
     *  Get the network secret (used at startup).
     *  @return The network secret.
     */
    public String[] getNetworkSecrets();

    /**
     *  Set the network secret (used at startup).
     *  @param value The network secret.
     */
    public void setNetworkSecrets(String... value);
}
