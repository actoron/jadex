package jadex.base;

import java.util.Map;
import java.util.logging.Level;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;

public interface IRootComponentConfiguration {

    /**
     * Kernel names enum.
     */
    public enum KERNEL
    {
        component, micro, bpmn, v3, bdi, bdibpmn, multi
    }

    /**
     * Discovery names enum.
     */
    public enum AWAMECHANISM
    {
        /**
         * Uses IPv4 broadcast to announce awareness infos in local networks.
         * Default Port used is 55670.
         */
        broadcast,
        /**
         * Uses IPv4 Multicast to find other platforms.
         * Default multicast address used is 224.0.0.0, port 5567.
         */
        multicast,
        /**
         * Message discovery allows detecting other platforms upon message reception.
         * This helps especially if network connection is assymatric, e.g. one platform can
         * find the other (and send messages) but not vice versa.
         */
        message,
        /**
         * The Relay discovery is the most robust discovery variant. It uses an external
         * web server as relay where each platform registers. It is possible to set-up a self-hosted
         * relay server, but per default, https://activecomponents.org/relay is used.
         */
        relay,
        /**
         * The local discovery uses a file-based mechanism to detect platforms running on the same host.
         */
        local,
        /**
         * The Registry mechanism implements a master-slave mechanism, where one platform is
         * the registry. Other platforms that have this mechanism enabled register themselves
         * and the registry distributes awareness info to all registered platforms.
         * All RegistryDiscoveryAgents have to be parameterized with the same ip address
         * (of the registry).
         */
        registry,
        /**
         * The IP-Scanner discovery mechanism sends out awareness infos to all IP addresses within the
         * local network (using port 55668)
         */
        scanner,
        // bluetooth
    }

    /** Tell starter to print welcome message. **/
    public static final String	WELCOME				= "welcome";								// class:
    // boolean
    // default:
    // true

    // ----- arguments set by starter for root or platform component -----

    /** The IPlatformComponentAccess instance **/
    public static final String	PLATFORM_ACCESS		= "platformaccess";

    /** The component factory instance. */
    public static final String	COMPONENT_FACTORY	= StarterConfiguration.COMPONENT_FACTORY;

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

    /** Flag to enable component persistence. **/
    public static final String	PERSIST				= "persist";								// class:
    // boolean
    // default:
    // false

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
    public static final String	SUPERPEER			= StarterConfiguration.SUPERPEER;		// class:
    // default:
    // false

    /** Flag if registry synchronization should be used. **/
    public static final String	SUPERPEERCLIENT			= StarterConfiguration.SUPERPEERCLIENT;		// class:
    // default:
    // false

    /**
     * This is used for consistency checks and includes all argument names which refer to boolean
     * arguments.
     */
    public static final String[] BOOLEAN_ARGS = {
            WELCOME, GUI, CLI, CLICONSOLE, SAVEONEXIT, LOGGING, SIMULATION, ASYNCEXECUTION, PERSIST,
            UNIQUEIDS, THREADPOOLDEFER, CHAT, AWARENESS, BINARYMESSAGES, STRICTCOM, USEPASS,
            PRINTPASS, TRUSTEDLAN, LOCALTRANSPORT, TCPTRANSPORT,
//            RELAYTRANSPORT,
//            RELAYSECURITY, RELAYAWAONLY,
//            SSLTCPTRANSPORT,
            WSPUBLISH, RSPUBLISH, MAVEN_DEPENDENCIES,
            MONITORINGCOMP, SENSORS, DF, CLOCK,
//            MESSAGE,
            SIMUL, FILETRANSFER, MARSHAL, SECURITY,
            LIBRARY, SETTINGS, CONTEXT, ADDRESS, SUPERPEER, SUPERPEERCLIENT
    };


    Map<String, Object> getArgs();

    /**
     * Set program arguments to be available at runtime.
     *
     * @param args
     */
    void setProgramArguments(String[] args);

    // // internal
    boolean getWelcome();

    void setWelcome(boolean value);

    /**
     * Set the platform access.
     *
     * @param value
     */
    void setPlatformAccess(IPlatformComponentAccess value);

    /**
     * Set the component factory.
     *
     * @param value
     */
    void setComponentFactory(IComponentFactory value);

    boolean getGui();

    void setGui(boolean value);

    boolean getCli();

    void setCli(boolean value);

    boolean getCliConsole();

    void setCliConsole(boolean value);

    boolean getSaveOnExit();

    void setSaveOnExit(boolean value);

    String getJccPlatforms();

    void setJccPlatforms(String value);

    boolean getLogging();

    void setLogging(boolean value);

    Level getLoggingLevel();

    void setLoggingLevel(Level value);

    boolean getSimulation();

    void setSimulation(boolean value);

    boolean getAsyncExecution();

    void setAsyncExecution(boolean value);

    boolean getPersist();

    void setPersist(boolean value);

    boolean getUniqueIds();

    void setUniqueIds(boolean value);

    boolean getThreadpoolDefer();

    void setThreadpoolDefer(boolean value);

    String getLibPath();

    void setLibPath(String value);

    ClassLoader getBaseClassloader();

    void setBaseClassloader(ClassLoader value);

    boolean getChat();

    void setChat(boolean value);

    boolean getAwareness();

    void setAwareness(boolean value);

    RootComponentConfiguration.AWAMECHANISM[] getAwaMechanisms();

    void setAwaMechanisms(RootComponentConfiguration.AWAMECHANISM... values);

    long getAwaDelay();

    void setAwaDelay(long value);

    boolean isAwaFast();

    void setAwaFast(boolean value);

    String getAwaIncludes();

    void setAwaIncludes(String value);

    String getAwaExcludes();

    void setAwaExcludes(String value);

    boolean getBinaryMessages();

    void setBinaryMessages(boolean value);

    boolean getStrictCom();

    void setStrictCom(boolean value);

    boolean getUsePass();

    void setUsePass(boolean value);

    boolean getPrintPass();

    void setPrintPass(boolean value);

    boolean getTrustedLan();

    void setTrustedLan(boolean value);

    String getNetworkName();

    void setNetworkName(String value);

    String getNetworkPass();

    void setNetworkPass(String value);

    Map getVirtualNames();

    void setVirtualNames(Map value);

    long getValidityDuration();

    void setValidityDuration(long value);

    boolean getLocalTransport();

    void setLocalTransport(boolean value);

    boolean getTcpTransport();

    void setTcpTransport(boolean value);

    int getTcpPort();

    void setTcpPort(int value);

//    boolean getRelayTransport();
//
//    void setRelayTransport(boolean value);
//
//    String getRelayAddress();
//
//    void setRelayAddress(String value);
//
//    boolean getRelaySecurity();
//
//    void setRelaySecurity(boolean value);
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

    boolean getWsPublish();

    void setWsPublish(boolean value);

    boolean getRsPublish();

    void setRsPublish(boolean value);

    String getRsPublishComponent();

    void setRsPublishComponent(String value);

    RootComponentConfiguration.KERNEL[] getKernels();

    void setKernels(String... value);

    void setKernels(RootComponentConfiguration.KERNEL... value);

    boolean getMavenDependencies();

    void setMavenDependencies(boolean value);

    boolean getMonitoringComp();

    void setMonitoringComp(boolean value);

    boolean getSensors();

    void setSensors(boolean value);

    String getThreadpoolClass();

    void setThreadpoolClass(String value);

    String getContextServiceClass();

    void setContextServiceClass(String value);

    boolean getDf();

    void setDf(boolean value);

    boolean getClock();

    void setClock(boolean value);

//    boolean getMessage();
//
//    void setMessage(boolean value);

    boolean getSimul();

    void setSimul(boolean value);

    boolean getFiletransfer();

    void setFiletransfer(boolean value);

    boolean getMarshal();

    void setMarshal(boolean value);

    boolean getSecurity();

    void setSecurity(boolean value);

    boolean getLibrary();

    void setLibrary(boolean value);

    boolean getSettings();

    void setSettings(boolean value);

    boolean getContext();

    void setContext(boolean value);

    boolean getAddress();

    void setAddress(boolean value);

    boolean getSuperpeer();

    void setSuperpeer(boolean value);
    
    boolean getSuperpeerClient();

    void setSuperpeerClient(boolean value);
}
