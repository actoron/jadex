package jadex.bdi.examples.cleanerworld;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import jadex.adapter.base.envsupport.environment.IEnvironmentSpace;
import jadex.adapter.base.envsupport.environment.ISpaceObject;
import jadex.adapter.base.envsupport.environment.ISpaceProcess;
import jadex.adapter.base.fipa.IAMS;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bridge.IAgentIdentifier;
import jadex.bridge.IApplicationContext;
import jadex.bridge.IClockService;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;

/**
 *  Process responsible for creating truck agents in response to full wastebins.
 */
public class CreateCollectionTruckProcess extends SimplePropertyObject implements ISpaceProcess
{
	protected Set ongoing = new HashSet();
	
	//-------- ISpaceProcess interface --------
	
	/**
	 *  This method will be executed by the object before the process gets added
	 *  to the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void start(IClockService clock, IEnvironmentSpace space)
	{
//		System.out.println("create waste process started.");
	}

	/**
	 *  This method will be executed by the object before the process is removed
	 *  from the execution queue.
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void shutdown(IEnvironmentSpace space)
	{
//		System.out.println("create waste process shutdowned.");
	}

	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 */
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		ISpaceObject[] wastebins = space.getSpaceObjectsByType("wastebin");
		
		if(wastebins.length>0)
		{
			final Set todo = new HashSet();
			
			for(int i=0; i<wastebins.length; i++)
			{
				if(((Boolean)wastebins[i].getProperty("full")).booleanValue()
					&& !ongoing.contains(wastebins[i]))
				{
					todo.add(wastebins[i]);
				}
			}
		
			if(todo.size()>0)
			{
//				System.out.println("Creating garbage collection truck.");
				final IApplicationContext app = (IApplicationContext)space.getContext();
				Map params = new HashMap();
				params.put("wastebins", todo.toArray());
				ongoing.addAll(todo);
				app.createAgent(null, "Truck", null, params, true, false, new IResultListener()
				{
					public void exceptionOccurred(Exception exception)
					{
					}
					public void resultAvailable(Object result)
					{
						IAgentIdentifier truck = (IAgentIdentifier)result;
						IAMS ams = (IAMS)app.getPlatform().getService(IAMS.class);
						ams.getExternalAccess(truck, new IResultListener()
						{
							public void exceptionOccurred(Exception exception)
							{
							}
							public void resultAvailable(Object result)
							{
								IExternalAccess ex = (IExternalAccess)result;
								ex.addAgentListener(new IAgentListener()
								{
									public void agentTerminated(AgentEvent ae)
									{
										ongoing.removeAll(todo);
									}
									public void agentTerminating(AgentEvent ae)
									{
									}
								});
							}
						});
						
					}
				}, null);
			}
		}
	}
	
	/**
	 *  Executes the environment process
	 *  @param clock	The clock.
	 *  @param space	The space this process is running in.
	 * /
	public void execute(IClockService clock, IEnvironmentSpace space)
	{
		ISpaceObject[] trucks = space.getSpaceObjectsByType("truck");
		if(trucks.length==0)
		{
			ISpaceObject[] wastebins = space.getSpaceObjectsByType("wastebin");
			
			if(wastebins.length>0)
			{
				boolean full = true;
				
				for(int i=0; full && i<wastebins.length; i++)
				{
					if(!((Boolean)wastebins[i].getProperty("full")).booleanValue())
						full = false;
				}
			
				if(full)
				{
					System.out.println("Creating garbage collection truck.");
					IApplicationContext app = (IApplicationContext)space.getContext();
					Map params = new HashMap();
					params.put("wastebins", wastebins);
					app.createAgent(null, "Truck", null, params, true, false, null, null);
				}
			}
		}
	}*/
}
