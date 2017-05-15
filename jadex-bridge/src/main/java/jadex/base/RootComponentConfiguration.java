package jadex.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.factory.IPlatformComponentAccess;
import jadex.javaparser.SJavaParser;


/**
 * Configuration of the root platform component.
 */
public class RootComponentConfiguration implements IRootComponentConfiguration
{

	private IModelInfo			model;

	public void setModel(IModelInfo model)
	{
		this.model = model;
	}



	/** All configured parameters as map. **/
	private Map<String, Object>	rootargs;

	/** The activated kernels. **/
	private KERNEL[]			kernels;

	/** The activated awareness machanisms. **/
	private AWAMECHANISM[]		awamechanisms;

	/**
	 * Create a new configuration.
	 */
	public RootComponentConfiguration()
	{
		rootargs = new HashMap<String, Object>(); // Arguments of root component
													// (platform)
	}

	/**
	 * Copy constructor
	 * 
	 * @param source
	 */
	public RootComponentConfiguration(RootComponentConfiguration source)
	{
		rootargs = new HashMap<String, Object>(source.rootargs);
	}

	/**
	 * Set a value in the root component configuration
	 * 
	 * @param key a key from the constants in this class.
	 * @param val the value
	 */
	public void setValue(String key, Object val)
	{
		rootargs.put(key, val);
	}

	/**
	 * Returns a value of a given configuration parameter.
	 * 
	 * @param key the key
	 * @return the value
	 */
	public Object getValue(String key)
	{
		Object val = rootargs.get(key);
		if(val == null && model != null)
		{
			val = getValueFromModel(key);
		}
		return val;
	}

	@Override
	public Map<String, Object> getArgs()
	{
		return rootargs;
	}

	@Override
	public void setProgramArguments(String[] args)
	{
		setValue(PROGRAM_ARGUMENTS, args);
	}

	// // internal
	@Override
	public boolean getWelcome()
	{
		return Boolean.TRUE.equals(getValue(WELCOME));
	}

	@Override
	public void setWelcome(boolean value)
	{
		setValue(WELCOME, value);
	}

	// // internal
	// public IPlatformComponentAccess getPlatformAccess()
	// {
	// return (IPlatformComponentAccess)getValue(PLATFORM_ACCESS);
	// }
	@Override
	public void setPlatformAccess(IPlatformComponentAccess value)
	{
		setValue(PLATFORM_ACCESS, value);
	}

	//
	// // internal
	// public IComponentFactory getComponentFactory()
	// {
	// return (IComponentFactory)getValue(COMPONENT_FACTORY);
	// }
	@Override
	public void setComponentFactory(IComponentFactory value)
	{
		setValue(COMPONENT_FACTORY, value);
	}

	// individual getters/setters

	@Override
	public boolean getGui()
	{
		return Boolean.TRUE.equals(getValue(GUI));
	}

	@Override
	public void setGui(boolean value)
	{
		setValue(GUI, value);
	}

	@Override
	public boolean getCli()
	{
		return Boolean.TRUE.equals(getValue(CLI));
	}

	@Override
	public void setCli(boolean value)
	{
		setValue(CLI, value);
	}

	@Override
	public boolean getCliConsole()
	{
		return Boolean.TRUE.equals(getValue(CLICONSOLE));
	}

	@Override
	public void setCliConsole(boolean value)
	{
		setValue(CLICONSOLE, value);
	}

	@Override
	public boolean getSaveOnExit()
	{
		return Boolean.TRUE.equals(getValue(SAVEONEXIT));
	}

	@Override
	public void setSaveOnExit(boolean value)
	{
		setValue(SAVEONEXIT, value);
	}

	@Override
	public String getJccPlatforms()
	{
		return (String)getValue(JCCPLATFORMS);
	}

	@Override
	public void setJccPlatforms(String value)
	{
		setValue(JCCPLATFORMS, value);
	}

	@Override
	public boolean getLogging()
	{
		return Boolean.TRUE.equals(getValue(LOGGING));
	}

	@Override
	public void setLogging(boolean value)
	{
		setValue(LOGGING, value);
	}

	@Override
	public Level getLoggingLevel()
	{
		return (Level)getValue(LOGGING_LEVEL);
	}

	@Override
	public void setLoggingLevel(Level value)
	{
		setValue(LOGGING_LEVEL, value);
	}

	@Override
	public boolean getSimulation()
	{
		return Boolean.TRUE.equals(getValue(SIMULATION));
	}

	@Override
	public void setSimulation(boolean value)
	{
		setValue(SIMULATION, value);
	}

	@Override
	public boolean getAsyncExecution()
	{
		return Boolean.TRUE.equals(getValue(ASYNCEXECUTION));
	}

	@Override
	public void setAsyncExecution(boolean value)
	{
		setValue(ASYNCEXECUTION, value);
	}

	@Override
	public boolean getPersist()
	{
		return Boolean.TRUE.equals(getValue(PERSIST));
	}

	@Override
	public void setPersist(boolean value)
	{
		setValue(PERSIST, value);
	}

	@Override
	public boolean getUniqueIds()
	{
		return Boolean.TRUE.equals(getValue(UNIQUEIDS));
	}

	@Override
	public void setUniqueIds(boolean value)
	{
		setValue(UNIQUEIDS, value);
	}

	@Override
	public boolean getThreadpoolDefer()
	{
		return Boolean.TRUE.equals(getValue(THREADPOOLDEFER));
	}

	@Override
	public void setThreadpoolDefer(boolean value)
	{
		setValue(THREADPOOLDEFER, value);
	}

	@Override
	public String getLibPath()
	{
		return (String)getValue(LIBPATH);
	}

	@Override
	public void setLibPath(String value)
	{
		setValue(LIBPATH, value);
	}

	@Override
	public ClassLoader getBaseClassloader()
	{
		return (ClassLoader)getValue(BASECLASSLOADER);
	}

	@Override
	public void setBaseClassloader(ClassLoader value)
	{
		setValue(BASECLASSLOADER, value);
	}

	@Override
	public boolean getChat()
	{
		return Boolean.TRUE.equals(getValue(CHAT));
	}

	@Override
	public void setChat(boolean value)
	{
		setValue(CHAT, value);
	}

	@Override
	public boolean getAwareness()
	{
		return Boolean.TRUE.equals(getValue(AWARENESS));
	}

	@Override
	public void setAwareness(boolean value)
	{
		setValue(AWARENESS, value);
	}

	@Override
	public AWAMECHANISM[] getAwaMechanisms()
	{
		return awamechanisms;
	}

	@Override
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

	@Override
	public long getAwaDelay()
	{
		return (Long)getValue(AWADELAY);
	}

	@Override
	public void setAwaDelay(long value)
	{
		setValue(AWADELAY, value);
	}

	@Override
	public boolean isAwaFast()
	{
		return (Boolean)getValue(AWAFAST);
	}

	@Override
	public void setAwaFast(boolean value)
	{
		setValue(AWAFAST, value);
	}

	@Override
	public String getAwaIncludes()
	{
		return (String)getValue(AWAINCLUDES);
	}

	@Override
	public void setAwaIncludes(String value)
	{
		setValue(AWAINCLUDES, value);
	}

	@Override
	public String getAwaExcludes()
	{
		return (String)getValue(AWAEXCLUDES);
	}

	@Override
	public void setAwaExcludes(String value)
	{
		setValue(AWAEXCLUDES, value);
	}

	@Override
	public boolean getBinaryMessages()
	{
		return Boolean.TRUE.equals(getValue(BINARYMESSAGES));
	}

	@Override
	public void setBinaryMessages(boolean value)
	{
		setValue(BINARYMESSAGES, value);
	}

	@Override
	public boolean getStrictCom()
	{
		return Boolean.TRUE.equals(getValue(STRICTCOM));
	}

	@Override
	public void setStrictCom(boolean value)
	{
		setValue(STRICTCOM, value);
	}

	@Override
	public boolean getUsePass()
	{
		return Boolean.TRUE.equals(getValue(USEPASS));
	}

	@Override
	public void setUsePass(boolean value)
	{
		setValue(USEPASS, value);
	}

	@Override
	public boolean getPrintPass()
	{
		return Boolean.TRUE.equals(getValue(PRINTPASS));
	}

	@Override
	public void setPrintPass(boolean value)
	{
		setValue(PRINTPASS, value);
	}

	@Override
	public boolean getTrustedLan()
	{
		return Boolean.TRUE.equals(getValue(TRUSTEDLAN));
	}

	@Override
	public void setTrustedLan(boolean value)
	{
		setValue(TRUSTEDLAN, value);
	}

	@Override
	public String getNetworkName()
	{
		return (String)getValue(NETWORKNAME);
	}

	@Override
	public void setNetworkName(String value)
	{
		setValue(NETWORKNAME, value);
	}

	@Override
	public String getNetworkPass()
	{
		return (String)getValue(NETWORKPASS);
	}

	@Override
	public void setNetworkPass(String value)
	{
		setValue(NETWORKPASS, value);
	}

	@Override
	public Map getVirtualNames()
	{
		return (Map)getValue(VIRTUALNAMES);
	}

	@Override
	public void setVirtualNames(Map value)
	{
		setValue(VIRTUALNAMES, value);
	}

	@Override
	public long getValidityDuration()
	{
		return (Long)getValue(VALIDITYDURATION);
	}

	@Override
	public void setValidityDuration(long value)
	{
		setValue(VALIDITYDURATION, value);
	}

	@Override
	public boolean getLocalTransport()
	{
		return Boolean.TRUE.equals(getValue(LOCALTRANSPORT));
	}

	@Override
	public void setLocalTransport(boolean value)
	{
		setValue(LOCALTRANSPORT, value);
	}

	@Override
	public boolean getTcpTransport()
	{
		return Boolean.TRUE.equals(getValue(TCPTRANSPORT));
	}

	@Override
	public void setTcpTransport(boolean value)
	{
		setValue(TCPTRANSPORT, value);
	}

	@Override
	public int getTcpPort()
	{
		return (Integer)getValue(TCPPORT);
	}

	@Override
	public void setTcpPort(int value)
	{
		setValue(TCPPORT, value);
	}

	@Override
	public boolean getNioTcpTransport()
	{
		return Boolean.TRUE.equals(getValue(NIOTCPTRANSPORT));
	}

	@Override
	public void setNioTcpTransport(boolean value)
	{
		setValue(NIOTCPTRANSPORT, value);
	}

	@Override
	public int getNioTcpPort()
	{
		return (Integer)getValue(NIOTCPPORT);
	}

	@Override
	public void setNioTcpPort(int value)
	{
		setValue(NIOTCPPORT, value);
	}

	@Override
	public boolean getRelayTransport()
	{
		return Boolean.TRUE.equals(getValue(RELAYTRANSPORT));
	}

	@Override
	public void setRelayTransport(boolean value)
	{
		setValue(RELAYTRANSPORT, value);
	}

	@Override
	public String getRelayAddress()
	{
		return (String)getValue(RELAYADDRESS);
	}

	@Override
	public void setRelayAddress(String value)
	{
		setValue(RELAYADDRESS, value);
	}

	@Override
	public boolean getRelaySecurity()
	{
		return Boolean.TRUE.equals(getValue(RELAYSECURITY));
	}

	@Override
	public void setRelaySecurity(boolean value)
	{
		setValue(RELAYSECURITY, value);
	}

	@Override
	public boolean getRelayAwaonly()
	{
		return Boolean.TRUE.equals(getValue(RELAYAWAONLY));
	}

	@Override
	public void setRelayAwaonly(boolean value)
	{
		setValue(RELAYAWAONLY, value);
	}

	@Override
	public boolean getSslTcpTransport()
	{
		return Boolean.TRUE.equals(getValue(SSLTCPTRANSPORT));
	}

	@Override
	public void setSslTcpTransport(boolean value)
	{
		setValue(SSLTCPTRANSPORT, value);
	}

	@Override
	public int getSslTcpPort()
	{
		return (Integer)getValue(SSLTCPPORT);
	}

	@Override
	public void setSslTcpPort(int value)
	{
		setValue(SSLTCPPORT, value);
	}

	@Override
	public boolean getWsPublish()
	{
		return Boolean.TRUE.equals(getValue(WSPUBLISH));
	}

	@Override
	public void setWsPublish(boolean value)
	{
		setValue(WSPUBLISH, value);
	}

	@Override
	public boolean getRsPublish()
	{
		return Boolean.TRUE.equals(getValue(RSPUBLISH));
	}

	@Override
	public void setRsPublish(boolean value)
	{
		setValue(RSPUBLISH, value);
	}

	@Override
	public String getRsPublishComponent()
	{
		return (String)getValue(RSPUBLISHCOMPONENT);
	}

	@Override
	public void setRsPublishComponent(String value)
	{
		setValue(RSPUBLISHCOMPONENT, value);
	}

	@Override
	public KERNEL[] getKernels()
	{
		return kernels;
	}

	@Override
	public void setKernels(String... value)
	{
		List<KERNEL> kernelList = new ArrayList<KERNEL>();
		for(String kernel : value)
		{
			kernelList.add(KERNEL.valueOf(kernel));
		}
		setKernels(kernelList.toArray(new KERNEL[kernelList.size()]));
	}

	@Override
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

	@Override
	public boolean getMavenDependencies()
	{
		return Boolean.TRUE.equals(getValue(MAVEN_DEPENDENCIES));
	}

	@Override
	public void setMavenDependencies(boolean value)
	{
		setValue(MAVEN_DEPENDENCIES, value);
	}

	@Override
	public boolean getMonitoringComp()
	{
		return Boolean.TRUE.equals(getValue(MONITORINGCOMP));
	}

	@Override
	public void setMonitoringComp(boolean value)
	{
		setValue(MONITORINGCOMP, value);
	}

	@Override
	public boolean getSensors()
	{
		return Boolean.TRUE.equals(getValue(SENSORS));
	}

	@Override
	public void setSensors(boolean value)
	{
		setValue(SENSORS, value);
	}

	@Override
	public String getThreadpoolClass()
	{
		return (String)getValue(THREADPOOLCLASS);
	}

	@Override
	public void setThreadpoolClass(String value)
	{
		setValue(THREADPOOLCLASS, value);
	}

	@Override
	public String getContextServiceClass()
	{
		return (String)getValue(CONTEXTSERVICECLASS);
	}

	@Override
	public void setContextServiceClass(String value)
	{
		setValue(CONTEXTSERVICECLASS, value);
	}

	@Override
	public boolean getDf()
	{
		return Boolean.TRUE.equals(getValue(DF));
	}

	@Override
	public void setDf(boolean value)
	{
		setValue(DF, value);
	}

	@Override
	public boolean getClock()
	{
		return Boolean.TRUE.equals(getValue(CLOCK));
	}

	@Override
	public void setClock(boolean value)
	{
		setValue(CLOCK, value);
	}

	@Override
	public boolean getMessage()
	{
		return Boolean.TRUE.equals(getValue(MESSAGE));
	}

	@Override
	public void setMessage(boolean value)
	{
		setValue(MESSAGE, value);
	}

	@Override
	public boolean getSimul()
	{
		return Boolean.TRUE.equals(getValue(SIMUL));
	}

	@Override
	public void setSimul(boolean value)
	{
		setValue(SIMUL, value);
	}

	@Override
	public boolean getFiletransfer()
	{
		return Boolean.TRUE.equals(getValue(FILETRANSFER));
	}

	@Override
	public void setFiletransfer(boolean value)
	{
		setValue(FILETRANSFER, value);
	}

	@Override
	public boolean getMarshal()
	{
		return Boolean.TRUE.equals(getValue(MARSHAL));
	}

	@Override
	public void setMarshal(boolean value)
	{
		setValue(MARSHAL, value);
	}

	@Override
	public boolean getSecurity()
	{
		return Boolean.TRUE.equals(getValue(SECURITY));
	}

	@Override
	public void setSecurity(boolean value)
	{
		setValue(SECURITY, value);
	}

	@Override
	public boolean getLibrary()
	{
		return Boolean.TRUE.equals(getValue(LIBRARY));
	}

	@Override
	public void setLibrary(boolean value)
	{
		setValue(LIBRARY, value);
	}

	@Override
	public boolean getSettings()
	{
		return Boolean.TRUE.equals(getValue(SETTINGS));
	}

	@Override
	public void setSettings(boolean value)
	{
		setValue(SETTINGS, value);
	}

	@Override
	public boolean getContext()
	{
		return Boolean.TRUE.equals(getValue(CONTEXT));
	}

	@Override
	public void setContext(boolean value)
	{
		setValue(CONTEXT, value);
	}

	@Override
	public boolean getAddress()
	{
		return Boolean.TRUE.equals(getValue(ADDRESS));
	}

	@Override
	public void setAddress(boolean value)
	{
		setValue(ADDRESS, value);
	}

	@Override
	public boolean getRegistrySync()
	{
		return Boolean.TRUE.equals(getValue(REGISTRY_SYNC));
	}

	@Override
	public void setRegistrySync(boolean value)
	{
		setValue(REGISTRY_SYNC, value);
	}

	/**
	 * Enhance this config with given other config. Will overwrite all values
	 * that are set in the other config.
	 * 
	 * @param other
	 */
	public void enhanceWith(RootComponentConfiguration other)
	{
		for(Map.Entry<String, Object> entry : other.rootargs.entrySet())
		{
			this.setValue(entry.getKey(), entry.getValue());
		}
	}

	/**
	 * Checks this config for consistency.
	 */
	protected void checkConsistency()
	{
		StringBuilder errorText = new StringBuilder();
		Object publish = getValue(RSPUBLISH);
		Object publishComponent = getValue(RSPUBLISHCOMPONENT);
		
		if(Boolean.TRUE.equals(publish) && (publishComponent == null || "".equals(publishComponent)))
		{
			errorText.append(RSPUBLISH + " set to true, but no " + RSPUBLISHCOMPONENT + " found.");
		}

		Object kernels = getValue(KERNELS); // may need to get value from model
		if (kernels == null || ((String) kernels).trim().isEmpty()) {
			errorText.append("No Kernels set. Cannot start platform.");
		}

		for (String argName:BOOLEAN_ARGS) {
			if (!isBoolean(rootargs.get(argName))) {
				errorText.append(USEPASS + " must be a boolean value (or null), but is set to: " + getValue(USEPASS));
			}
		}

		if(errorText.length() != 0)
		{
			throw new RuntimeException("Configuration consistency error: \n" + errorText.toString());
		}
	}

	/**
	 * Check whether value can be converted to boolean or not.
	 * @param value
	 * @return
	 */
	private boolean isBoolean(Object value) {
		boolean result = false;
		if (value != null) {
			if (value instanceof Boolean) {
				result = true;
			}
		} else {
			result = true;
		}
		return result;
	}

	/**
	 * Returns the value as it is used in the (already loaded) model.
	 * 
	 * @param key
	 * @return Object
	 */
	private Object getValueFromModel(String key)
	{
		Object val;
		Argument argument = (Argument)model.getArgument(key);
		val = SJavaParser.getParsedValue(argument, model.getAllImports(), null, Starter.class.getClassLoader());
		if(val == null)
		{
			// get default value
			val = SJavaParser.getParsedValue(argument.getDefaultValue(), model.getAllImports(), null, Starter.class.getClassLoader());
		}
		return val;
	}
}
