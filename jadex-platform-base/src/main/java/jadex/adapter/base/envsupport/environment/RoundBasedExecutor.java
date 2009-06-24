package jadex.adapter.base.envsupport.environment;

import jadex.adapter.base.appdescriptor.ApplicationContext;
import jadex.adapter.base.envsupport.dataview.IDataView;
import jadex.adapter.base.envsupport.environment.AgentActionList.ActionEntry;
import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.Vector1Long;
import jadex.adapter.base.execution.IExecutionService;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IClockService;
import jadex.bridge.IPlatform;
import jadex.bridge.ITimedObject;
import jadex.commons.ICommand;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IExecutable;

import java.util.Comparator;
import java.util.Iterator;

/**
 *  Synchronized execution of all actions in rounds based on clock ticks.
 */
public class RoundBasedExecutor extends SimplePropertyObject implements ISpaceExecutor
{
	//-------- constants --------
	
	/** The property for the action execution mode. */
	public static final String	PROPERTY_MODE	= "mode";
	
	/** The value for the last action execution mode. */
	public static final String	MODE_LASTACTION	= "lastaction";
	
	//-------- attributes --------
	
	/** Last time stamp. */
	protected long timestamp;
	
	/** Current time. */
	protected long currenttime;
	
	//-------- constructors--------
	
	/**
	 *  Creates a new round based executor.
	 */
	public RoundBasedExecutor()
	{
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space)
	{
		this(space, null);
	}
	
	/**
	 *  Creates a new round based executor.
	 *  @param space	The space.
	 *  @param clockservice	The clock service.
	 *  @param acomp	The action comparator.
	 */
	public RoundBasedExecutor(AbstractEnvironmentSpace space, Comparator acomp)
	{
		setProperty("space", space);
		setProperty("comparator", acomp);
	}
	
	//-------- methods --------
	
	/**
	 *  Start the space executor.
	 */
	public void start()
	{
		final AbstractEnvironmentSpace space = (AbstractEnvironmentSpace)getProperty("space");
		IPlatform	platform	= ((ApplicationContext)space.getContext()).getPlatform();
		final IClockService clockservice = (IClockService)platform.getService(IClockService.class);
		final IExecutionService exeservice = (IExecutionService)platform.getService(IExecutionService.class);

		Comparator comp = (Comparator)getProperty("comparator");
		if(comp!=null)
		{
			space.getAgentActionList().setOrdering(comp);
		}
		if(MODE_LASTACTION.equals(getProperty(PROPERTY_MODE)))
		{
			space.getAgentActionList().setScheduleCommand(new ICommand()
			{
				public void execute(Object args)
				{
					ActionEntry	entry	= (ActionEntry)args;
					ActionEntry	entries[]	= space.getAgentActionList().getActionEntries();
					
					if(entries.length>0)
					{
						IAgentIdentifier	actor	= entry.parameters!=null ?
							(IAgentIdentifier)entry.parameters.get(ISpaceAction.ACTOR_ID) : null;
						
						for(int i=0; actor!=null && i<entries.length; i++)
						{
							IAgentIdentifier	actor2	= entries[i].parameters!=null ?
								(IAgentIdentifier)entries[i].parameters.get(ISpaceAction.ACTOR_ID) : null;
							if(actor.equals(actor2))
							{
//								System.out.println("Removing duplicate action: "+entries[i]);
								space.getAgentActionList().removeAgentAction(entries[i]);
							}
						}
					}
					
					space.getAgentActionList().addAgentAction(entry);
				}
			});
		}
		
		final IExecutable	executable	= new IExecutable()
		{
			boolean first = true;
			
			public boolean execute()
			{
//				System.out.println("---+++--- New round: "+currenttime+" ---+++---");
				
				IVector1 progress = new Vector1Long(currenttime - timestamp);
				timestamp = currenttime;
				
				synchronized(space.getMonitor())
				{
					if(!first)
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
					
					first = false;
				}
//				System.out.println("-------------------------------------------");

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
		
		// In the first round only percepts are distributed.
		clockservice.createTickTimer(new ITimedObject()
		{
			public void timeEventOccurred(long currenttime)
			{
				RoundBasedExecutor.this.currenttime = currenttime;
				exeservice.execute(executable);
				clockservice.createTickTimer(this);
			}
		});
	}
}
