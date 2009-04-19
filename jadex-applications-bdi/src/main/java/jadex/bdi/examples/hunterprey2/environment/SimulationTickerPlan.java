package jadex.bdi.examples.hunterprey2.environment;

import jadex.adapter.base.fipa.SFipa;
import jadex.bdi.examples.hunterprey2.Creature;
import jadex.bdi.examples.hunterprey2.CurrentVision;
import jadex.bdi.examples.hunterprey2.Vision;
import jadex.bdi.planlib.simsupport.environment.SimulationEvent;
import jadex.bdi.runtime.IInternalEvent;
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
	// ----- attributes -------
	
	// The world environment
	private Environment env = null;

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

		// queue all simulation events
		getWaitqueue().addInternalEvent("simulation_event");
		
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

				// wait for all move task simulation events 
				IInternalEvent evt = null;
				while (env.getSimTaskCounter() > 0)
				{
					//System.out.println(this + " waiting for move tasks : " + env.getSimTaskCounter());
					do
					{
						evt = waitForInternalEvent("simulation_event");
					} while (!evt.getParameter("type").getValue().equals(SimulationEvent.DESTINATION_REACHED));
					
					env.updateSimTaskCounter(-1);
					//System.out.println(this + " one task finished ("+evt.getParameter("type").getValue()+"), waiting for move tasks : " + env.getSimTaskCounter());
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

				IInternalEvent event = createInternalEvent("tick_event");
				event.getParameter("round").setValue(new Integer(env.getWorldAge()));
				dispatchInternalEvent(event);
				
			}
		}
	}

}
