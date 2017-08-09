package jadex.base;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService;

/**
 * This class models the Configuration of a Jadex Platform.
 * It includes a configuration for the Starter as well as one for the RootComponent (usually an Agent).
 * All methods in this class are delegated to one of the former.
 */
public class PlatformConfiguration extends AbstractPlatformConfiguration
{

	// NO DELEGATES FOR:
	//
	// setPlatformModel
	// enhanceWith
	// setModel
	// setValue
	// getValue
	// checkConsistency
	//

	public PlatformConfiguration(PlatformConfiguration config) {
		super(config);
	}

	public PlatformConfiguration(String[] args) {
		super(args);
	}

	public PlatformConfiguration() {
	}


	@Override
	public Map<String, Object> getArgs() {
		return getRootConfig().getArgs();
	}

	@Override
	public void setProgramArguments(String[] args) {
		getRootConfig().setProgramArguments(args);
	}

	@Override
	public boolean getWelcome() {
		return getRootConfig().getWelcome();
	}

	@Override
	public void setWelcome(boolean value) {
		getRootConfig().setWelcome(value);
	}

	@Override
	public void setPlatformAccess(IPlatformComponentAccess value) {
		getRootConfig().setPlatformAccess(value);
	}

	@Override
	public void setComponentFactory(IComponentFactory value) {
		getRootConfig().setComponentFactory(value);
	}

	@Override
	public boolean getGui() {
		return getRootConfig().getGui();
	}

	@Override
	public void setGui(boolean value) {
		getRootConfig().setGui(value);
	}

	@Override
	public boolean getCli() {
		return getRootConfig().getCli();
	}

	@Override
	public void setCli(boolean value) {
		getRootConfig().setCli(value);
	}

	@Override
	public boolean getCliConsole() {
		return getRootConfig().getCliConsole();
	}

	@Override
	public void setCliConsole(boolean value) {
		getRootConfig().setCliConsole(value);
	}

	@Override
	public boolean getSaveOnExit() {
		return getRootConfig().getSaveOnExit();
	}

	@Override
	public void setSaveOnExit(boolean value) {
		getRootConfig().setSaveOnExit(value);
	}

	@Override
	public String getJccPlatforms() {
		return getRootConfig().getJccPlatforms();
	}

	@Override
	public void setJccPlatforms(String value) {
		getRootConfig().setJccPlatforms(value);
	}

	@Override
	public boolean getLogging() {
		return getRootConfig().getLogging();
	}

	@Override
	public void setLogging(boolean value) {
		getRootConfig().setLogging(value);
	}

	@Override
	public Level getLoggingLevel() {
		return getRootConfig().getLoggingLevel();
	}

	@Override
	public void setLoggingLevel(Level value) {
		getRootConfig().setLoggingLevel(value);
	}

	@Override
	public boolean getSimulation() {
		return getRootConfig().getSimulation();
	}

	@Override
	public void setSimulation(boolean value) {
		getRootConfig().setSimulation(value);
	}

	@Override
	public boolean getAsyncExecution() {
		return getRootConfig().getAsyncExecution();
	}

	@Override
	public void setAsyncExecution(boolean value) {
		getRootConfig().setAsyncExecution(value);
	}

	@Override
	public boolean getPersist() {
		return getRootConfig().getPersist();
	}

	@Override
	public void setDebugFutures(boolean value) {
		getStarterConfig().setDebugFutures(value);
	}

	@Override
	public boolean getDebugFutures() {
		return getStarterConfig().getDebugFutures();
	}

	@Override
	public void setDebugServices(boolean value) {
		getStarterConfig().setDebugServices(value);
	}

	@Override
	public boolean getDebugServices() {
		return getStarterConfig().getDebugServices();
	}

	@Override
	public void setDebugSteps(boolean value) {
		getStarterConfig().setDebugSteps(value);
	}

	@Override
	public boolean getDebugSteps() {
		return getStarterConfig().getDebugSteps();
	}

	@Override
	public void setNoStackCompaction(boolean value) {
		getStarterConfig().setNoStackCompaction(value);
	}

	@Override
	public boolean getNoStackCompaction() {
		return getStarterConfig().getNoStackCompaction();
	}

	@Override
	public boolean getBooleanValue(String key) {
		return getStarterConfig().getBooleanValue(key);
	}

	@Override
	public void setOpenGl(boolean value) {
		getStarterConfig().setOpenGl(value);
	}

	@Override
	public boolean getOpenGl() {
		return getStarterConfig().getOpenGl();
	}

	@Override
	public void setPersist(boolean value) {
		getRootConfig().setPersist(value);
	}

	@Override
	public boolean getUniqueIds() {
		return getRootConfig().getUniqueIds();
	}

	@Override
	public void setUniqueIds(boolean value) {
		getRootConfig().setUniqueIds(value);
	}

	@Override
	public boolean getThreadpoolDefer() {
		return getRootConfig().getThreadpoolDefer();
	}

	@Override
	public void setThreadpoolDefer(boolean value) {
		getRootConfig().setThreadpoolDefer(value);
	}

	@Override
	public String getLibPath() {
		return getRootConfig().getLibPath();
	}

	@Override
	public void setLibPath(String value) {
		getRootConfig().setLibPath(value);
	}

	@Override
	public ClassLoader getBaseClassloader() {
		return getRootConfig().getBaseClassloader();
	}

	@Override
	public void setBaseClassloader(ClassLoader value) {
		getRootConfig().setBaseClassloader(value);
	}

	@Override
	public boolean getChat() {
		return getRootConfig().getChat();
	}

	@Override
	public void setChat(boolean value) {
		getRootConfig().setChat(value);
	}

	@Override
	public boolean getAwareness() {
		return getRootConfig().getAwareness();
	}

	@Override
	public void setAwareness(boolean value) {
		getRootConfig().setAwareness(value);
	}

	@Override
	public RootComponentConfiguration.AWAMECHANISM[] getAwaMechanisms() {
		return getRootConfig().getAwaMechanisms();
	}

	@Override
	public void setAwaMechanisms(RootComponentConfiguration.AWAMECHANISM... values) {
		getRootConfig().setAwaMechanisms(values);
	}

	@Override
	public long getAwaDelay() {
		return getRootConfig().getAwaDelay();
	}

	@Override
	public void setAwaDelay(long value) {
		getRootConfig().setAwaDelay(value);
	}

	@Override
	public boolean isAwaFast() {
		return getRootConfig().isAwaFast();
	}

	@Override
	public void setAwaFast(boolean value) {
		getRootConfig().setAwaFast(value);
	}

	@Override
	public String getAwaIncludes() {
		return getRootConfig().getAwaIncludes();
	}

	@Override
	public void setAwaIncludes(String value) {
		getRootConfig().setAwaIncludes(value);
	}

	@Override
	public String getAwaExcludes() {
		return getRootConfig().getAwaExcludes();
	}

	@Override
	public void setAwaExcludes(String value) {
		getRootConfig().setAwaExcludes(value);
	}

	@Override
	public boolean getBinaryMessages() {
		return getRootConfig().getBinaryMessages();
	}

	@Override
	public void setBinaryMessages(boolean value) {
		getRootConfig().setBinaryMessages(value);
	}

	@Override
	public boolean getStrictCom() {
		return getRootConfig().getStrictCom();
	}

	@Override
	public void setStrictCom(boolean value) {
		getRootConfig().setStrictCom(value);
	}

	@Override
	public boolean getUsePass() {
		return getRootConfig().getUsePass();
	}

	@Override
	public void setUsePass(boolean value) {
		getRootConfig().setUsePass(value);
	}

	@Override
	public boolean getPrintPass() {
		return getRootConfig().getPrintPass();
	}

	@Override
	public void setPrintPass(boolean value) {
		getRootConfig().setPrintPass(value);
	}

	@Override
	public boolean getTrustedLan() {
		return getRootConfig().getTrustedLan();
	}

	@Override
	public void setTrustedLan(boolean value) {
		getRootConfig().setTrustedLan(value);
	}

	@Override
	public String getNetworkName() {
		return getRootConfig().getNetworkName();
	}

	@Override
	public void setNetworkName(String value) {
		getRootConfig().setNetworkName(value);
	}

	@Override
	public String getNetworkPass() {
		return getRootConfig().getNetworkPass();
	}

	@Override
	public void setNetworkPass(String value) {
		getRootConfig().setNetworkPass(value);
	}

	@Override
	public Map getVirtualNames() {
		return getRootConfig().getVirtualNames();
	}

	@Override
	public void setVirtualNames(Map value) {
		getRootConfig().setVirtualNames(value);
	}

	@Override
	public long getValidityDuration() {
		return getRootConfig().getValidityDuration();
	}

	@Override
	public void setValidityDuration(long value) {
		getRootConfig().setValidityDuration(value);
	}

	@Override
	public boolean getLocalTransport() {
		return getRootConfig().getLocalTransport();
	}

	@Override
	public void setLocalTransport(boolean value) {
		getRootConfig().setLocalTransport(value);
	}

	@Override
	public boolean getTcpTransport() {
		return getRootConfig().getTcpTransport();
	}

	@Override
	public void setTcpTransport(boolean value) {
		getRootConfig().setTcpTransport(value);
	}

	@Override
	public int getTcpPort() {
		return getRootConfig().getTcpPort();
	}

	@Override
	public void setTcpPort(int value) {
		getRootConfig().setTcpPort(value);
	}

	@Override
	public boolean getRelayTransport() {
		return getRootConfig().getRelayTransport();
	}

	@Override
	public void setRelayTransport(boolean value) {
		getRootConfig().setRelayTransport(value);
	}

	@Override
	public String getRelayAddress() {
		return getRootConfig().getRelayAddress();
	}

	@Override
	public void setRelayAddress(String value) {
		getRootConfig().setRelayAddress(value);
	}

	@Override
	public boolean getRelaySecurity() {
		return getRootConfig().getRelaySecurity();
	}

	@Override
	public void setRelaySecurity(boolean value) {
		getRootConfig().setRelaySecurity(value);
	}

	@Override
	public boolean getRelayAwaonly() {
		return getRootConfig().getRelayAwaonly();
	}

	@Override
	public void setRelayAwaonly(boolean value) {
		getRootConfig().setRelayAwaonly(value);
	}

	@Override
	public boolean getSslTcpTransport() {
		return getRootConfig().getSslTcpTransport();
	}

	@Override
	public void setSslTcpTransport(boolean value) {
		getRootConfig().setSslTcpTransport(value);
	}

	@Override
	public int getSslTcpPort() {
		return getRootConfig().getSslTcpPort();
	}

	@Override
	public void setSslTcpPort(int value) {
		getRootConfig().setSslTcpPort(value);
	}

	@Override
	public boolean getWsPublish() {
		return getRootConfig().getWsPublish();
	}

	@Override
	public void setWsPublish(boolean value) {
		getRootConfig().setWsPublish(value);
	}

	@Override
	public boolean getRsPublish() {
		return getRootConfig().getRsPublish();
	}

	@Override
	public void setRsPublish(boolean value) {
		getRootConfig().setRsPublish(value);
	}

	@Override
	public String getRsPublishComponent() {
		return getRootConfig().getRsPublishComponent();
	}

	@Override
	public void setRsPublishComponent(String value) {
		getRootConfig().setRsPublishComponent(value);
	}

	@Override
	public RootComponentConfiguration.KERNEL[] getKernels() {
		return getRootConfig().getKernels();
	}

	@Override
	public void setKernels(String... value) {
		getRootConfig().setKernels(value);
	}

	@Override
	public void setKernels(RootComponentConfiguration.KERNEL... value) {
		getRootConfig().setKernels(value);
	}

	@Override
	public boolean getMavenDependencies() {
		return getRootConfig().getMavenDependencies();
	}

	@Override
	public void setMavenDependencies(boolean value) {
		getRootConfig().setMavenDependencies(value);
	}

	@Override
	public boolean getMonitoringComp() {
		return getRootConfig().getMonitoringComp();
	}

	@Override
	public void setMonitoringComp(boolean value) {
		getRootConfig().setMonitoringComp(value);
	}

	@Override
	public boolean getSensors() {
		return getRootConfig().getSensors();
	}

	@Override
	public void setSensors(boolean value) {
		getRootConfig().setSensors(value);
	}

	@Override
	public String getThreadpoolClass() {
		return getRootConfig().getThreadpoolClass();
	}

	@Override
	public void setThreadpoolClass(String value) {
		getRootConfig().setThreadpoolClass(value);
	}

	@Override
	public String getContextServiceClass() {
		return getRootConfig().getContextServiceClass();
	}

	@Override
	public void setContextServiceClass(String value) {
		getRootConfig().setContextServiceClass(value);
	}

	@Override
	public boolean getDf() {
		return getRootConfig().getDf();
	}

	@Override
	public void setDf(boolean value) {
		getRootConfig().setDf(value);
	}

	@Override
	public boolean getClock() {
		return getRootConfig().getClock();
	}

	@Override
	public void setClock(boolean value) {
		getRootConfig().setClock(value);
	}

	@Override
	public boolean getMessage() {
		return getRootConfig().getMessage();
	}

	@Override
	public void setMessage(boolean value) {
		getRootConfig().setMessage(value);
	}

	@Override
	public boolean getSimul() {
		return getRootConfig().getSimul();
	}

	@Override
	public void setSimul(boolean value) {
		getRootConfig().setSimul(value);
	}

	@Override
	public boolean getFiletransfer() {
		return getRootConfig().getFiletransfer();
	}

	@Override
	public void setFiletransfer(boolean value) {
		getRootConfig().setFiletransfer(value);
	}

	@Override
	public boolean getMarshal() {
		return getRootConfig().getMarshal();
	}

	@Override
	public void setMarshal(boolean value) {
		getRootConfig().setMarshal(value);
	}

	@Override
	public boolean getSecurity() {
		return getRootConfig().getSecurity();
	}

	@Override
	public void setSecurity(boolean value) {
		getRootConfig().setSecurity(value);
	}

	@Override
	public boolean getLibrary() {
		return getRootConfig().getLibrary();
	}

	@Override
	public void setLibrary(boolean value) {
		getRootConfig().setLibrary(value);
	}

	@Override
	public boolean getSettings() {
		return getRootConfig().getSettings();
	}

	@Override
	public void setSettings(boolean value) {
		getRootConfig().setSettings(value);
	}

	@Override
	public boolean getContext() {
		return getRootConfig().getContext();
	}

	@Override
	public void setContext(boolean value) {
		getRootConfig().setContext(value);
	}

	@Override
	public boolean getAddress() {
		return getRootConfig().getAddress();
	}

	@Override
	public void setAddress(boolean value) {
		getRootConfig().setAddress(value);
	}

	@Override
	public boolean getSuperpeer() {
		return getRootConfig().getSuperpeer();
	}

	@Override
	public void setSuperpeer(boolean value) {
		getRootConfig().setSuperpeer(value);
	}

	//---- DELEGATES for StarterConfiguration -----//


//	@Override
//	public void setValue(String key, Object value) {
//		getStarterConfig().setValue(key, value);
//	}
//
//	@Override
//	public Object getValue(String key) {
//		return getStarterConfig().getValue(key);
//	}

	@Override
	public String getPlatformName() {
		return getStarterConfig().getPlatformName();
	}

	@Override
	public void setPlatformName(String value) {
		getStarterConfig().setPlatformName(value);
	}

	@Override
	public String getConfigurationName() {
		return getStarterConfig().getConfigurationName();
	}

	@Override
	public void setConfigurationName(String value) {
		getStarterConfig().setConfigurationName(value);
	}

	@Override
	public boolean getAutoShutdown() {
		return getStarterConfig().getAutoShutdown();
	}

	@Override
	public void setAutoShutdown(boolean value) {
		getStarterConfig().setAutoShutdown(value);
	}

	@Override
	public Class getPlatformComponent() {
		return getStarterConfig().getPlatformComponent();
	}

	@Override
	public void setPlatformComponent(Class value) {
		getStarterConfig().setPlatformComponent(value);
	}

	@Override
	public void setDefaultTimeout(long to) {
		getStarterConfig().setDefaultTimeout(to);
	}

	@Override
	public Long getDefaultTimeout() {
		return getStarterConfig().getDefaultTimeout();
	}

	@Override
	public long getLocalDefaultTimeout() {
		return getStarterConfig().getLocalDefaultTimeout();
	}

	@Override
	public long getRemoteDefaultTimeout() {
		return getStarterConfig().getRemoteDefaultTimeout();
	}

	@Override
	public void addComponent(Class clazz) {
		getStarterConfig().addComponent(clazz);
	}

	@Override
	public void addComponent(String path) {
		getStarterConfig().addComponent(path);
	}

	@Override
	public void setComponents(List<String> newcomps) {
		getStarterConfig().setComponents(newcomps);
	}

	@Override
	public List<String> getComponents() {
		return getStarterConfig().getComponents();
	}

	@Override
	public String getComponentFactory() {
		return getStarterConfig().getComponentFactory();
	}

	@Override
	public void setConfigurationFile(String value) {
		getStarterConfig().setConfigurationFile(value);
	}

	@Override
	public String getConfigurationFile() {
		return getStarterConfig().getConfigurationFile();
	}

	@Override
	public void setMonitoring(IMonitoringService.PublishEventLevel level) {
		getStarterConfig().setMonitoring(level);
	}

	@Override
	public IMonitoringService.PublishEventLevel getMonitoring() {
		return getStarterConfig().getMonitoring();
	}
}
