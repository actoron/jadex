package jadex.platform;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import jadex.base.IPlatformConfiguration;
import jadex.base.IRootComponentConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.commons.SReflect;

public class PlatformConfigurationTest
{
	@Test
	public void testParametersEquivalence() throws ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
//		PlatformConfiguration config = new PlatformConfiguration();
//		RootComponentConfiguration rootConfig = config.getRootConfig();
//		config.setPlatformName("testcases_*");
//		rootConfig.setGui(false);
//		rootConfig.setSaveOnExit(false);
//		rootConfig.setWelcome(false);
//		config.setAutoShutdown(false);
//		rootConfig.setPrintPass(false);
//		long timeout = Starter.getLocalDefaultTimeout(null);
		
		
		// only load model
		Class<?> cfclass = SReflect.classForName(IPlatformConfiguration.FALLBACK_COMPONENT_FACTORY, this.getClass().getClassLoader());
		final IComponentFactory cfac = (IComponentFactory)cfclass.getConstructor(new Class[]{String.class})
			.newInstance(new Object[]{"rootid"});
		final IModelInfo defmodel	= cfac.loadModel(IPlatformConfiguration.FALLBACK_PLATFORM_CONFIGURATION, null, null).get();	// No execution yet, can only work if method is synchronous.
		
		// start whole platfrom
//		IExternalAccess	platform = (IExternalAccess)Starter.createPlatform(config).get(timeout);
//		IModelInfo defmodel = platform.getModel();
		
//		RootComponentConfiguration defaultRootConfig = PlatformConfiguration.getDefault().getRootConfig();
		
		IArgument[] arguments = defmodel.getArguments();
		
		HashMap<String,Field> staticFieldContents = getStaticFieldContents(IRootComponentConfiguration.class);
		Map<String, Method> setters = getSettersByName(IRootComponentConfiguration.class, staticFieldContents.keySet());
		
		for(IArgument argument : arguments)
		{
			String name = argument.getName();
			// those don't have to be in the root component configuration
			if (!(name.equals(IPlatformConfiguration.PLATFORM_NAME)
				|| name.equals(IPlatformConfiguration.AUTOSHUTDOWN)
				|| name.equals(IPlatformConfiguration.CONFIGURATION_NAME)
				|| name.equals(IPlatformConfiguration.PLATFORM_COMPONENT))) {
				Field field = staticFieldContents.get(name);
				assertTrue("RootComponentConfiguration should contain parameter: " + name,field != null);
				
				// this parameter does not have a getter
				if (!name.equals(IRootComponentConfiguration.PROGRAM_ARGUMENTS)) {
					String prettyName = makePrettyName(name);
					Method method = setters.get(prettyName);
					assertNotNull("No setter for: " + prettyName, method);
					Class< ? > setterParamType = method.getParameterTypes()[0];
					Class< ? > modelParamType = argument.getClazz().getType(this.getClass().getClassLoader());
					if (SReflect.isBasicType(setterParamType) && !SReflect.isBasicType(modelParamType)) {
						setterParamType = SReflect.getWrappedType(setterParamType);
					}
					// this parameter has another parameter type in config object
					if (!(name.equals(IRootComponentConfiguration.KERNELS)
						|| name.equals(IRootComponentConfiguration.AWAMECHANISMS))) {
						assertEquals("Field " + name + " has not the same type.", modelParamType, setterParamType);
					}
					
//					Object defValue = defaultRootConfig.getValue(name);
//					IParsedExpression parseExpression = SJavaParser.parseExpression(argument.getDefaultValue(), null, null);
//					System.out.println(parseExpression);
//					assertTrue(name, parseExpression.equals(defValue));
				}
				
			}
		}
		
		for(String argument: staticFieldContents.keySet())
		{
			boolean contains = false;
			// those dont have to be in the agent model
			if (!(argument.equals(IRootComponentConfiguration.COMPONENT_FACTORY) 
				|| argument.equals(IRootComponentConfiguration.PLATFORM_ACCESS)))
			{
					for(IArgument iArgument : arguments)
					{
						if (iArgument.getName().equals(argument)) {
							contains = true;
							continue;
						}
					}
					if(!contains)
					assertTrue("RootComponentConfiguration contains parameter that is not in platform model: " + argument, contains);
			}
		}
	}
	
	@Test
	public void testMinimalPlatform() 
	{
		IPlatformConfiguration minimal = PlatformConfigurationHandler.getMinimal();
		minimal.setRelayTransport(false);
		minimal.setWsTransport(false);
		Starter.createPlatform(minimal).get();
	}

	private Map<String,Method> getSettersByName(Class<IRootComponentConfiguration> class1, Set<String> names)
	{
		HashSet<String> myNames = new HashSet<String>();
		for(String string : names)
		{
			myNames.add(makePrettyName(string));
		}
//		System.out.println(myNames);
		HashMap<String,Method> hashMap = new HashMap<String, Method>();
		Method[] methods = class1.getMethods();
		
		for(Method method : methods)
		{
			String name = method.getName();
			if (name.startsWith("set")) {
				name = name.substring(3);
				String prettyName = makePrettyName(name);
				if (myNames.contains(prettyName)) {
					hashMap.put(prettyName, method);
				}
			}
		}
		return hashMap;
	}

	private String makePrettyName(String name)
	{
		return name.toLowerCase().replace("_", "");
	}

	private HashMap<String, Field> getStaticFieldContents(Class<IRootComponentConfiguration> class1)
	{
		HashMap<String,Field> hashMap = new HashMap<String, Field>();
		Field[] fields = class1.getFields();
		for(Field field : fields)
		{
			if (!field.getName().startsWith("AWAMECHANISM_")
					&& !field.getName().startsWith("KERNEL_")
					&& Modifier.isStatic(field.getModifiers())) {
				if(String.class.isAssignableFrom(field.getType())) {
					try
					{
						hashMap.put((String)field.get(null), field);
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
