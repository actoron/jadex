package jadex.android.clientappdemo.bdiagent;

import jadex.bdi.runtime.Plan;

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
		
//		Object fact = getBeliefbase().getBelief("androidContext").getFact();
		
//		if (fact instanceof PlatformProvidingFragment) {
//			 final PlatformProvidingFragment act = (PlatformProvidingFragment)fact;
//			 
//			 act.runOnUiThread(new Runnable()
//				{
//					
//					@Override
//					public void run()
//					{
//						Toast.makeText(act.getActivity(), message, Toast.LENGTH_LONG).show();
//					}
//				});
//				
//			 
//		} else if (fact instanceof PlatformService) {
//			final PlatformService act = (PlatformService) fact;
//
//			act.post(new Runnable()
//				{
//					
//					@Override
//					public void run()
//					{
//						Toast.makeText(act.getApplicationContext(), message, Toast.LENGTH_LONG).show();
//					}
//				});
//				
//		}
		

	}
	
}

