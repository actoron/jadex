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
 *  Feature that ensures the bdi behavior is started.
 *  
 *  Differs from pojo BDILifecycleAgentFeature by extending ComponentLifecycleFeature.
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
	}
	
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