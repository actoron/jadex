package jadex.bdiv3x.features;

import jadex.bdiv3.features.impl.IInternalBDILifecycleFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentLifecycleFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IFuture;

/**
 *  Feature that ensures the agent created(), body() and killed() are called on the pojo. 
 */
public class BDILifecycleAgentFeature extends ComponentLifecycleFeature implements IInternalBDILifecycleFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(ILifecycleComponentFeature.class, BDILifecycleAgentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null, false);
	
	/** Is the agent inited and allowed to execute rules? */
	protected boolean inited;
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDILifecycleAgentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
		
//		BDIAgentFeature bdif = (IInternalBDIAgentFeature)component.getComponentFeature(IBDIAgentFeature.class);
//		Object pojo = component.getComponentFeature(IPojoComponentFeature.class).getPojoAgent();
//		bdif.injectAgent(component, pojo, bdif.getBDIModel(), null);
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
		jadex.bdiv3.features.impl.BDILifecycleAgentFeature.startBehavior(getComponent());
		inited	= true;
		return super.body();
	}
	
	/**
	 *  Get the inited.
	 *  @return The inited.
	 */
	public boolean isInited()
	{
		return inited;
	}
}