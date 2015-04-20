package jadex.bdiv3.features.impl;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;
import jadex.micro.features.impl.MicroLifecycleComponentFeature;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class BDILifecycleAgentFeature extends MicroLifecycleComponentFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(ILifecycleComponentFeature.class, BDILifecycleAgentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null, false);
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDILifecycleAgentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
//		BDIAgentFeature bdif = (BDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
//		Object pojo = getComponent().getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//		bdif.injectAgent(getComponent(), pojo, bdif.getBDIModel(), null);
//		bdif.invokeInitCalls(pojo);
//		bdif.initCapabilities(pojo, bdif.getBDIModel().getSubcapabilities() , 0);
	}
	
//	/**
//	 *  Initialize the feature.
//	 *  Empty implementation that can be overridden.
//	 */
//	public IFuture<Void> init()
//	{
////		startBehavior();
//		return super.init();
//	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		BDIAgentFeature bdif = (BDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
		bdif.startBehavior();
		return super.body();
	}
}
