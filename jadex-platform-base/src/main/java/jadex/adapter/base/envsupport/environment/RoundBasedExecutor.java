package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.bridge.IClockService;
import jadex.bridge.ITimedObject;
import jadex.commons.SimplePropertyObject;

import java.util.Comparator;
import java.util.Iterator;

/**
 *  Synchronized execution of all actions in rounds based on clock ticks.
 */
public class RoundBasedExecutor extends SimplePropertyObject implements ISpaceExecutor
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
	public RoundBasedExecutor()
	{
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 */
	public RoundBasedExecutor(final AbstractEnvironmentSpace space, final IClockService clockservice)
	{
		this(space, clockservice, null);
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 *  @param acomp	The action comparator.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space, IClockService clockservice, Comparator acomp)
	{
		setProperty("space", space);
		setProperty("clockservice", clockservice);
		setProperty("comparator", acomp);
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		final IClockService clockservice = (IClockService)getProperty("clockservice");
		Comparator comp = (Comparator)getProperty("comparator");
		if(comp!=null)
			space.getAgentActionList().setOrdering(comp);
		
		this.timestamp = clockservice.getTime();
		
		// Start the processes.
		Object[] procs = space.getProcesses().toArray();
		for(int i = 0; i < procs.length; ++i)
		{
			ISpaceProcess process = (ISpaceProcess) procs[i];
			process.start(clockservice, space);
		}
		
		// In the first round only percepts are distributed.
		final boolean[] first = new boolean[]{true};
		clockservice.createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				IVector1 progress = new Vector1Long(currenttime - timestamp);
				timestamp = currenttime;
				
				synchronized(space.getMonitor())
				{
					if(!first[0])
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
					}
					
					// Send the percepts to the agents.
					space.getPerceptList().processPercepts(null);

					// Wakeup the agents.
					space.getAgentActionList().wakeupAgents(null);
					
					first[0] = false;
				}

				clockservice.createTickTimer(this);
//				System.out.println("-------------------------------------------");
			}
		});
	}
}
