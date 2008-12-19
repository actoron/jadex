package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.CurrentVision;
import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bridge.MessageFailureException;


/**
 *  The simulation ticker plan has the task to trigger
 *  the environment whenever a simulation step needs to be done.
 */
/*  @requires belief environment
 *  @requires belief roundtime
 */
public class SimulationTickerPlan extends Plan
{
	// The world environment
	Environment env = null;
	
	//-------- methods --------

	/**
	 *  The body method.
	 */
	public void body()
	{
		
		// wait for environment
		while (env == null)
		{
			env = (Environment)getBeliefbase().getBelief("environment").getFact();
			waitForFactChanged("environment", 1000);
			//waitFor(100);
		}

		while(true)
		{
			System.out.println("simticker - round");
			
			waitFor(((Long)getBeliefbase().getBelief("roundtime").getFact()).longValue());
			env.executeStep();
			
			//System.out.println("Actual tick cnt: "+getBeliefbase().getBelief("???").getFact("tickcnt"));

			// Dispatch new visions.
			Creature[]	creatures	= env.getCreatures();
			//System.out.println("Knows creatures: "+creatures.length);
			for(int i=0; i<creatures.length; i++)
			{
				//System.out.println("Sending to: "+creatures[i].getName()+" "+creatures[i].getAID());
				Vision	vision	= env.internalGetVision(creatures[i]);
				CurrentVision	cv	= new CurrentVision(creatures[i], vision);
				IMessageEvent mevent = createMessageEvent("inform_vision");
				mevent.getParameterSet(SFipa.RECEIVERS).addValue(creatures[i].getAID());
				mevent.getParameter(SFipa.CONTENT).setValue(cv);
				try
				{
					sendMessage(mevent);
				}
				catch(MessageFailureException e)
				{
					env.removeCreature(creatures[i]);
				}
			}
		}
	}
	
	
	
}
