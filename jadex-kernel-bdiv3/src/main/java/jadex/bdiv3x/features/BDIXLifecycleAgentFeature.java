package jadex.bdiv3x.features;

import java.util.Collection;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDILifecycleFeature;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MInternalEvent;
import jadex.bdiv3.model.MMessageEvent;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3x.runtime.RInternalEvent;
import jadex.bdiv3x.runtime.RMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentLifecycleFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

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

		MCapability mcapa = (MCapability)bdif.getCapability().getModelElement();
		MConfiguration mconfig = mcapa.getConfiguration(getComponent().getConfiguration());

		if(mconfig!=null)
		{
			// Send initial messages
			// Throw initial internal events
			for(MConfigParameterElement cpe: SUtil.safeList(mconfig.getInitialEvents()))
			{
				MInternalEvent mievent = mcapa.getInternalEvent(cpe.getRef());
				if(mievent!=null)
				{
					RInternalEvent rievent = new RInternalEvent(mievent, getComponent(), cpe);
					bdif.getCapability().getEventbase().dispatchInternalEvent(rievent);
				}
				else
				{
					MMessageEvent mmevent = mcapa.getResolvedMessageEvent(null, cpe.getRef());
					RMessageEvent rmevent = new RMessageEvent(mmevent, getComponent(), cpe);
					bdif.getCapability().getEventbase().sendMessage(rmevent).addResultListener(new DefaultResultListener<Void>()
					{
						public void resultAvailable(Void result)
						{
						}
					});
				}
			}
		}
		
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
	 *  Start the end state.
	 */
	@Override
	public IFuture<Void> shutdown()
	{
		final Future<Void>	ret	= new Future<Void>();
		IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
		
		// Abort running plans.
		Collection<RPlan>	plans	= bdif.getCapability().getPlans();
		IResultListener<Void>	crl	= new CounterResultListener<Void>(plans.size(), true,
			new DelegationResultListener<Void>(ret)
		{
			@Override
			public void customResultAvailable(Void result)
			{
				// Todo: wait for end goals and end plans
				
				BDIXLifecycleAgentFeature.super.shutdown()
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
		for(RPlan plan: plans)
		{
			plan.abort().addResultListener(crl);
		}
		
		return ret;
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