package jadex.bdiv3.examples.alarmclock;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.commons.SUtil;

/**
 *  Adapt the settings to the "alarms" beliefset by adding or removing alarms.
 */
@Plan
public class SyncSettingsAlarmsPlan
{
	//-------- attributes --------
	
	/** The scope. */
	@PlanCapability
	protected AlarmclockAgent	scope;
	
	//-------- methods ----------

	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	@PlanBody
	public void body()
	{
		Settings sets = scope.getSettings();
		Alarm[] sas = sets.getAlarms();
		Alarm[] bas = scope.getAlarms();

		// Remove obsolete alarms from the settings.
		for(int i=0; i<sas.length; i++)
		{
			if(!SUtil.arrayContains(bas, sas[i]))
				sets.removeAlarm(sas[i]);
		}

		// Add new alarms to the settings.
		for(int i=0; i<bas.length; i++)
		{
			if(!SUtil.arrayContains(sas, bas[i]))
				sets.addAlarm(bas[i]);
		}
	}
}
