package jadex.android.classloading.bditest;

import jadex.android.classloading.MyActivity;
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
		
		final MyActivity	act = (MyActivity)getBeliefbase().getBelief("androidContext").getFact();
		
		act.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				Toast.makeText(act.getActivity(), message, Toast.LENGTH_LONG).show();
			}
		});
		
	}
	
}

