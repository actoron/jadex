package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.CurrentVision;
import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IParameter;
import jadex.bdi.runtime.IParameterSet;
import jadex.bdi.runtime.Plan;
import jadex.bdi.runtime.TimeoutException;
import jadex.bridge.MessageFailureException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


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
	private Environment env = null;
	
	// HACK! max iterations for a goal
	private Integer maxIterations = new Integer(10);
	private Map iterationCounters = new HashMap();
	private List recreatedGoals = new ArrayList();
	
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
				//System.out.println("dispatching -"+subgoals.length+"- subgoals");
				for (int i = 0; i < subgoals.length; i++)
				{
					dispatchSubgoal(subgoals[i]);
				}
				
				// ensure all goals are finished
				waitForGoals(subgoals);
				
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

	/**
	 * Wait for all subgoals in array
	 * @param IGoal[] subgoals
	 */
	private void waitForGoals(IGoal[] subgoals)
	{
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
						//System.out.println("subgoal -"+i+"- NOT FINISHED :" + subgoals[i]);
						finished = false;
						
						// HACK! sometimes the goto goal doesn't return
						if (iterationCounters.containsKey(subgoals[i]))
						{
							Integer goalIterations = (Integer) iterationCounters.get(subgoals[i]);
							
							if (goalIterations.compareTo(maxIterations) > 0)
							{
								// is this goal already recreated and have max iterations?
								if (recreatedGoals.contains(subgoals[i]))
								{
									// drop goal finally
									System.err.println("Sorry, we have to finally drop a simengine sub goal! " + subgoals[i]);
									subgoals[i].drop();
									subgoals[i] = null;
								}
								else
								{
									IGoal newGoal = recreateGoal(subgoals[i]);
									subgoals[i].drop();
									dispatchSubgoal(newGoal);
									recreatedGoals.add(newGoal);
									env.removeStepGoal(subgoals[i]);
									subgoals[i] = newGoal;
									subgoals[i] = null;
								}
							}
							else
							{
								iterationCounters.put(subgoals[i], new Integer(goalIterations.intValue()+1));
							}
						}
						else
						{
							iterationCounters.put(subgoals[i], new Integer(1));
						}
					}
					else
					{
						//System.out.println("subgoal -"+i+"- finished");
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
		
		// clear 
		iterationCounters.clear();
		recreatedGoals.clear();
		
	}

	/**
	 * Create a clone of the given goal 
	 * @param goal
	 * @return
	 */
	private IGoal recreateGoal(IGoal goal)
	{
		IGoal newGoal = createGoal(goal.getType());
		IParameter[] param = goal.getParameters();
		for (int i = 0; i < param.length; i++)
		{
			IParameter p = param[i];
			newGoal.getParameter(p.getName()).setValue(p.getValue());
		}
		IParameterSet[] params = goal.getParameterSets();
		for (int i = 0; i < params.length; i++)
		{
			IParameterSet set = params[i];
			newGoal.getParameterSet(set.getName()).addValues(set.getValues());
		}
		
		return newGoal;
	}
	
	
	
}
