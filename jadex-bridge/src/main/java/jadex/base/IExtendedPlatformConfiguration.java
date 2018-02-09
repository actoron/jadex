package jadex.base;

import jadex.bridge.ClassInfo;
import jadex.bridge.service.types.monitoring.IMonitoringService;

/**
 * 
 */
public interface IExtendedPlatformConfiguration
{
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
    
    /**
     *  Get the platform component.
     *  @return The platform component class.
     */
//    public Class<?> getPlatformComponent();
    public ClassInfo getPlatformComponent();

    /**
     *  Set the platform component.
     *  @param value The platform component.
     */
//    public void setPlatformComponent(Class<?> value);
    public void setPlatformComponent(ClassInfo value);
    
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
     *  Sets if the platform should keep admin/root privileges
     *  or attempt to drop to a user.
     *  
     *  @param value True, if the platform should retain root privileges.
     */
    public void setDropPrivileges(boolean value);

    /**
     *  Gets if the platform should keep admin/root privileges
     *  or attempt to drop to a user.
     *  
     *  @return True, if the platform should retain root privileges.
     */
    public boolean isDropPrivileges();
    
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

    // todo: refactor, in SecurityAgent other names are used
    
//    /**
//     *  Flag if the platform should be protected with password. 
//     *  @return True if protected.
//     */
//    public boolean getUsePass();
//
//    /**
//     *  Flag if the platform should be protected with password. 
//     *  @param value True for protected.
//     */
//    public void setUsePass(boolean value);

//    /**
//     *  Get the print password flag on startup.
//     *  @return Flag if password should be printed.
//     */
//    public boolean getPrintPass();
//
//    /**
//     *  Set the print password flag on startup.
//     *  @param value Flag if password should be printed.
//     */
//    public void setPrintPass(boolean value);

    // will be removed 
//    /**
//     *  Flag if trusted lan is activated. (Trusts internal ips)
//     *  @return True if trusted lan is active.
//     */
//    public boolean getTrustedLan();
//
//    /**
//     *  Flag if trusted lan is activated. (Trusts internal ips)
//     *  @param value True if trusted lan is active.
//     */
//    public void setTrustedLan(boolean value);

    // todo: ?
    
//    /**
//     *  Get the virtual names (roles).
//     *  @return The virtual names.
//     */
//    public Map<String, String> getVirtualNames();
//
//    /**
//     *  Set the virtual names (roles).
//     *  @param value The virtual names.
//     */
//    public void setVirtualNames(Map<String, String> value);

//    /**
//     *  Get the validity duration of messages.
//     *  @return The validity duration.
//     */
//    public long getValidityDuration();
//
//    /**
//     *  Set the validity duration of messages.
//     *  @param value The validity duration.
//     */
//    public void setValidityDuration(long value);

//    /**
//     *  Get the flag if the local transport is active.
//     *  @return Flag if the local transport is active.
//     */
//    public boolean getLocalTransport();
//
//    /**
//     *  Set the flag if the local transport is active.
//     *  @param value Flag if the local transport is active.
//     */
//    public void setLocalTransport(boolean value);

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
     *  Get the relay addresses.
     *  @return The relay addresses.
     */
    public String getRelayAddresses();

    /**
     *  Set the relay addresses.
     *  @param value The relay addresses.
     */
    public void setRelayAddresses(String value);

    /**
     *  Checks if the relay transport should support
     *  routing through dynamically acquired peers.
     *  
     *  @return True, if the routing service should support dynamic routing.
     */
    public boolean isRelayDynamicRouting();
    
    /**
     *  Sets if the relay transport should support
     *  routing through dynamically acquired peers.
     * 
     *  @param dynamicrouting If true, the routing service should support dynamic routing.
     */
    public void setRelayDynamicRouting(boolean dynamicrouting);
    
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

//    /**
//     *  Get the flag if maven dependencies are on.
//     *  @return The maven dependencies.
//     */
//    public boolean getMavenDependencies();
//
//    /**
//     *  Set the maven dependencies flag.
//     *  @param value The maven dependencies.
//     */
//    public void setMavenDependencies(boolean value);

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
     *  Get the threadpool class.
     *  @return The threadpool class name.
     */
    public String getThreadpoolClass();

    /**
     *  Set the threadpool class name.
     *  @param value The threadpool class name.
     */
    public void setThreadpoolClass(String value);
//
//    /**
//     *  Get the context service class.
//     *  @return The context service class.
//     */
//    public String getContextServiceClass();
//
//    /**
//     *  Get the context service class.
//     *  @param value The context service class.
//     */
//    public void setContextServiceClass(String value);
//
    
    
    // start component settings, when true the component is started
    // should ALL be renamed
    
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
    
}
