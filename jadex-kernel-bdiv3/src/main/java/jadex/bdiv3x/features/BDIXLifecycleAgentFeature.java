package jadex.bdiv3x.features;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDILifecycleFeature;
import jadex.bdiv3.runtime.IGoal;
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
 *  Feature that ensures the bdi behavior is started.
 *  
 *  Differs from pojo BDILifecycleAgentFeature by extending ComponentLifecycleFeature.
 */
public class BDIXLifecycleAgentFeature extends ComponentLifecycleFeature implements IInternalBDILifecycleFeature
{
	/** The factory. */
	public static final IComponentFeatureFactory FACTORY = new ComponentFeatureFactory(ILifecycleComponentFeature.class, BDIXLifecycleAgentFeature.class,
		new Class<?>[]{IRequiredServicesFeature.class, IProvidedServicesFeature.class, ISubcomponentsFeature.class}, null, false);
	
	/** Is the agent inited and allowed to execute rules? */
	protected boolean inited;
	
	/**
	 *  Factory method constructor for instance level.
	 */
	public BDIXLifecycleAgentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Execute the functional body of the agent.
	 *  Is only called once.
	 */
	public IFuture<Void> body()
	{
		IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
		createStartBehavior().startBehavior(bdif.getBDIModel(), bdif.getRuleSystem(), bdif.getCapability());
//		inited	= true;
		return super.body();
	}
	
	/**
	 *  Create the start behavior.
	 */
	protected StartBehavior createStartBehavior()
	{
		return new StartBehavior(component);
	}
	
	/**
	 *  Get the inited.
	 *  @return The inited.
	 */
	public boolean isInited()
	{
		return inited;
	}
	
	/**
	 *  The inited to set.
	 *  @param inited The inited to set
	 */
	public void setInited(boolean inited)
	{
//		System.out.println("inited: "+getComponent().getComponentIdentifier());
		this.inited = inited;
	}
	
	/**
	 *  Extracted start behavior. 
	 */
	public static class StartBehavior extends jadex.bdiv3.features.impl.BDILifecycleAgentFeature.StartBehavior
	{
		/**
		 *  Create a new start behavior.
		 */
		public StartBehavior(IInternalAccess component)
		{
			super(component);
		}
		
		/**
		 *  Get the capability object (only for pojo).
		 */
		public Object getCapabilityObject(String name)
		{
			return null;
		}
		
		/**
		 *  Dispatch a top level goal.
		 */
		public IFuture<Object> dispatchTopLevelGoal(Object goal)
		{
			IBDIXAgentFeature bdif = component.getComponentFeature(IBDIXAgentFeature.class);
			return bdif.getGoalbase().dispatchTopLevelGoal((IGoal)goal);
		}
	}
}