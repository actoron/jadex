package jadex.adapter.jade;

import jade.core.behaviours.CyclicBehaviour;
import jadex.bridge.IKernelAgent;
import jadex.bridge.ITimer;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *  The jadex Timing Behaviour is responsible for
 *  waiting specified amounts of time and then waking
 *  up the next timeouted behaviour.
 */
public class TimingBehaviour extends CyclicBehaviour
{
	//-------- attributes --------

	/** The agent. */
	protected IKernelAgent agent;

	/** The clock. */
//	protected JadeAgentClock clock;

	//-------- constructor --------

	/**
	 *  Create a new TimingBehaviour.
	 *  @param agent The jadex agent.
	 * /
	public TimingBehaviour(IJadexAgent agent, JadeAgentClock clock)
	{
		this.agent	= agent;
		this.clock	= clock;
		clock.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				System.out.println("restarting timing");
				TimingBehaviour.this.restart();
			}
		});
	}*/

	//-------- methods --------

	/**
	 *  Wait till the next timeout.
	 */
	public void action()
	{
	}
	/*	// Block when timetable is empty.
		ITimer	next = clock.getNextTimer();
		if(next==null)
		{
			System.out.println("timing sleeping");
			block();
		}
			
		else
		{
			// Get next entry from timetable.
			long	diff = next.getNotificationTime() - System.currentTimeMillis();

			// Wait until next entry is due.
			if(diff>0)
			{
				System.out.println("timing waiting for "+diff+" milliseconds.");
				block(diff);
			}
			
			// Handle due entry.
			else
			{
				System.out.println("timing notifying next entry: "+next);
				clock.notifyNext();
			}
		}
	}*/
}

