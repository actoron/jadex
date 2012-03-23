package jadex.bridge.service.types.factory;

import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.SReflect;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Helper methods for loading component models without a running platform.
 */
public class SBootstrapLoader
{
//	//-------- constants --------
//
//	/** The fallback platform configuration. */
//	public static final String FALLBACK_PLATFORM_CONFIGURATION = "jadex/standalone/Platform.component.xml";
//
//	/** The component factory to be used for platform component. */
//	public static final String FALLBACK_COMPONENT_FACTORY = "jadex.component.ComponentComponentFactory";
//
//	/** The configuration file. */
//	public static final String CONFIGURATION_FILE = "conf";
//	
//	/** The configuration name. */
//	public static final String CONFIGURATION_NAME = "configname";
//	
//	/** The platform name. */
//	public static final String PLATFORM_NAME = "platformname";
//
//	/** The component factory classname. */
//	public static final String COMPONENT_FACTORY = "componentfactory";
//	
//	/** The adapter factory classname. */
//	public static final String ADAPTER_FACTORY = "adapterfactory";
//	
//	/** The autoshutdown flag. */
//	public static final String AUTOSHUTDOWN = "autoshutdown";
//
//	/** The welcome flag. */
//	public static final String WELCOME = "welcome";
//
//	/** The component flag (for starting an additional component). */
//	public static final String COMPONENT = "component";
//	
//	/** The parameter copy flag. */
//	public static final String PARAMETERCOPY = "parametercopy";
//
//	
//	/** The reserved platform parameters. */
//	public static final Set<String> RESERVED;
//	
//	static
//	{
//		RESERVED = new HashSet<String>();
//		RESERVED.add(CONFIGURATION_FILE);
//		RESERVED.add(CONFIGURATION_NAME);
//		RESERVED.add(PLATFORM_NAME);
//		RESERVED.add(COMPONENT_FACTORY);
//		RESERVED.add(ADAPTER_FACTORY);
//		RESERVED.add(AUTOSHUTDOWN);
//		RESERVED.add(WELCOME);
//		RESERVED.add(COMPONENT);
//		RESERVED.add(PARAMETERCOPY);
//	}
	
	//-------- static methods --------
	
	/**
	 *  Load a component model.
	 */
	public static IFuture<IModelInfo>	loadModel(ClassLoader cl, String model, String factory)
	{
		Future<IModelInfo> ret = new Future<IModelInfo>();
		try
		{
			Class<IComponentFactory> cfclass = SReflect.findClass(factory, null, cl);
			// The providerid for this service is not important as it will be thrown away 
			// after loading the first component model.
			final IComponentFactory cfac = cfclass.getConstructor(new Class[]{String.class})
				.newInstance(new Object[]{"rootid"});
			
			cfac.loadModel(model, null, null)//rid)
				.addResultListener(new DelegationResultListener<IModelInfo>(ret)
			{
				public void customResultAvailable(IModelInfo model) 
				{
					if(model.getReport()!=null)
					{
						throw new RuntimeException("Error loading model:\n"+model.getReport().getErrorText());
					}
					else
					{
						super.customResultAvailable(model);
					}
				}
			});
		}
		catch(Exception e)
		{
			ret.setException(e);
		}		
		return ret;
	}
	
	/**
	 *  Get an argument expression string from the model.
	 */
	public static String	getArgumentString(String name, IModelInfo model, String configname)
	{
		ConfigurationInfo	config	= configname!=null
			? model.getConfiguration(configname) 
			: model.getConfigurations().length>0 ? model.getConfigurations()[0] : null;
		
		String	ret	= null;
		if(config!=null)
		{
			UnparsedExpression[]	upes	= config.getArguments();
			for(int i=0; ret==null && i<upes.length; i++)
			{
				if(name.equals(upes[i].getName()))
				{
					ret	= upes[i].getValue();
				}
			}
		}
		if(ret==null)
		{
			IArgument	arg	= model.getArgument(name);
			if(arg!=null)
			{
				Object	argval	= arg.getDefaultValue();
				ret	= argval instanceof UnparsedExpression
					? ((UnparsedExpression)argval).getValue() : argval!=null
					? ""+argval : "";
			}
		}
		return ret;
	}
}

