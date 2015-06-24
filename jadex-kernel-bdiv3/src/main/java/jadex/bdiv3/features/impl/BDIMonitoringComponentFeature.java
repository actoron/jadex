package jadex.bdiv3.features.impl;

import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.runtime.impl.BeliefInfo;
import jadex.bdiv3.runtime.impl.GoalInfo;
import jadex.bdiv3.runtime.impl.PlanInfo;
import jadex.bdiv3.runtime.impl.RGoal;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.ComponentCreationInfo;
import jadex.bridge.component.impl.MonitoringComponentFeature;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.bridge.service.types.monitoring.MonitoringEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  Overrides the monitoring feature to add the logic for current state.
 */
public class BDIMonitoringComponentFeature extends MonitoringComponentFeature
{
	/**
	 *  Create the feature.
	 */
	public BDIMonitoringComponentFeature(IInternalAccess component, ComponentCreationInfo cinfo)
	{
		super(component, cinfo);
	}
	
	/**
	 *  Get the current state as events.
	 */
	public List<IMonitoringEvent> getCurrentStateEvents()
	{
		List<IMonitoringEvent> ret = super.getCurrentStateEvents();
		if(ret==null)
			ret = new ArrayList<IMonitoringEvent>();
		
		// Already gets merged beliefs (including subcapas).
		IBDIModel bdimodel = (IBDIModel)getComponent().getModel().getRawModel();
		List<MBelief> mbels = bdimodel.getCapability().getBeliefs();
		
		if(mbels!=null)
		{
			for(MBelief mbel: mbels)
			{
				BeliefInfo info = BeliefInfo.createBeliefInfo(getComponent(), mbel, getComponent().getClassLoader());
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_FACT, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(mbel.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		// Goals of this capability.
		Collection<RGoal> goals = getComponent().getComponentFeature(IInternalBDIAgentFeature.class).getCapability().getGoals();
		if(goals!=null)
		{
			for(RGoal goal: goals)
			{
				GoalInfo info = GoalInfo.createGoalInfo(goal);
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_GOAL, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(goal.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		// Plans of this capability.
		Collection<RPlan> plans	= getComponent().getComponentFeature(IInternalBDIAgentFeature.class).getCapability().getPlans();
		if(plans!=null)
		{
			for(RPlan plan: plans)
			{
				PlanInfo info = PlanInfo.createPlanInfo(plan);
				MonitoringEvent ev = new MonitoringEvent(getComponent().getComponentIdentifier(), getComponent().getComponentDescription().getCreationTime(), IMonitoringEvent.EVENT_TYPE_CREATION+"."+IMonitoringEvent.SOURCE_CATEGORY_PLAN, System.currentTimeMillis(), PublishEventLevel.FINE);
				ev.setSourceDescription(plan.toString());
				ev.setProperty("details", info);
				ret.add(ev);
			}
		}
		
		return ret;
	}
}
