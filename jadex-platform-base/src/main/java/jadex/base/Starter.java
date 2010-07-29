package jadex.base;

import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.ComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.IFuture;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *  Starter class for  
 */
public class Starter
{
	//-------- constants --------

	/** The fallback platform configuration. */
	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/standalone/Platform.application.xml";

	/** The component factory to be used for platform component. */
	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.application.ApplicationComponentFactory";

	/** The component factory to be used for platform adapter. */
	public static final String FALLBACK_ADAPTER_FACTORY = "jadex.standalone.ComponentAdapterFactory";

	
	/** The configuration file. */
	public static final String CONFIGURATION_FILE = "conf";
	
	/** The configuration name. */
	public static final String CONFIGURATION_NAME = "configname";
	
	/** The platform name. */
	public static final String PLATFORM_NAME = "platformname";

	/** The component factory classname. */
	public static final String COMPONENT_FACTORY = "componentfactory";
	
	/** The adapter factory classname. */
	public static final String ADAPTER_FACTORY = "adapterfactory";

	
	/** The reserved platform parameters. */
	public static final Set RESERVED;
	
	static
	{
		RESERVED = new HashSet();
		RESERVED.add(CONFIGURATION_FILE);
		RESERVED.add(CONFIGURATION_NAME);
		RESERVED.add(PLATFORM_NAME);
		RESERVED.add(COMPONENT_FACTORY);
		RESERVED.add(ADAPTER_FACTORY);
	}
	
	//-------- static methods --------
	
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args)
	{
		createPlatform(args);
	}
	
	/**
	 *  Create the platform.
	 *  @param args The command line arguments.
	 *  @return The external access of the root component.
	 */
	public static IFuture createPlatform(String[] args)
	{
		try
		{
			// Absolute start time (for testing and benchmarking).
			long starttime = System.currentTimeMillis();
		
			Map cmdargs = new HashMap();
			Map compargs = new HashMap();
			for(int i=0; i<args.length; i+=2)
			{
				String key = args[i].substring(1);
				Object val = args[i+1];
				if(!RESERVED.contains(key))
				{
					try
					{
						val = SJavaParser.evaluateExpression(args[i+1], null);
						compargs.put(key, val);
					}
					catch(Exception e)
					{
						System.out.println("Argument parse exception using as string: "+args[i]+"="+args[i+1]);
					}
				}
				cmdargs.put(key, val);
			}
			
			// Load the platform (component) model.
			ClassLoader cl = Starter.class.getClassLoader();
			String configfile = (String)cmdargs.get(CONFIGURATION_FILE)!=null? 
				(String)cmdargs.get(CONFIGURATION_FILE): FALLBACK_PLATFORM_CONFIGURATION;
			String cfclname = (String)cmdargs.get(COMPONENT_FACTORY)!=null? 
				(String)cmdargs.get(COMPONENT_FACTORY): FALLBACK_COMPONENT_FACTORY;
			Class cfclass = SReflect.findClass(cfclname, null, cl);
			// The providerid for this service is not important as it will be thrown away 
			// after loading the first component model.
			IComponentFactory cfac = (IComponentFactory)cfclass.getConstructor(new Class[]{Object.class})
				.newInstance(new Object[]{"rootid"});
			ILoadableComponentModel model = cfac.loadModel(configfile, null, cl);
	//		System.out.println("Model: "+model);
			
			// Create an instance of the component.
			String platformname = (String)cmdargs.get(PLATFORM_NAME);
			if(platformname==null)
			{
				IArgument[] cargs = model.getArguments();
				for(int i=0; i<cargs.length; i++)
				{
					if("platformname".equals(cargs[i].getName()))
					{
						platformname = (String)cargs[i].getDefaultValue((String)cmdargs.get("-configname"));
					}
				}
			}
			if(platformname==null)
			{
				platformname = "platform";
			}
			
			IComponentIdentifier cid = new ComponentIdentifier(platformname);
			CMSComponentDescription desc = new CMSComponentDescription(cid, null, null, false, false);
			
			String afclname = (String)cmdargs.get(ADAPTER_FACTORY)!=null? 
				(String)cmdargs.get(ADAPTER_FACTORY): FALLBACK_ADAPTER_FACTORY;
			Class afclass = SReflect.findClass(afclname, null, cl);
			IComponentAdapterFactory afac = (IComponentAdapterFactory)afclass.newInstance();
			Object[] root = cfac.createComponentInstance(desc, afac, model, (String)cmdargs.get(CONFIGURATION_NAME), compargs, null);
			IComponentInstance instance = (IComponentInstance)root[0];
			IComponentAdapter adapter = (IComponentAdapter)root[1];
	//		System.out.println("Instance: "+instance);
			
			// Initiate first step of root component (i.e. platform).
			afac.executeStep(adapter);
			
			long startup = System.currentTimeMillis() - starttime;
			System.out.println("Platform startup time: " + startup + " ms.");
	//		platform.logger.info("Platform startup time: " + startup + " ms.");
			
			return instance.getExternalAccess();
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}

