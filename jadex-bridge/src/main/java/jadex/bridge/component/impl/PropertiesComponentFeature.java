package jadex.bridge.component.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IPropertiesFeature;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  This feature provides arguments.
 */
public class PropertiesComponentFeature	extends	AbstractComponentFeature	implements IPropertiesFeature
{
	//-------- constants ---------
	
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(
		IPropertiesFeature.class, PropertiesComponentFeature.class);
	
	//-------- attributes --------
	
	/** The properties. */
	protected Map<String, Object>	properties;
	
	//-------- constructors --------
	
	/**
	 *  Create the feature.
	 */
	public PropertiesComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Initialize the feature.
	 */
	public IFuture<Void> init()
	{
		// Todo: runtime-supplied properties?
//		if(cinfo.getArguments()!=null)
//		{
//			for(Iterator<Map.Entry<String, Object>> it=cinfo.getArguments().entrySet().iterator(); it.hasNext(); )
//			{
//				Map.Entry<String, Object> entry = it.next();
//				if(arguments==null)
//				{
//					this.arguments	= new LinkedHashMap<String, Object>();
//				}
//				arguments.put(entry.getKey(), entry.getValue());
//			}
//		}
		
		// Todo: configuration properties?
//		ConfigurationInfo	ci	= cinfo.getConfiguration()!=null ? component.getModel().getConfiguration(cinfo.getConfiguration()) : null;
//		if(ci!=null)
//		{
//			UnparsedExpression[]	upes	= ci.getArguments();
//			for(int i=0; i<upes.length; i++)
//			{
//				if(arguments==null || !arguments.containsKey(upes[i].getName()))
//				{
//					if(arguments==null)
//					{
//						this.arguments	= new LinkedHashMap<String, Object>();
//					}
//					arguments.put(upes[i].getName(), SJavaParser.getParsedValue(upes[i], component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
//				}
//			}
//		}
		
		
		for(Map.Entry<String, Object> prop: getComponent().getModel().getProperties().entrySet())
		{
			if((properties==null || !properties.containsKey(prop.getKey())))
			{
				if(properties==null)
				{
					this.properties	= new LinkedHashMap<String, Object>();
				}
				properties.put(prop.getKey(), SJavaParser.getParsedValue(prop.getValue(), component.getModel().getAllImports(), component.getFetcher(), component.getClassLoader()));
			}
		}
		
		return IFuture.DONE;
	}
	
	//-------- IArgumentsFeature interface --------
	
	/**
	 *  Get a property value.
	 *  @param name	The property name.
	 *  @return The property value (if any).
	 */
	public Object	getProperty(String name)
	{
		return properties!=null ? properties.get(name) : null;
	}
}
