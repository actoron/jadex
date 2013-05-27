package jadex.android.clientappdemo.agent;

import jadex.android.clientappdemo.DefaultFragment;
import jadex.android.clientappdemo.PlatformService;
import jadex.bdi.runtime.Plan;
import android.widget.Toast;

/**
 *  Say Hello
 */
public class SayHelloPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		final String	message = (String)getBeliefbase().getBelief("HelloMessage").getFact();
		
		Object fact = getBeliefbase().getBelief("androidContext").getFact();
		
		if (fact instanceof DefaultFragment) {
			 final DefaultFragment act = (DefaultFragment)fact;
			 
			 act.runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						Toast.makeText(act.getActivity(), message, Toast.LENGTH_LONG).show();
					}
				});
				
			 
		} else if (fact instanceof PlatformService) {
			final PlatformService act = (PlatformService) fact;

			act.post(new Runnable()
				{
					
					@Override
					public void run()
					{
						Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
					}
				});
				
		}
		

	}
	
}

