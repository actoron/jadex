package jadex.micro.features.impl;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.AgentChildKilled;

/**
 *  Overrides execution feature to implement childTerminated().
 */
public class MicroExecutionComponentFeature extends ExecutionComponentFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(IExecutionFeature.class, MicroExecutionComponentFeature.class, null, null);
	
	/**
	 *  Create the feature.
	 */
	public MicroExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Called when a child has been terminated.
	 */
	@Override
	public void childTerminated(IComponentDescription desc, Exception ex)
	{
		IFuture<Void> ret = MicroLifecycleComponentFeature.invokeMethod(getInternalAccess(), AgentChildKilled.class, new Object[]{desc, ex});
	}
}
