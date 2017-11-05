package jadex.base;

import java.util.Map;
import java.util.logging.Level;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;

/**
 *  Interface for configuring the root component, i.e. platform.
 */
public interface IRootComponentConfiguration 
{
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
	public static final String AWAMECHANISM_MESSAGE = "message";
	/** The Relay discovery is the most robust discovery variant. It uses an external
	 *  web server as relay where each platform registers. It is possible to set-up a
	 *  self-hosted relay server, but per default, https://activecomponents.org/relay is used. */
	public static final String AWAMECHANISM_RELAY = "relay";
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

    /** The IPlatformComponentAccess instance **/
    public static final String	PLATFORM_ACCESS		= "platformaccess";

    /** The component factory instance. */
    public static final String	COMPONENT_FACTORY	= IStarterConfiguration.COMPONENT_FACTORY;

    /** The saved program arguments. **/
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

//    /** Flag to enable component persistence. **/
//    public static final String	PERSIST				= "persist";								// class:
//    // boolean
//    // default:
//    // false

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

    /** The awareness fast flag **/
    public static final String	AWAFAST				= "awafast";								// class:
    // boolean
    // default:
    // false

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

    /**
     * Flag if the platform should be protected with password. If a value is
     * provided this value overrides the settings value.
     **/
    public static final String	USEPASS				= "usepass";								// class:
    // Boolean
    // default:
    // emptyvalue

    /** Flag if the platform password should be printed to the console. **/
    public static final String	PRINTPASS			= "printpass";								// class:
    // boolean
    // default:
    // true

    /** Flag if trusted lan should be used. **/
    public static final String	TRUSTEDLAN			= "trustedlan";								// class:
    // Boolean
    // default:
    // emptyvalue

    /** Network name. **/
    public static final String	NETWORKNAME			= "networkname";							// class:
    // String
    // default:
    // emptyvalue

    /** Network pass. **/
    public static final String	NETWORKPASS			= "networkpass";							// class:
    // String
    // default:
    // emptyvalue

    /** Virtual names that are used for authentication **/
    public static final String	VIRTUALNAMES		= "virtualnames";							// class:
    // java.util.Map
    // default:
    // emptyvalue

    /** The message validity duration (in minutes) **/
    public static final String	VALIDITYDURATION	= "validityduration";						// class:
    // long
    // default:

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
    public static final String	RELAYADDRESS		= "relayaddress";							// class:
    // String
    // default:
    // jadex.platform.service.message.transport.httprelaymtp.SRelay.DEFAULT_ADDRESS

//    /** Flag if relay should use HTTPS for receiving messages. **/
//    public static final String	RELAYSECURITY		= "relaysecurity";							// class:
//    // boolean
//    // default:
//    // $args.relayaddress.indexOf("https://")==-1
//    // ?
//    // false
//    // :
//    // true

//    /** Flag if only awareness messages should be sent through relay. **/
//    public static final String	RELAYAWAONLY		= "relayawaonly";							// class:
//    // boolean
//    // default:
//    // false

//    /** Flag if ssltcp transport should enabled (requires Jadex Pro add-on). **/
//    public static final String	SSLTCPTRANSPORT		= "ssltcptransport";						// class:
//    // boolean
//    // default:
//    // false
//
//    /** Port for SSL TCP transport. **/
//    public static final String	SSLTCPPORT			= "ssltcpport";								// class:
//    // int
//    // default:
//    // 44334

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

    /** Flag if global monitoring is turned on. **/
    public static final String	MONITORINGCOMP		= "monitoringcomp";							// class:
    // boolean
    // default:
    // true

    /** Flag if sensors are turned on. **/
    public static final String	SENSORS				= "sensors";								// class:
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

    /** Flag if df component and service should be started. **/
    public static final String	DF					= "df";										// class:
    // boolean
    // default:
    // true

    /** Flag if clock component and service should be started. **/
    public static final String	CLOCK				= "clock";									// class:
    // boolean
    // default:
    // true

//    /** Flag if message component and service should be started. **/
//    public static final String	MESSAGE				= "message";								// class:
//    // boolean
//    // default:
//    // true

    /** Flag if simulation component and service should be started. **/
    public static final String	SIMUL				= "simul";									// class:
    // boolean
    // default:
    // true

    /** Flag if filetransfer component and service should be started. **/
    public static final String	FILETRANSFER		= "filetransfer";							// class:
    // boolean
    // default:
    // true

    /** Flag if marshal component and service should be started. **/
    public static final String	MARSHAL				= "marshal";								// class:
    // boolean
    // default:
    // true

    /** Flag if security component and service should be started. **/
    public static final String	SECURITY			= "security";								// class:
    // boolean
    // default:
    // true

    /** Flag if library component and service should be started. **/
    public static final String	LIBRARY				= "library";								// class:
    // boolean
    // default:
    // true

    /** Flag if settings component and service should be started. **/
    public static final String	SETTINGS			= "settings";								// class:
    // boolean
    // default:
    // true

    /** Flag if context component and service should be started. **/
    public static final String	CONTEXT				= "context";								// class:
    // boolean
    // default:
    // true

    /** Flag if address component and service should be started. **/
    public static final String	ADDRESS				= "address";								// class:
    // boolean
    // default:
    // true

    /** Flag if platform should support registry synchronization. **/
    public static final String	SUPERPEER			= IStarterConfiguration.SUPERPEER;		// class:
    // default:
    // false

    /** Flag if registry synchronization should be used. **/
    public static final String	SUPERPEERCLIENT			= IStarterConfiguration.SUPERPEERCLIENT;		// class:
    // default:
    // false

    public static final String WSPORT = "wsport";
    
    public static final String WSTRANSPORT = "wstransport";
    
    public static final String RELAYFORWARDING = "relayforwarding";
    
    /**
     * This is used for consistency checks and includes all argument names which refer to boolean
     * arguments.
     */
    public static final String[] BOOLEAN_ARGS = {
            WELCOME, GUI, CLI, CLICONSOLE, SAVEONEXIT, LOGGING, SIMULATION, ASYNCEXECUTION,
//            PERSIST,
            UNIQUEIDS, THREADPOOLDEFER, CHAT, AWARENESS, BINARYMESSAGES, STRICTCOM, USEPASS,
            PRINTPASS, TRUSTEDLAN, LOCALTRANSPORT, TCPTRANSPORT,
            RELAYTRANSPORT,
//            RELAYSECURITY, RELAYAWAONLY,
//            SSLTCPTRANSPORT,
            WSPUBLISH, RSPUBLISH, MAVEN_DEPENDENCIES,
            MONITORINGCOMP, SENSORS, DF, CLOCK,
//            MESSAGE,
            SIMUL, FILETRANSFER, MARSHAL, SECURITY,
            LIBRARY, SETTINGS, CONTEXT, ADDRESS, SUPERPEER, SUPERPEERCLIENT
    };


    public Map<String, Object> getArgs();

    /**
     *  Set program arguments to be available at runtime.
     *  @param args The arguments.
     */
    public void setProgramArguments(String[] args);

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
     *  Set the platform access.
     *  @param value The platform access.
     */
    void setPlatformAccess(IPlatformComponentAccess value);

    /**
     *  Get the component factory.
     *  @return value The component factory.
     */
    IComponentFactory getBootstrapFactory(IComponentFactory value);

    /**
     *  Set the bootstrap component factory.
     *  @param value The component factory.
     */
    void setBootstrapFactory(IComponentFactory value);

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
     *  Get the flag if command line interface is opened.
     *  @return True means start with cli.
     */
    public boolean getCli();

    /**
     *  Set the command line interface flag.
     *  @param value True for starting with gui.
     */
    void setCli(boolean value);

    /**
     *  Should the cli console (in jcc) 
     *  @return Flag if cli console should be active.
     */
    public boolean getCliConsole();

    /**
     *  Set the cli console flag (in jcc).
     *  @param value Flag if cli console should be active.
     */
    public void setCliConsole(boolean value);

    /**
     *  Get flag for save settings on exit.
     *  @return True, if settings are saved on exit.
     */
    public boolean getSaveOnExit();

    /**
     *  Set flag for save settings on exit.
     *  @param True, if settings are saved on exit.
     */
    public void setSaveOnExit(boolean value);

    /**
     *  Get flag for open jcc for specific remote platforms.
     *  @return The jcc platform names.
     */
    public String getJccPlatforms();

    /**
     *  Set flag for open jcc for specific remote platforms.
     *  @param value The jcc platform names.
     */
    public void setJccPlatforms(String value);

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
     *  Get the flag for simulation execution.
     *  @return True for simulation mode.
     */
    public boolean getSimulation();

    /**
     *  Set the flag for simulation execution.
     *  @param value True for simulation mode.
     */
    public void setSimulation(boolean value);

    /**
     *  Get the async execution mode flag.
     *  @return The async execution mode flag.
     */
    public boolean getAsyncExecution();

    /**
     *  Set the async execution mode flag.
     *  @param value The async execution mode flag.
     */
    public void setAsyncExecution(boolean value);

//    public boolean getPersist();

//    public void setPersist(boolean value);

    /**
     *  Get the unique id flag, i.e. do not reuse ids formerly used by dead components.
     *  @return True for unique ids.
     */
    public boolean getUniqueIds();

    /**
     *  Set the unique id flag, i.e. do not reuse ids formerly used by dead components.
     *  @param value True for unique ids.
     */
    public void setUniqueIds(boolean value);

    /**
     *  Get the flag for deferred thread creation/deletion in threadpool.
     *  @return The defer flag.
     */
    public boolean getThreadpoolDefer();

    /**
     *  Set the flag for deferred thread creation/deletion in threadpool.
     *  @param value The defer flag.
     */
    public void setThreadpoolDefer(boolean value);

    /**
     *  Get the library path.
     *  @return The library path.
     */
    public String getLibPath();

    /**
     *  Set the library path.
     *  @param value The library path.
     */
    public void setLibPath(String value);

    /**
     *  Get the base classloader.
     *  @return The base classloader.
     */
    public ClassLoader getBaseClassloader();

    /**
     *  Set the base classloader.
     *  @param value The base classloader.
     */
    public void setBaseClassloader(ClassLoader value);

    /**
     *  Get the flag for starting with chat.
     *  @return True for starting with chat.
     */
    public boolean getChat();

    /**
     *  Set the flag for starting with chat.
     *  @param value True for starting with chat.
     */
    public void setChat(boolean value);

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
     *  Get the awareness mechanisms.
     *  @return The awareness mechanisms.
     */
//    public IRootComponentConfiguration.AWAMECHANISM[] getAwaMechanisms();
    public String[] getAwaMechanisms();

    /**
     *  Set the awareness mechanisms.
     *  @param values The awareness mechanisms.
     */
//    public void setAwaMechanisms(IRootComponentConfiguration.AWAMECHANISM... values);
    public void setAwaMechanisms(String... values);

    /**
     *  Get the delay between awareness notifications.
     *  @return The delay in millis.
     */
    public long getAwaDelay();

    /**
     *  Set the delay between awareness notifications.
     *  @param value The delay in millis.
     */
    public void setAwaDelay(long value);

    /**
     *  Mode in which awareness is blocking startup.
     *  @return flag for fast awa.
     */
    public boolean isAwaFast();

    /**
     *  Mode in which awareness is blocking startup.
     *  @return value Flag for fast awa.
     */
    public void setAwaFast(boolean value);

    /**
     *  Get the awareness platform includes.
     *  @return The awareness platform includes.
     */
    public String getAwaIncludes();

    /**
     *  Set the awareness platform includes.
     *  @param value The awareness platform includes. 
     */
    public void setAwaIncludes(String value);

    /**
     *  Get the awareness platform excludes.
     *  @return The awareness platform excludes.
     */
    public String getAwaExcludes();

    /**
     *  Set the awareness platform excludes.
     *  @param value The awareness platform excludes. 
     */
    public void setAwaExcludes(String value);

    /**
     *  Get the flag for binary messages.
     *  @return The flag for binary messages.
     */
    public boolean getBinaryMessages();

    /**
     *  Set the flag for binary messages.
     *  @param value The flag for binary messages.
     */
    public void setBinaryMessages(boolean value);

    /**
     *  Get flag for strict communication.
     *  Fail on recoverable message decoding errors instead of ignoring
     *  @return Strict communication flag.
     */
    public boolean getStrictCom();

    /**
     *  Get flag for strict communication.
     *  Fail on recoverable message decoding errors instead of ignoring
     *  @return Strict communication flag.
     */
    public void setStrictCom(boolean value);

    /**
     *  Flag if the platform should be protected with password. I
     *  @return True if protected.
     */
    public boolean getUsePass();

    /**
     *  Flag if the platform should be protected with password. I
     *  @param value True for protected.
     */
    public void setUsePass(boolean value);

    /**
     *  Get the print password flag on startup.
     *  @return Flag if password should be printed.
     */
    public boolean getPrintPass();

    /**
     *  Set the print password flag on startup.
     *  @param value Flag if password should be printed.
     */
    public void setPrintPass(boolean value);

    /**
     *  Flag if trusted lan is activated. (Trusts internal ips)
     *  @return True if trusted lan is active.
     */
    public boolean getTrustedLan();

    /**
     *  Flag if trusted lan is activated. (Trusts internal ips)
     *  @param value True if trusted lan is active.
     */
    public void setTrustedLan(boolean value);

    /**
     *  Get the network name (used at startup).
     *  @return The network name.
     */
    public String getNetworkName();

    /**
     *  Set the network name (used at startup).
     *  @param value The network name.
     */
    public void setNetworkName(String value);

    /**
     *  Get the network pass (used at startup).
     *  @return The network pass.
     */
    public String getNetworkPass();

    /**
     *  Set the network pass (used at startup).
     *  @param value The network pass.
     */
    public void setNetworkPass(String value);

    /**
     *  Get the virtual names (roles).
     *  @return The virtual names.
     */
    public Map<String, String> getVirtualNames();

    /**
     *  Set the virtual names (roles).
     *  @param value The virtual names.
     */
    public void setVirtualNames(Map<String, String> value);

    /**
     *  Get the validity duration of messages.
     *  @return The validity duration.
     */
    public long getValidityDuration();

    /**
     *  Set the validity duration of messages.
     *  @param value The validity duration.
     */
    public void setValidityDuration(long value);

    /**
     *  Get the flag if the local transport is active.
     *  @return Flag if the local transport is active.
     */
    public boolean getLocalTransport();

    /**
     *  Set the flag if the local transport is active.
     *  @param value Flag if the local transport is active.
     */
    public void setLocalTransport(boolean value);

    /**
     *  Get the flag if the tcp transport is active.
     *  @return Flag if the tcp transport is active.
     */
    public boolean getTcpTransport();

    /**
     *  Set the flag if the tcp transport is active.
     *  @param value Flag if the tcp transport is active.
     */
    public void setTcpTransport(boolean value);

    /**
     *  Get the tcp port of the tcp transport.
     *  @return The tcp port.
     */
    public int getTcpPort();

    /**
     *  Set the tcp port of the tcp transport.
     *  @param value The tcp port.
     */
    public void setTcpPort(int value);

    /**
     *  Get the flag if the relay transport is active.
     *  @return Flag if the relay transport is active.
     */
    public boolean getRelayTransport();

    /**
     *  Set the flag if the relay transport is active.
     *  @param value Flag if the relay transport is active.
     */
    public void setRelayTransport(boolean value);

    /**
     *  Get the relay address.
     *  @return The relay address.
     */
    public String getRelayAddress();

    /**
     *  Set the relay address.
     *  @param value The relay address.
     */
    public void setRelayAddress(String value);
    
    /**
     *  Get the flag if the ws transport is active.
     *  @return Flag if the ws transport is active.
     */
    public boolean getWsTransport();

    /**
     *  Set the flag if the ws transport is active.
     *  @param value Flag if the ws transport is active.
     */
    public void setWsTransport(boolean value);
    
    /**
     *  Get the websocket port of the websocket transport.
     *  @return The websocket port.
     */
    public int getWsPort();

    /**
     *  Set the websocket port of the websocket transport.
     *  @param value The websocket port.
     */
    public void setWsPort(int value);

    /**
     *  Should the platform act as relay, i.e. forward messages from one platform to another platform?
     */
    boolean getRelayForwarding();

    /**
     *  Should the platform act as relay, i.e. forward messages from one platform to another platform?
     */
    void setRelayForwarding(boolean value);
//
//    boolean getRelayAwaonly();
//
//    void setRelayAwaonly(boolean value);
//
//    boolean getSslTcpTransport();
//
//    void setSslTcpTransport(boolean value);
//
//    int getSslTcpPort();
//
//    void setSslTcpPort(int value);

    /**
     *  Get the flag if wsdl publishing is on.
     *  @return True if wsdl publishing is on.
     */
    public boolean getWsPublish();

    /**
     *  Set the flag if wsdl publishing is on.
     *  @param value True if wsdl publishing is on.
     */
    public void setWsPublish(boolean value);

    /**
     *  Get the flag if rest publishing is on.
     *  @return True if rest publishing is on.
     */
    public boolean getRsPublish();

    /**
     *  Set the flag if rest publishing is on.
     *  @param value True if rest publishing is on.
     */
    public void setRsPublish(boolean value);

    /**
     *  Get the rest publish component.
     *  @return The rest publish component.
     */
    public String getRsPublishComponent();

    /**
     *  Set the rest publishing component.
     *  @param value The rest publishing component.
     */
    public void setRsPublishComponent(String value);

    /**
     *  Get the kernel names.
     *  @return The kernel names.
     */
//    public IRootComponentConfiguration.KERNEL[] getKernels();
    public String[] getKernels();

    /**
     *  Set the kernel names.
     *  @param value The kernel names.
     */
    public void setKernels(String... value);

//    public void setKernels(IRootComponentConfiguration.KERNEL... value);

    /**
     *  Get the flag if maven dependencies are on.
     *  @return The maven dependencies.
     */
    public boolean getMavenDependencies();

    /**
     *  Set the maven dependencies flag.
     *  @param value The maven dependencies.
     */
    public void setMavenDependencies(boolean value);

    /**
     *  Get the monitoring component flag.
     *  @return The monitoring component flag.
     */
    public boolean getMonitoringComp();

    /**
     *  Set the monitoring component flag.
     *  @param value The monitoring component flag.
     */
    public void setMonitoringComp(boolean value);

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
     *  Get the threadpool class.
     *  @return The threadpool class name.
     */
    public String getThreadpoolClass();

    /**
     *  Set the threadpool class name.
     *  @param value The threadpool class name.
     */
    public void setThreadpoolClass(String value);

    /**
     *  Get the context service class.
     *  @return The context service class.
     */
    public String getContextServiceClass();

    /**
     *  Get the context service class.
     *  @param value The context service class.
     */
    public void setContextServiceClass(String value);

    /**
     *  Get the df (directory facilitator) flag.
     *  @return The df flag.
     */
    public boolean getDf();

    /**
     *  Get the df (directory facilitator) flag.
     *  @param value The df flag.
     */
    public void setDf(boolean value);

    /**
     *  Get the clock flag.
     *  @return The clock flag.
     */
    public boolean getClock();

    /**
     *  Set the clock flag.
     *  @param value The clock flag.
     */
    public void setClock(boolean value);

//    boolean getMessage();
//
//    void setMessage(boolean value);

    /**
     *  Get the simulation flag.
     *  @return The simulation flag.
     */
    public boolean getSimul();

    /**
     *  Set the simulation flag.
     *  @param value The simulation flag.
     */
    public void setSimul(boolean value);

    /**
     *  Get the file transfer flag.
     *  @return The file transfer flag.
     */
    public boolean getFiletransfer();

    /**
     *  Set the file transfer flag.
     *  @param value The file transfer flag.
     */
    public void setFiletransfer(boolean value);

    /**
     *  Get the marshal flag.
     *  @return The marshal flag.
     */
    public boolean getMarshal();

    /**
     *  Set the marshal flag.
     *  @param value The marshal flag.
     */
    public void setMarshal(boolean value);

    /**
     *  Get the security flag.
     *  @return The security flag.
     */
    public boolean getSecurity();

    /**
     *  Set the security flag.
     *  @param value The security flag.
     */
    public void setSecurity(boolean value);

    /**
     *  Get the library flag.
     *  @return The library flag.
     */
    public boolean getLibrary();

    /**
     *  Set the library flag.
     *  @param value The library flag.
     */
    public void setLibrary(boolean value);

    /**
     *  Get the settings flag.
     *  @return The settings flag.
     */
    public boolean getSettings();

    /**
     *  Set the settings flag.
     *  @param value The settings flag.
     */
    public void setSettings(boolean value);

    /**
     *  Get the context flag.
     *  @return The context flag.
     */
    public boolean getContext();

    /**
     *  Set the context flag.
     *  @param value The context flag.
     */
    public void setContext(boolean value);

    /**
     *  Get the address flag.
     *  @return The address flag.
     */
    public boolean getAddress();

    /**
     *  Set the address flag.
     *  @param value The address flag.
     */
    public void setAddress(boolean value);

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
     *  Get the superpeer client flag.
     *  @return The superpeer client flag.
     */
    boolean getSuperpeerClient();

    /**
     *  Set the superpeer client flag.
     *  @param value The superpeer client flag.
     */
    public void setSuperpeerClient(boolean value);
    
    /**
     *  Set a value.
     *  @param key The key.
     *  @param value The value
     */
    public void setValue(String key, Object value);
}
