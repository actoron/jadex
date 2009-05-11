package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;

import java.util.Iterator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Space executor that connects to a clock service and emits time deltas.
 */
public class DeltaTimeExecutor
{
	//-------- attributes --------
	
	/** Current time stamp */
	protected long timestamp;
	
	//-------- constructors--------
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor(final AbstractEnvironmentSpace space, final IClockService clockservice)
	{
		this.timestamp = clockservice.getTime();
		
		// Start the processes.
		Object[] procs = space.getProcesses().toArray();
		for(int i = 0; i < procs.length; ++i)
		{
			ISpaceProcess process = (ISpaceProcess) procs[i];
			process.start(clockservice, space);
		}

		clockservice.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{				
				long currenttime = clockservice.getTime();
				IVector1 progress = new Vector1Long(currenttime - timestamp);
				timestamp = currenttime;

//				System.out.println("step: "+timestamp+" "+progress);
	
				synchronized(space.getMonitor())
				{
					// Update the environment objects.
					for(Iterator it = space.getSpaceObjectsCollection().iterator(); it.hasNext(); )
					{
						SpaceObject obj = (SpaceObject)it.next();
						obj.updateObject(progress);
					}
					
					// Execute the scheduled agent actions.
					space.getAgentActionList().executeActions(null, true);
					
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
				}
			}
		});
	}
}
