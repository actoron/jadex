package jadex.bdi.examples.alarmclock;

import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Adapt the settings to the "alarms" beliefset by adding or removing alarms.
 */
public class SyncSettingsAlarmsPlan extends Plan
{
	//-------- methods ----------

	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		Settings sets = (Settings)getScope().getBeliefbase().getBelief("settings").getFact();
		Alarm[] sas = sets.getAlarms();
		Alarm[] bas = (Alarm[])getScope().getBeliefbase().getBeliefSet("alarms").getFacts();

		// Remove obsolete alarms from the setztings.
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
