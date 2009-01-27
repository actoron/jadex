package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.CurrentVision;
import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
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
			try 
			{
				System.out.println("Waiting for environemt to start ticker");
				waitForFactChanged("environment", 1000);
			}
			catch (TimeoutException te) {}
		}

		while(true)
		{
			boolean tick = ((Boolean) getBeliefbase().getBelief("tick").getFact()).booleanValue();
			
			if (!tick)
			{
				waitForFactChanged("tick");
			}
			else
			{
				
				// wait step time
				waitFor(((Long)getBeliefbase().getBelief("roundtime").getFact()).longValue());
				
				env.executeStep();
				
				// dispatch all step goals
				IGoal[] subgoals = env.getStepGoals();
				System.out.println("dispatching -"+subgoals.length+"- subgoals");
				for (int i = 0; i < subgoals.length; i++)
				{
					dispatchSubgoal(subgoals[i]);
				}

				// ensure all goals are finished
				boolean finished = false;
				while (!finished)
				{
					finished = true;
					
					for (int i = 0; i < subgoals.length; i++)
					{
						if (subgoals[i] != null)
						{
							if (!subgoals[i].isFinished())
							{
								System.out.println("subgoal -"+i+"- NOT FINISHED :" + subgoals[i].getLifecycleState());
								finished = false;
							}
							else
							{
								System.out.println("subgoal -"+i+"- finished");
								env.removeStepGoal(subgoals[i]);
								subgoals[i] = null;
							}
						}
					}
					
					if (!finished)
					{
						try 
						{ 
							waitFor(100); 
						} 
						catch (TimeoutException e ) 
						{
							// ignore
						}
					}
				}
				
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
	
	
	
}
