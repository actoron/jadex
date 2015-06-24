package jadex.base;

import java.util.HashMap;
import java.util.Map;

public class RootComponentConfiguration
{
	// ----- arguments only handled by root component -----
	
	/** The argument key, where program arguments are stored for later retrieval. */
	public static final String PROGRAM_ARGUMENTS = "programarguments";

	/** The component factory instance. */
	public static final String	COMPONENT_FACTORY	= PlatformConfiguration.COMPONENT_FACTORY;

	/** The IPlatformComponentAccess instance **/
	public static final String	PLATFORM_ACCESS	= "platformaccess";

	/** Flag to enable or disable dht providing features (service discovery). **/
	public static final String DHT_PROVIDE = PlatformConfiguration.DHT_PROVIDE;
	

	// ----- arguments handled by starter AND root component -----
	
	/** Flag to enable or disable dht features (service discovery). **/
	public static final String DHT = PlatformConfiguration.DHT;

	private Map<String, Object>	rootargs;
	
	public RootComponentConfiguration()
	{
		rootargs = new HashMap<String, Object>();	// Arguments of root component (platform)
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

}
