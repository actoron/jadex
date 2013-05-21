package jadex.android.clientapp.bditest;

import jadex.android.clientapp.MyActivity;
import jadex.android.clientapp.MyPlatformService;
import jadex.android.clientapp.MyService;
import jadex.bdi.runtime.Plan;
import android.content.Context;
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
		
		if (fact instanceof MyActivity) {
			 final MyActivity act = (MyActivity)fact;
			 
			 act.runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						Toast.makeText(act.getActivity(), message, Toast.LENGTH_LONG).show();
					}
				});
				
			 
		} else if (fact instanceof MyPlatformService){
			final MyPlatformService act = (MyPlatformService) fact;

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

