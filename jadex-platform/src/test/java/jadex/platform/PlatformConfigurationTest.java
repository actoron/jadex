package jadex.platform;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;

import jadex.base.PlatformConfiguration;
import jadex.base.RootComponentConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;

import org.junit.Test;

public class PlatformConfigurationTest
{
	@Test
	public void tester() {
		PlatformConfiguration config = new PlatformConfiguration();
		RootComponentConfiguration rootConfig = config.getRootConfig();
		rootConfig.setPlatformName("testcases_*");
		rootConfig.setGui(false);
		rootConfig.setSaveOnExit(false);
		rootConfig.setWelcome(false);
		rootConfig.setAutoShutdown(false);
		rootConfig.setPrintPass(false);
		long timeout = Starter.getLocalDefaultTimeout(null);
		
		IExternalAccess	platform = (IExternalAccess)Starter.createPlatform(config).get(timeout);
		IModelInfo defmodel = platform.getModel();
		
		IArgument[] arguments = defmodel.getArguments();
		
		HashMap<String,String> staticFieldContents = getStaticFieldContents(RootComponentConfiguration.class);
		
		for(IArgument iArgument : arguments)
		{
			assertTrue("RootComponentConfiguration should contain parameter: " + iArgument.getName(),staticFieldContents.values().contains(iArgument.getName()));
		}
		
		for(String argument: staticFieldContents.values())
		{
			boolean contains = false;
			// those dont have to be in the agent model:
			if (!(argument.equals(RootComponentConfiguration.COMPONENT_FACTORY) 
				|| argument.equals(RootComponentConfiguration.PLATFORM_ACCESS)))
			{
					for(IArgument iArgument : arguments)
					{
						if (iArgument.getName().equals(argument)) {
							contains = true;
							continue;
						}
					}
					assertTrue("RootComponentConfiguration contains parameter that is not in platform model: " + argument, contains);
			}
		}
		
	}

	private HashMap<String, String> getStaticFieldContents(Class<RootComponentConfiguration> class1)
	{
		HashMap<String,String> hashMap = new HashMap<String, String>();
		Field[] fields = class1.getFields();
		for(Field field : fields)
		{
			if (Modifier.isStatic(field.getModifiers())) {
				if(String.class.isAssignableFrom(field.getType())) {
					try
					{
						hashMap.put(field.getName(), (String)field.get(null));
					}
					catch(IllegalArgumentException e)
					{
						e.printStackTrace();
					}
					catch(IllegalAccessException e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return hashMap;
	}
}
