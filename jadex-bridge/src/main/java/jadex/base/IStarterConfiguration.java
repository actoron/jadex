package jadex.base;

import java.util.List;

import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.monitoring.IMonitoringService;

public interface IStarterConfiguration {

    /** ======== Arguments used by starter. ======== **/

    /** The name of the platform component (null for auto generation). To use a custom prefix name
     and an auto generated postfix the name should end with _* (3 digits) or with _ and an arbitrary number of +, e.g. _++++.  **/
    String PLATFORM_NAME = "platformname"; // class: String default: "jadex"
    /** The configuration to use. **/
    String CONFIGURATION_NAME = "configname"; // class: String default: "auto"
    /** Automatically shut down the platform when no user agents are running anymore. **/
    String AUTOSHUTDOWN = "autoshutdown"; // class: boolean default: false
    /** Tell the starter to use the default platform component implementation (usually no need to change). **/
    String PLATFORM_COMPONENT = "platformcomponent"; // class: Class default: jadex.platform.service.cms.PlatformComponent.class

    //-------- platform data keys --------

    /** Flag if copying parameters for local service calls is allowed. */
    String DATA_PARAMETERCOPY = "parametercopy";

    /**  Flag if local timeouts should be realtime (instead of clock dependent). */
    String DATA_REALTIMETIMEOUT = "realtimetimeout";

    /** The local service registry data key. */
    String DATA_SERVICEREGISTRY = "serviceregistry";

    /** The transport address book data key. */
    String DATA_ADDRESSBOOK = "addressbook";
    
    /** The serialization services for serializing and en/decoding objects including remote reference handling. */
    String DATA_SERIALIZATIONSERVICES = "serialservs";
    
    /** The transport cache used to . */
    String DATA_TRANSPORTCACHE = "transportcache";
    
    /** The CMS component map. */
    String DATA_COMPONENTMAP = "componentmap";
    
    /** Constant for local default timeout name. */
    String DATA_DEFAULT_LOCAL_TIMEOUT = "default_local_timeout";

    /** Constant for remote default timeout name. */
    String DATA_DEFAULT_REMOTE_TIMEOUT = "default_remote_timeout";

    //-------- constants --------

    /** The default platform configuration. */
//	String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/Platform.component.xml";
    String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/PlatformAgent.class";
//    String FALLBACK_PLATFORM_CONFIGURATION = "jadex/platform/PlatformNGAgent.class";

    /** The default component factory to be used for platform component. */
//	String FALLBACK_COMPONENT_FACTORY = "jadex.component.ComponentComponentFactory";
    String FALLBACK_COMPONENT_FACTORY = "jadex.micro.MicroAgentFactory";

    /** The configuration file argument. */
    String CONFIGURATION_FILE = "conf";

    /** The component factory classname argument. */
    String COMPONENT_FACTORY = "componentfactory";

    /** The monitoring flag argument. */
    String MONITORING = "monitoring";

    /** The component flag argument (for starting an additional component). */
    String COMPONENT = "component";

    /** The persist flag argument. */
    String PERSIST = "persist";

    /** The default timeout argument. */
    String DEFTIMEOUT = "deftimeout";

    /** The debug futures flag argument. */
    String DEBUGFUTURES = "debugfutures";

    /** The debug futures services argument. */
    String DEBUGSERVICES = "debugservices";

    /** The debug futures services argument. */
    String DEBUGSTEPS = "debugsteps";

    /** The stack compaction disable flag argument. */
    String NOSTACKCOMPACTION = "nostackcompaction";

    /** The opengl disable flag argument. */
    String OPENGL = "opengl";

    /** Flag to enable or disable the platform as superpeer. **/
    String SUPERPEER = "superpeer";


    void setPlatformModel(IModelInfo model);

    String getPlatformName();

    void setPlatformName(String value);

    String getConfigurationName();

    void setConfigurationName(String value);

    boolean getAutoShutdown();

    void setAutoShutdown(boolean value);

    Class getPlatformComponent();

    void setPlatformComponent(Class value);

    void setDefaultTimeout(long to);

    Long getDefaultTimeout();

    long getLocalDefaultTimeout();

    long getRemoteDefaultTimeout();

    void addComponent(Class clazz);

    void addComponent(String path);

    void setComponents(List<String> newcomps);

    List<String> getComponents();

    String getComponentFactory();

    void setConfigurationFile(String value);

    String getConfigurationFile();

    void setMonitoring(IMonitoringService.PublishEventLevel level);

    IMonitoringService.PublishEventLevel getMonitoring();

    void setPersist(boolean value);

    boolean getPersist();

    void setDebugFutures(boolean value);

    boolean getDebugFutures();

    void setDebugServices(boolean value);

    boolean getDebugServices();

    void setDebugSteps(boolean value);

    boolean getDebugSteps();

    void setNoStackCompaction(boolean value);

    boolean getNoStackCompaction();

    boolean getBooleanValue(String key);

    void setOpenGl(boolean value);

    boolean getOpenGl();
}
