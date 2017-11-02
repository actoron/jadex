package jadex.base;

import java.util.List;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.monitoring.IMonitoringService;

/**
 *  Interface for starter configuration.
 */
public interface IStarterConfiguration 
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

    /** Flag to enable or disable the platform as superpeer. **/
    public static final String SUPERPEER = "superpeer";

    /** Flag to enable or disable the platform as superpeern client. **/
    public static final String SUPERPEERCLIENT = "superpeerclient";

    /** Flag if exceptions should be printed. */
    public static final String PRINTEXCEPTIONS = "printexceptions";
    
    /** Flag if copying parameters for local service calls is allowed. */
    public static final String PARAMETERCOPY = "parametercopy";

    /**  Flag if local timeouts should be realtime (instead of clock dependent). */
    public static final String REALTIMETIMEOUT = "realtimetimeout";
    
    /** Constant for remote default timeout. */
    public static long DEFAULT_REMOTE_TIMEOUT = PlatformConfigurationHandler.getDefaultTimeout();

    /** Constant for local default timeout. */
    public static long DEFAULT_LOCAL_TIMEOUT = PlatformConfigurationHandler.getDefaultTimeout();

    /** The reserved platform parameters. Those are (usually) not handled by the root component. */
    public static final Set<String> RESERVED = PlatformConfigurationHandler.createReserved();

    /**
     *  Set the platform model.
     *  @param model The model info of the platform.
     */
    public void setPlatformModel(IModelInfo model);

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
     *  Get the auto shutdown flag.
     *  @return The auto shutdown flag.
     */
    public boolean getAutoShutdown();

    /**
     *  Set the auto shutdown flag.
     *  @param value The value.
     */
    public void setAutoShutdown(boolean value);

    // todo: make classinfo
    /**
     *  Get the platform component.
     *  @return The platform component class.
     */
//    public Class<?> getPlatformComponent();
    public ClassInfo getPlatformComponent();

    // todo: make classinfo
    /**
     *  Set the platform component.
     *  @param value The platform component.
     */
//    public void setPlatformComponent(Class<?> value);
    public void setPlatformComponent(ClassInfo value);

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
     *  Get the local default timeout.
     *  @return The local default timeout.
     */
    public long getLocalDefaultTimeout();

    /**
     *  Get the remote default timeout.
     *  @return The remote default timeout.
     */
    public long getRemoteDefaultTimeout();

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
     *  Get the component factory.
     *  @return The component factory filename.
     */
    public String getComponentFactory();

    /**
     *  Set the configuration file.
     *  @param value The configuration file.
     */
    public void setConfigurationFile(String value);

    /**
     *  Get the configuration file.
     *  @return The configuration file.
     */
    public String getConfigurationFile();

    /**
     *  Set the monitoring level.
     *  @param level The monitoring level.
     */
    public void setMonitoring(IMonitoringService.PublishEventLevel level);

    /**
     *  Get the monitoring level.
     *  @return The level.
     */
    public IMonitoringService.PublishEventLevel getMonitoring();

//    public void setPersist(boolean value);

//    public boolean getPersist();

    /**
     *  Set the debug futures flag.
     *  @param value The debug futures flag.
     */
    public void setDebugFutures(boolean value);

    /**
     *  Get the debug futures flag.
     *  @return The debug futures flag.
     */
    public boolean getDebugFutures();

    /**
     *  Set the debug services flag.
     *  @param value The debug futures flag.
     */
    public void setDebugServices(boolean value);

    /**
     *  Get the debug services flag.
     *  @return The debug services flag.
     */
    public boolean getDebugServices();

    /**
     *  Set the debug steps flag.
     *  @param value The debug steps flag.
     */
    public void setDebugSteps(boolean value);

    /**
     *  Get the debug steps flag.
     *  @return The debug steps flag.
     */
    public boolean getDebugSteps();

    /**
     *  Set the no stack compaction flag.
     *  @param value The no stack compaction flag.
     */
    public void setNoStackCompaction(boolean value);

    /**
     *  Get the no stack compaction flag.
     *  @return The no stack compaction flag.
     */
    public boolean getNoStackCompaction();

//    /**
//     *  Get a boolean value per key.
//     *  @param key The key.
//     *  @return The boolean value.
//     */
//    public boolean getBooleanValue(String key);

    /**
     *  Set the opengl flag.
     *  @param value The opengl flag.
     */
    public void setOpenGl(boolean value);

    /**
     *  Get the opengl flag.
     *  @return The opengl flag.
     */
    public boolean getOpenGl();
    
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
     *  Parse an argument.
     *  @param key The key.
     *  @param strval The value.
     *  @param value The object value.
     */
    public void parseArg(String key, String strval, Object value);
    
    /**
	 *  Check the consistency.
	 */
	public void checkConsistency();
	
	/**
	 * Enhance this config with given other config. Will overwrite all values
	 * that are set in the other config.
	 */
	public void enhanceWith(IStarterConfiguration other);
	
	/**
	 *  Get a value per key.
	 *  @param key The key.
	 *  @return The value.
	 */
	public Object getValue(String key);
}
