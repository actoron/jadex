package jadex.base;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;


public abstract class AbstractPlatformConfiguration implements IStarterConfiguration, IRootComponentConfiguration
{
    /** Configuration of the starter. **/
    private StarterConfiguration starterconfig;
    /** Configuration of the root component. **/
    private RootComponentConfiguration	rootconfig;

    /** Global platform data. */
    protected static final Map<IComponentIdentifier, Map<String, Object>> platformmem = new HashMap<IComponentIdentifier, Map<String, Object>>();

    // Needed here to avoid ambigous error (both interfaces contain the constant, too)
    public static final String	COMPONENT_FACTORY	= StarterConfiguration.COMPONENT_FACTORY;


    public AbstractPlatformConfiguration() {
        starterconfig = new StarterConfiguration();
        rootconfig = new RootComponentConfiguration();
    }

    /**
     * Constructor that creates a new Configuration based on an args object.
     * (which can be a Map or a String array).
     * Does not parse the args - no public use!
     * @param args
     */
    protected AbstractPlatformConfiguration(String[] args) {
        starterconfig = new StarterConfiguration();
        rootconfig = new RootComponentConfiguration();
        rootconfig.setProgramArguments(args);
    }

//    public AbstractPlatformConfiguration(RootComponentConfiguration rootconfig) {
//        this.rootconfig = rootconfig;
//    }

    /**
     * Copy constructor.
     * @param  config AbstractPlatformConfiguration
     */
    public AbstractPlatformConfiguration(AbstractPlatformConfiguration config) {
        this.starterconfig = new StarterConfiguration(config.getStarterConfig());
        this.rootconfig = new RootComponentConfiguration(config.getRootConfig());
    }

    /**
     * Returns a PlatformConfiguration with the default parameters.
     * @return
     */
    public static PlatformConfiguration getDefault()
    {
        PlatformConfiguration config = new PlatformConfiguration();
//		config.setPlatformName("jadex");
        config.getStarterConfig().setPlatformName(null);
        config.getStarterConfig().setConfigurationName("auto");
        config.getStarterConfig().setAutoShutdown(false);
        config.getStarterConfig().setPlatformComponent(SReflect.classForName0("jadex.platform.service.cms.PlatformComponent", null));
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
        rootConfig.setAwaMechanisms(RootComponentConfiguration.AWAMECHANISM.broadcast, RootComponentConfiguration.AWAMECHANISM.multicast, RootComponentConfiguration.AWAMECHANISM.message, RootComponentConfiguration.AWAMECHANISM.relay, RootComponentConfiguration.AWAMECHANISM.local);
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
        rootConfig.setKernels(RootComponentConfiguration.KERNEL.multi);
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
//		rootConfig.setRegistrySync(false);
        return config;
    }

    /**
     * Returns a PlatformConfiguration with the default parameters but without gui.
     * @return
     */
    public static PlatformConfiguration getDefaultNoGui()
    {
        PlatformConfiguration config = getDefault();
        config.getRootConfig().setGui(false);
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
        rootConfig.setKernels(RootComponentConfiguration.KERNEL.component, RootComponentConfiguration.KERNEL.micro, RootComponentConfiguration.KERNEL.bpmn, RootComponentConfiguration.KERNEL.v3);
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
        rootConfig.setCli(false);
        rootConfig.setCliConsole(false);

        rootConfig.setChat(false);

        rootConfig.setAwareness(false);
        rootConfig.setAwaMechanisms();

        rootConfig.setLocalTransport(true); // needed by message
        rootConfig.setNioTcpTransport(false);
        rootConfig.setRelayTransport(false);
        rootConfig.setSslTcpTransport(false);

        rootConfig.setKernels(RootComponentConfiguration.KERNEL.micro);
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
        rootConfig.setAwaMechanisms(RootComponentConfiguration.AWAMECHANISM.relay);
        rootConfig.setAwaFast(true);	// Make sure awareness finds other platforms quickly
        rootConfig.setRelayTransport(true);

        rootConfig.setSecurity(true);	// enable security when remote comm.

        return config;
    }


    // -----------------------------
    // 		Argument Parsing
    // -----------------------------

    /**
     *  Create the platform.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     */
//	public static StarterConfiguration processArgs(Map<String, String> args)
//	{
//		StarterConfiguration config = new StarterConfiguration(args);
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
     * Returns the configuration of the root component.
     * @return RootComponentConfiguration
     */
    public RootComponentConfiguration getRootConfig()
    {
        return rootconfig;
    }

    /**
     * Returns the configuration of the root component.
     * @return RootComponentConfiguration
     */
    public StarterConfiguration getStarterConfig()
    {
        return starterconfig;
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     */
    public static PlatformConfiguration processArgs(String args)
    {
        return processArgs(args.split("\\s+"));
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
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
        config.getRootConfig().setProgramArguments(args);

        return config;
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     *  @deprecated since 3.0.7. Use other processArgs methods instead.
     */
    @Deprecated
    public static PlatformConfiguration processArgs(Map<String, String> args)
    {
        PlatformConfiguration config = new PlatformConfiguration(); // ?! hmm needs to be passed as parameter also?
        if(args!=null)
        {
            for(Map.Entry<String, String> arg: args.entrySet())
            {
                parseArg(arg.getKey(), arg.getValue(), config);
            }
        }
        return config;
    }

//    public static void parseArg(String key, String stringValue, PlatformConfiguration config) {
//        config.parseArg(key, stringValue);
//    }

    public void parseArgs(String[] args) {
        parseArgs(args, this);
    }

    public void parseArg(String key, String val) {
        parseArg(key, val, this);
    }

    public static void parseArgs(String[] args, AbstractPlatformConfiguration config) {
        for(int i=0; args!=null && i<args.length; i+=2)
        {
            parseArg(args[i], args[i+1], config);
        }
    }

    public static void parseArg(String okey, String val, AbstractPlatformConfiguration config) {
        String key = okey.startsWith("-")? okey.substring(1): okey;
        Object value = val;
        if(!StarterConfiguration.RESERVED.contains(key))
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

        config.getStarterConfig().parseArg(key, val, value);
    }


    /**
     * Sets the platform model to extract configuration values from it.
     * @param model
     */
    public void setPlatformModel(IModelInfo model)
    {
        starterconfig.setPlatformModel(model);
        rootconfig.setModel(model);
    }

    /**
     * Set a value to both configs, not recommended.
     * Use specific set methods instead.
     * @param key
     * @param value
     * @deprecated since 3.1.3
     */
    @Deprecated
    public void setValue(String key, Object value) {
        starterconfig.setValue(key, value);
        rootconfig.setValue(key, value);
    }

    /**
     * Returns a value. This only checks the starterconfig (for compatibility reasons)!
     * Use specific get methods instead.
     * @param key
     * @return
     * @deprecated since 3.0.2
     */
    @Deprecated
    Object getValue(String key) {
        Object value = starterconfig.getValue(key);
//        if (value == null) {
//            value = rootconfig.getValue(key);
//        }
        return value;
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
            return StarterConfiguration.DEFAULT_REMOTE_TIMEOUT;

        platform = platform.getRoot();
        return hasPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT)? ((Long)getPlatformValue(platform, DATA_DEFAULT_REMOTE_TIMEOUT)).longValue(): StarterConfiguration.DEFAULT_REMOTE_TIMEOUT;
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
            return StarterConfiguration.DEFAULT_LOCAL_TIMEOUT;

        platform = platform.getRoot();
        return hasPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT)? ((Long)getPlatformValue(platform, DATA_DEFAULT_LOCAL_TIMEOUT)).longValue(): StarterConfiguration.DEFAULT_LOCAL_TIMEOUT;
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
        return !Boolean.FALSE.equals(getPlatformValue(platform, StarterConfiguration.DATA_REALTIMETIMEOUT));
    }

    /**
     *  Check if the parameter copy flag is set for a platform.
     */
    public static synchronized boolean	isParameterCopy(IComponentIdentifier platform)
    {
        // not equals false to make true the default.
        return !Boolean.FALSE.equals(getPlatformValue(platform, StarterConfiguration.DATA_PARAMETERCOPY));
    }

    // --------------- Getter/setter --------------------- //

//    /**
//     * Enables/disables distributed registry synchronization.
//     * @param value
//     */
//    public void setRegistrySync(boolean value)
//    {
//        rootconfig.setRegistrySync(value);
//    }
//    /**
//     * Get the flag for distributed registry synchronization.
//     * @return
//     */
//    public boolean getRegistrySync()
//    {
//        return rootconfig.getRegistrySync();
//    }


    /**
     * Checks this configuration for consistency errors.
     */
    protected void checkConsistency()
    {
        starterconfig.checkConsistency();
        rootconfig.checkConsistency();
    }

    /**
     * Enhance this config with given other config.
     * Will overwrite all values that are set in the other config.
     * @param other
     */
    public void enhanceWith(PlatformConfiguration other)
    {
        starterconfig.enhanceWith(other.getStarterConfig());
        rootconfig.enhanceWith(other.getRootConfig());
    }


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("");

        Set<Map.Entry<String, Object>> entries = starterconfig.cmdargs.entrySet();

        for (Map.Entry<String, Object> arg: entries)
        {
            if (!(arg.getValue() == null || arg.getValue().equals(false)))
            {
                sb.append(arg.getKey());
                sb.append(": ");
                sb.append(arg.getValue());
                sb.append("\n");
            }
        }

        entries = rootconfig.getArgs().entrySet();

        for (Map.Entry<String, Object> arg: entries)
        {
            if (!(arg.getValue() == null || arg.getValue().equals(false)))
            {
                sb.append(arg.getKey());
                sb.append(": ");
                sb.append(arg.getValue());
                sb.append("\n");
            }
        }

        sb.append(rootconfig.toString());
        return sb.toString();
    }

}
