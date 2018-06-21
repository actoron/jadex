package jadex.bdiv3x.features;

import java.util.Map;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDILifecycleFeature;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.IMessageEvent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.component.ILifecycleComponentFeature;
import jadex.bridge.component.IMessageFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.component.impl.ComponentFeatureFactory;
import jadex.bridge.component.impl.ComponentLifecycleFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
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
	
	/** Is the agent in shutdown?. */
	protected boolean shutdown;
	
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
	 *  Create the end behavior.
	 */
	protected EndBehavior createEndBehavior()
	{
		return new EndBehavior(component); 
	}
	
	/**
	 *  Start the end state.
	 */
	public IFuture<Void> shutdown()
	{
		setShutdown(true); 
		
		final Future<Void>	ret	= new Future<Void>();
		final IInternalBDIAgentFeature bdif = component.getComponentFeature(IInternalBDIAgentFeature.class);
		
//		System.out.println("shutdown start: "+component);
		createEndBehavior().startEndBehavior(bdif.getBDIModel(), bdif.getRuleSystem(), bdif.getCapability())
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
//				System.out.println("shutdown end: "+component);
				BDIXLifecycleAgentFeature.super.shutdown().addResultListener(new DelegationResultListener<Void>(ret));
			}

			public void exceptionOccurred(Exception exception)
			{
//				System.out.println("shutdown ex: "+component+", "+exception);
//				exception.printStackTrace();
				BDIXLifecycleAgentFeature.super.shutdown().addResultListener(new DelegationResultListener<Void>(ret));
			}
		});
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
	 *  Get the shutdown. 
	 *  @return The shutdown
	 */
	public boolean isShutdown()
	{
		return shutdown;
	}

	/**
	 *  Set the shutdown.
	 *  @param shutdown The shutdown to set
	 */
	public void setShutdown(boolean shutdown)
	{
		this.shutdown = shutdown;
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
		
		/**
		 *  Dispatch an internal event.
		 */
		public IFuture<Void> dispatchInternalEvent(IInternalEvent event)
		{
			IBDIXAgentFeature bdif = component.getComponentFeature(IBDIXAgentFeature.class);
			bdif.getEventbase().dispatchInternalEvent(event);
			return IFuture.DONE;
		}
	}
	
	/**
	 *  Extracted end behavior. 
	 */
	public static class EndBehavior extends jadex.bdiv3.features.impl.BDILifecycleAgentFeature.EndBehavior
	{
		/**
		 *  Create a new end behavior.
		 */
		public EndBehavior(IInternalAccess component)
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
		
		/**
//		 *  Dispatch a message event.
//		 */
//		public IFuture<Void> dispatchMessageEvent(IMessageEvent message)
//		{
//			IBDIMessageFeature mf = component.getComponentFeature(IMessageFeature.class);
//			return mf.sendMessage((Map<String, Object>)message.getMessage(), message.getMessageType());
//		}
		
		/**
		 *  Dispatch an internal event.
		 */
		public IFuture<Void> dispatchInternalEvent(IInternalEvent event)
		{
			IBDIXAgentFeature bdif = component.getComponentFeature(IBDIXAgentFeature.class);
			bdif.getEventbase().dispatchInternalEvent(event);
			return IFuture.DONE;
		}
	}
}