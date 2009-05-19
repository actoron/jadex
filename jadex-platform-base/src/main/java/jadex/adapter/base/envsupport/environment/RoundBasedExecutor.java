package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;
import jadex.bridge.ITimedObject;

import java.util.Comparator;
import java.util.Iterator;

/**
 *  Synchronized execution of all actions in rounds based on clock ticks.
 */
public class RoundBasedExecutor
{
	//-------- constants --------
	
	/** Current time stamp */
	protected long timestamp;
	
	//-------- constructors--------
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 *  @param acomp	The action comparator.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space, IClockService clockservice, Comparator acomp)
	{
		this(space, clockservice);
		
		space.getAgentActionList().setOrdering(acomp);
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 */
	public RoundBasedExecutor(final AbstractEnvironmentSpace space, final IClockService clockservice)
	{
		this.timestamp = clockservice.getTime();
		
		// Start the processes.
		Object[] procs = space.getProcesses().toArray();
		for(int i = 0; i < procs.length; ++i)
		{
			ISpaceProcess process = (ISpaceProcess) procs[i];
			process.start(clockservice, space);
		}
		
		clockservice.createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				IVector1 progress = new Vector1Long(currenttime - timestamp);
				timestamp = currenttime;
				
				synchronized(space.getMonitor())
				{
					// Update the environment objects.
					for(Iterator it = space.getSpaceObjectsCollection().iterator(); it.hasNext(); )
					{
						SpaceObject obj = (SpaceObject)it.next();
						obj.updateObject(space, progress);
					}
					
					// Execute the scheduled agent actions.
					space.getAgentActionList().executeActions(null, false);
					
					// Execute the processes.
					Object[] procs = space.getProcesses().toArray();
					for(int i = 0; i < procs.length; ++i)
					{
						ISpaceProcess process = (ISpaceProcess) procs[i];
						process.execute(clockservice, space);
					}
					
					// Update the views.
					for (Iterator it = space.getViews().iterator(); it.hasNext(); )
					{
						IDataView view = (IDataView) it.next();
						view.update(space);
					}

					// Send the percepts to the agents.
					space.getPerceptList().processPercepts(null);

					// Wakeup the agents.
					space.getAgentActionList().wakeupAgents(null);
				}

				clockservice.createTickTimer(this);
//				System.out.println("-------------------------------------------");
			}
		});
	}
}
