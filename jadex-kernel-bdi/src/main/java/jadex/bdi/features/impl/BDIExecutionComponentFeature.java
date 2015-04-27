package jadex.bdi.features.impl;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishTarget;
import jadex.bridge.service.types.monitoring.MonitoringEvent;
import jadex.rules.rulesystem.Activation;
import jadex.rules.state.IProfiler;

/**
 *  BDI execution feature adds rule engine behavior to the cycle.
 */
public class BDIExecutionComponentFeature extends ExecutionComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public BDIExecutionComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Components with autonomous behavior may override this method
	 *  to implement a recurring execution cycle.
	 *  @return true, if the execution should continue, false, if the component may become idle. 
	 */
	protected boolean executeCycle()
	{
		assert isComponentThread();
		
		// Evaluate conditions in addition to executing steps.
		boolean	again = false;
		IInternalBDIAgentFeature bdif = (BDIAgentFeature)getComponent().getComponentFeature(IBDIAgentFeature.class);
		
		try
		{
			// check st!=null is not enough as agent could be terminated during init.
			// In this case the rulesystem may never has been inited.
//			String st = (String)state.getAttributeValue(ragent, OAVBDIRuntimeModel.agent_has_state);
			if(bdif.getRuleSystem().isInited())
			{
				// Hack!!! platform should inform about ext entries to update agenda.
				Activation	act	= bdif.getRuleSystem().getAgenda().getLastActivation();
				bdif.getState().getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
				bdif.getState().expungeStaleObjects();
				bdif.getState().notifyEventListeners();
				bdif.getState().getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
	
				if(!bdif.getRuleSystem().getAgenda().isEmpty())
				{					
//					notifyListeners(new ComponentChangeEvent(IComponentChangeEvent.EVENT_TYPE_CREATION,
//						IComponentChangeEvent.SOURCE_CATEGORY_EXECUTION, null, null, getComponentIdentifier(), getComponentDescription().getCreationTime(), null));
					
					if(getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
					{
						getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
					}
					
					bdif.getRuleSystem().getAgenda().fireRule();
					act	= bdif.getRuleSystem().getAgenda().getLastActivation();
//					System.out.println("here: "+getComponentIdentifier()+" "+act+", "+rulesystem.getAgenda().getActivations());
					bdif.getState().getProfiler().start(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
					bdif.getState().expungeStaleObjects();
					bdif.getState().notifyEventListeners();
					bdif.getState().getProfiler().stop(IProfiler.TYPE_RULE, act!=null?act.getRule():null);
					
					if(getComponent().getComponentFeature(IMonitoringComponentFeature.class).hasEventTargets(PublishTarget.TOALL, PublishEventLevel.FINE))
					{
						getComponent().getComponentFeature(IMonitoringComponentFeature.class).publishEvent(new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_DISPOSAL+"."+IMonitoringEvent.SOURCE_CATEGORY_EXECUTION, System.currentTimeMillis(), PublishEventLevel.FINE), PublishTarget.TOALL);
					}
				}
	
				again = !bdif.getRuleSystem().getAgenda().isEmpty();
			}
		}
		catch(Throwable e)
		{
			// Catch fatal error and cleanup before propagating error to platform.
//				cleanup();
			if(e instanceof RuntimeException)
				throw (RuntimeException)e;
			else if(e instanceof Error)
				throw (Error)e;
			else // Shouldn't happen!? 
				throw new RuntimeException(e);
		}
		
		return again;
	}
}