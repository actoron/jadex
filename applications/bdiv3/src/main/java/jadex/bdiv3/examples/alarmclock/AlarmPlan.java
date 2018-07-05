package jadex.bdiv3.examples.alarmclock;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.alarmclock.AlarmclockBDI.AlarmGoal;
import jadex.bdiv3.examples.alarmclock.AlarmclockBDI.NotifyGoal;
import jadex.bdiv3.runtime.ICapability;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;

/**
 *  The alarm plan has the purpose to wait for the
 *  next alarm time and issue the notification.
 */
@Plan
public class AlarmPlan
{
	//-------- attributes --------
	
	@PlanCapability
	protected ICapability	scope;
	
	@PlanAPI
	protected IPlan	plan;
	
	@PlanReason
	protected AlarmGoal	goal;
	
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	@PlanBody
	public void body()
	{
		while(true)
		{
			// Check if there is an alarm to do.
			long	time	= scope.getAgent().getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)).getTime();
			long alarmtime = goal.getAlarm().getAlarmtime(time);
//			System.out.println("Alarm plan alarmtime: "+alarmtime);
			if(alarmtime==Alarm.NO_ALARM)
			{
				scope.getAgent().getLogger().info("Alarmplan fails due to no alarm time: "+goal);
				throw new PlanFailureException();
			}
			
			// Wait until the alarm time has come.
			long wait = alarmtime-time;
			if(wait>0)
			{
				scope.getAgent().getLogger().info("Waiting for: "+wait/1000+" secs");
				scope.getAgent().getFeature(IExecutionFeature.class).waitForDelay(wait).get();
			}
			// Play the designated alarm song.
			if(wait>-1000) // todo: what is the limit?
			{
				//System.out.println("Notifying user.");
				try
				{
					plan.dispatchSubgoal(new NotifyGoal(goal.getAlarm())).get();
				}
				catch(GoalFailureException e)
				{
					scope.getAgent().getLogger().info("Could not play alarm for reason:"+e);
				}
			}

			// Avoid triggering more than once for the same alarm time.
			scope.getAgent().getFeature(IExecutionFeature.class).waitForDelay(1000).get();
			
			// Indicate that alarm has triggered.
			goal.getAlarm().triggerd();
		}
	}
}
