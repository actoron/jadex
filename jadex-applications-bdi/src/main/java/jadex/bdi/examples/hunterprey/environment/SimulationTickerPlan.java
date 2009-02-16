package jadex.bdi.examples.hunterprey.environment;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey.Creature;
import jadex.bdi.examples.hunterprey.CurrentVision;
import jadex.bdi.examples.hunterprey.Vision;
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
	//-------- methods --------

	/**
	 *  The body method.
	 */
	public void body()
	{
		Environment env = (Environment)getBeliefbase().getBelief("environment").getFact();
		while(true)
		{
			waitFor(((Long)getBeliefbase().getBelief("roundtime").getFact()).longValue());
			
			synchronized (env)
			{
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
				
				// clear task list, so waiting plans can work
				env.clearTaskList();
			}
			
		}
	}
}
