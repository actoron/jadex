package jadex.bdi.benchmarks;

import jadex.bdiv3x.runtime.Plan;


/**
 *  Plan for receiving messages.
 */
public class MessageReceiverPlan extends Plan
{
	/**
	 *  Create a new plan body.
	 */
	public void body()
	{
		int msgcnt = ((Integer)getBeliefbase().getBelief("msg_cnt").getFact()).intValue();
		int received = ((Integer)getBeliefbase().getBelief("received").getFact()).intValue();
		received++;
		getBeliefbase().getBelief("received").setFact(Integer.valueOf(received));
		
//		System.out.println("received: "+received);
		
		if(received==msgcnt)
		{
			long starttime = ((Long)getBeliefbase().getBelief("starttime").getFact()).longValue();
			long dur = getTime() - starttime;
			System.out.println("Sending/receiving " + msgcnt + " messages took: " + dur + " milliseconds.");
			killAgent();
		}
	}
}
