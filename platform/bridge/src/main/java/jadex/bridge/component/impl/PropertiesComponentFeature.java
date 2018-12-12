package jadex.bridge.component.impl;

import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IPropertiesFeature;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

/**
 *  This feature provides arguments.
 */
// Todo: remove!!! only used for logging level.
public class PropertiesComponentFeature	extends	AbstractComponentFeature implements IPropertiesFeature
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
		try
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
					
					Object tmp	= prop.getValue();
					if(tmp instanceof UnparsedExpression)
					{
						final UnparsedExpression unexp = (UnparsedExpression)tmp;
						Class<?> clazz = unexp.getClazz()!=null? unexp.getClazz().getType(getComponent().getClassLoader(), getComponent().getModel().getAllImports()): null;
						if(unexp.getValue()==null || unexp.getValue().length()==0 && clazz!=null)
						{
							tmp = clazz.newInstance();
						}
						else
						{
							tmp = SJavaParser.evaluateExpression(unexp.getValue(), getComponent().getModel().getAllImports(), getComponent().getFetcher(), getComponent().getClassLoader());
						}
					}
					properties.put(prop.getKey(), tmp);
				}
			}
			
			return IFuture.DONE;
		}
		catch(Exception e)
		{
			return new Future<Void>(e);
		}
	}
	
	/**
	 *  Check if the feature potentially executed user code in body.
	 *  Allows blocking operations in user bodies by using separate steps for each feature.
	 *  Non-user-body-features are directly executed for speed.
	 *  If unsure just return true. ;-)
	 */
	public boolean	hasUserBody()
	{
		return false;
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
