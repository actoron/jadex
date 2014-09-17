package jadex.micro.features.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IArgumentsFeature;
import jadex.bridge.component.IComponentFeature;
import jadex.bridge.component.impl.AbstractComponentFeature;
import jadex.commons.FieldInfo;
import jadex.commons.Tuple2;
import jadex.commons.Tuple3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroModel;
import jadex.micro.features.IMicroArgumentsInjectionFeature;
import jadex.micro.features.IMicroLifecycleFeature;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * 
 */
public class MicroLifecycleFeature extends	AbstractComponentFeature implements IMicroLifecycleFeature
{
	//-------- type level --------
	
	/**
	 *  Bean constructor for type level.
	 */
	public MicroLifecycleFeature()
	{
	}
	
	/**
	 *  Get the user interface type of the feature.
	 */
	public Class<?>	getType()
	{
		return IMicroLifecycleFeature.class;
	}
	
	/**
	 *  Create an instance of the feature.
	 *  @param access	The access of the component.
	 *  @param info	The creation info.
	 */
	public IComponentFeature	createInstance(IInternalAccess access, ComponentCreationInfo info)
	{
		return new MicroLifecycleFeature(access, info);
	}
	
	//-------- instance level --------
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public MicroLifecycleFeature(IInternalAccess component, ComponentCreationInfo cinfo)
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
//		
//		// Create the pojo agent
//		MicroModel model = getComponent().getModel().getRawModel();
//		model.get
//		
//		Map<String, Object>	args	= getComponent().getComponentFeature(IArgumentsFeature.class).getArguments();
//
//		
//		if(!ret.isDone())
//			ret.setResult(null);
		
		return ret;
	}
}
