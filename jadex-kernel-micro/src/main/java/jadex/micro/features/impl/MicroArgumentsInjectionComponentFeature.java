package jadex.micro.features.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;
import jadex.javaparser.SimpleValueFetcher;
import jadex.micro.MicroModel;
import jadex.micro.features.IMicroArgumentsInjectionFeature;
import jadex.micro.features.IMicroLifecycleFeature;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *  Inject agent arguments into annotated field values.
 */
public class MicroArgumentsInjectionComponentFeature extends	AbstractComponentFeature
{
	//-------- type level --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public MicroArgumentsInjectionComponentFeature()
	{
	}
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IMicroArgumentsInjectionFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 *  @param access	The access of the component.
	 *  @param info	The creation info.
	 */
	public IComponentFeature	createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new MicroArgumentsInjectionComponentFeature(access, info);
	}
	
	//-------- instance level --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroArgumentsInjectionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}

	/**
	 *  Initialize the feature.
	 *  Empty implementation that can be overridden.
	 */
	public IFuture<Void> init()
	{
		Future<Void> ret = new Future<Void>();
		
		Map<String, Object>	args	= getComponent().getComponentFeature(IArgumentsFeature.class).getArguments();
		Map<String, Object>	results	= getComponent().getComponentFeature(IArgumentsFeature.class).getResults();
		MicroModel	model	= (MicroModel)getComponent().getModel().getRawModel();

		if(args!=null)
		{
			String[] names = model.getArgumentInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					Object val = args.get(names[i]);
					
//					if(val!=null || getModel().getArgument(names[i]).getDefaultValue()!=null)
					final Tuple2<FieldInfo, String>[] infos = model.getArgumentInjections(names[i]);
					
					try
					{
						for(int j=0; j<infos.length; j++)
						{
							Field field = infos[j].getFirstEntity().getField(getComponent().getClassLoader());
							String convert = infos[j].getSecondEntity();
//							System.out.println("seting arg: "+names[i]+" "+val);
							setFieldValue(val, field, convert);
						}
					}
					catch(Exception e)
					{
						getComponent().getLogger().warning("Field injection failed: "+e);
						if(!ret.isDone())
							ret.setException(e);
					}
				}
			}
		}
		
		// Inject default result values
		if(results!=null)
		{
			String[] names = model.getResultInjectionNames();
			if(names.length>0)
			{
				for(int i=0; i<names.length; i++)
				{
					if(results.containsKey(names[i]))
					{
						Object val = results.get(names[i]);
						final Tuple3<FieldInfo, String, String> info = model.getResultInjection(names[i]);
						
						try
						{
							Field field = info.getFirstEntity().getField(getComponent().getClassLoader());
							String convert = info.getSecondEntity();
//							System.out.println("seting res: "+names[i]+" "+val);
							setFieldValue(val, field, convert);
						}
						catch(Exception e)
						{
							getComponent().getLogger().warning("Field injection failed: "+e);
							if(!ret.isDone())
								ret.setException(e);
						}
					}
				}
			}
		}
		
		if(!ret.isDone())
			ret.setResult(null);
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Set an injected field value.
	 */
	protected void setFieldValue(Object val, Field field, String convert)
	{
		if(val!=null || !SReflect.isBasicType(field.getType()))
		{
			try
			{
				Object agent = getComponent().getComponentFeature(IMicroLifecycleFeature.class).getPojoAgent();
				if(convert!=null)
				{
					SimpleValueFetcher fetcher = new SimpleValueFetcher(getComponent().getFetcher());
					fetcher.setValue("$value", val);
					val = SJavaParser.evaluateExpression(convert, getComponent().getModel().getAllImports(), fetcher, getComponent().getClassLoader());
				}
				field.setAccessible(true);
				field.set(agent, val);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
	}
}
