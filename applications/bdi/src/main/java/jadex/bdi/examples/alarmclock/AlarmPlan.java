package jadex.bdi.examples.alarmclock;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3.runtime.impl.GoalFailureException;
import jadex.bdiv3x.runtime.Plan;

/**
 *  The alarm plan has the purpose to wait for the
 *  next alarm time and issue the notification.
 */
public class AlarmPlan extends Plan
{
	//-------- methods --------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		while(true)
		{
			// Check if there is an alarm to do.
			Alarm alarm = (Alarm)getParameter("alarm").getValue();
//			System.out.println("Alarmplan got alarm: "+alarm);
			
			long alarmtime = alarm.getAlarmtime(getTime());
//			System.out.println("Alarm plan alarmtime: "+alarmtime);
			if(alarmtime==Alarm.NO_ALARM)
			{
				getLogger().info("Alarmplan fails due to no alarm time: "+getReason());
				fail();
			}
			
			// Wait until the alarm time has come.
			long wait = alarmtime-getTime();
			if(wait>0)
			{
				getLogger().info("Waiting for: "+wait/1000+" secs");
				waitFor(wait);
			}
			// Play the designated alarm song.
			if(wait>-1000) // todo: what is the limit?
			{
				//System.out.println("Notifying user.");
				IGoal notify = createGoal("notify");
				notify.getParameter("alarm").setValue(alarm);
				try
				{
					dispatchSubgoalAndWait(notify);
				}
				catch(GoalFailureException e)
				{
					getLogger().info("Could not play alarm for reason:"+e);
				}
			}

			// Avoid triggering more than once for the same alarm time.
			waitFor(1000);
			// Indicate that alarm has triggered.
			alarm.triggerd();
		}
	}
}
