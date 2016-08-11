package jadex.bdi.testcases.semiautomatic;

import java.util.Calendar;
import java.util.Date;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan to wake up at every full minute.
 */
public class WakeupPlan extends Plan
{
	public void body()
	{
		// Get date of full minute.
		Calendar now = Calendar.getInstance();
		Calendar wakeup = Calendar.getInstance();
		wakeup.clear();
		wakeup.set(now.get(Calendar.YEAR), now.get(Calendar.MONTH), now.get(Calendar.DATE),
			now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), 0);
		if(wakeup.before(now))
		{
			wakeup.add(Calendar.MINUTE, 1);
		}

		while(true)
		{
			// Wait until next full minute
			long delay = wakeup.getTime().getTime() - getTime();
//			System.out.println("Waiting for: "+delay+" ms");
			waitFor(wakeup.getTime().getTime() - getTime());

			// Do the action...
			getLogger().info("Time is now: "+new Date(getTime()));
//			System.out.println("Time is now: "+new Date(getTime()));

			// Increment wakeup time one day.
			wakeup.add(Calendar.MINUTE, 1);
		}
	}
}
