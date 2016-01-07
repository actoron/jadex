package jadex.bdi.benchmarks;

import java.util.Random;

import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;

/**
 *	Handle requests and generate reply value.
 */
public class RequestSenderPlan extends Plan
{
	/** Random number generator. */
	protected Random	rnd	= new Random();
	
	public void body()
	{
		int max	= ((Integer)getBeliefbase().getBelief("max").getFact()).intValue();
		IComponentIdentifier	receiver	= (IComponentIdentifier) getBeliefbase().getBelief("receiver").getFact();
		long	time	= System.currentTimeMillis();
		long	lasttime	= System.currentTimeMillis();

		for(int i=0; i<max || max==-1 ; i++)
		{
			if(i%50==0)
			{
				if(i==0)
				{
					System.out.println("Starting protocol "+(i+1));
				}
				else
				{
					long	newtime	= System.currentTimeMillis();
					System.out.println("Starting protocol "+(i+1)+", "+(newtime-lasttime));
					lasttime	= newtime;
				}
//				waitFor(1);
			}

				// Simple challenge response scheme allowing to check,
			// if the right request was answered.
			int	challenge	= rnd.nextInt(Integer.MAX_VALUE);

			IGoal request = createGoal("procap.rp_initiate");
			request.getParameter("action").setValue(Integer.valueOf(challenge));
			request.getParameter("receiver").setValue(receiver);
			dispatchSubgoalAndWait(request);
			Integer	response	= (Integer) request.getParameter("result").getValue();
			if(response.intValue()!=challenge+1)
				throw new RuntimeException("Invalid response "+challenge+", "+response);			
		}
		
		time	= System.currentTimeMillis() - time;
		System.out.println("Issued "+max+" protocols in "+time+" millis.");
		
		// Kill the receiver (i.e. the request performance master agent.
		IComponentManagementService	ces	= (IComponentManagementService)getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
		ces.destroyComponent(receiver);
	}
}
