package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.bridge.IPlatform;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IExecutable;
import jadex.service.clock.IClockService;
import jadex.service.clock.ITimedObject;
import jadex.service.execution.IExecutionService;

import java.util.Iterator;

/**
 * Space executor that connects to a clock service and reacts on time deltas.
 */
// Todo: immediate execution of agent actions and percepts?
public class DeltaTimeExecutor extends SimplePropertyObject implements ISpaceExecutor
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
	public DeltaTimeExecutor()
	{
	}
	
	/**
	 * Creates a new DeltaTimeExecutor
	 * @param timecoefficient the time coefficient
	 * @param clockservice the clock service
	 */
	public DeltaTimeExecutor(AbstractEnvironmentSpace space, boolean tick)
	{
		setProperty("space", space);
		setProperty("tick", new Boolean(tick));
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		final boolean tick = getProperty("tick")!=null && ((Boolean)getProperty("tick")).booleanValue();
		IPlatform	platform	= ((ApplicationContext)space.getContext()).getPlatform();
		final IClockService clockservice = (IClockService)platform.getService(IClockService.class);
		final IExecutionService exeservice = (IExecutionService)platform.getService(IExecutionService.class);
		
		final IExecutable	executable	= new IExecutable()
		{
			public boolean execute()
			{
				long currenttime = clockservice.getTime();
				long progress = currenttime - timestamp;
				timestamp = currenttime;

//				System.out.println("step: "+timestamp+" "+progress);
	
				synchronized(space.getMonitor())
				{
					// Update the environment objects.
					Object[]	objs	= space.getSpaceObjectsCollection().toArray();
					for(int i=0; i<objs.length; i++)
					{
						SpaceObject obj = (SpaceObject)objs[i];
						obj.updateObject(space, progress, clockservice);
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
				return false;
			}
		};
		
		this.timestamp = clockservice.getTime();
		
		// Start the processes.
		Object[] procs = space.getProcesses().toArray();
		for(int i = 0; i < procs.length; ++i)
		{
			ISpaceProcess process = (ISpaceProcess) procs[i];
			process.start(clockservice, space);
		}

		if(tick)
		{
			clockservice.createTickTimer(new ITimedObject()
			{
				public void timeEventOccurred(long currenttime)
				{
					exeservice.execute(executable);
					clockservice.createTickTimer(this);
				}
			});
		}
		else
		{
			clockservice.addChangeListener(new IChangeListener()
			{
				public void changeOccurred(ChangeEvent e)
				{
					exeservice.execute(executable);
				}
			});
		}
	}
}
