package jadex.bdi.examples.cleanerworld;

import jadex.application.runtime.IApplication;
import jadex.application.space.envsupport.environment.IEnvironmentSpace;
import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.ISpaceProcess;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.commons.IFuture;
import jadex.commons.SimplePropertyObject;
import jadex.commons.concurrent.IResultListener;
import jadex.service.clock.IClockService;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
				final IApplication app = space.getContext();
				Map params = new HashMap();
				params.put("wastebins", todo.toArray());
				ongoing.addAll(todo);
				IComponentManagementService	ces	= (IComponentManagementService)app.getServiceContainer().getService(IComponentManagementService.class);
				
				IFuture ret = ces.createComponent(null, app.getComponentFilename("Truck"),
					new CreationInfo(null, params, app.getComponentIdentifier(), false, false, false, app.getAllImports()), null);
			
				IResultListener lis = new IResultListener()
				{
					public void exceptionOccurred(Object source, Exception exception)
					{
					}
					public void resultAvailable(Object source, Object result)
					{
						IComponentIdentifier truck = (IComponentIdentifier)result;
						IComponentManagementService ces = (IComponentManagementService)app.getServiceContainer()
							.getService(IComponentManagementService.class);
						ces.getExternalAccess(truck, new IResultListener()
						{
							public void exceptionOccurred(Object source, Exception exception)
							{
							}
							public void resultAvailable(Object source, Object result)
							{
								IBDIExternalAccess ex = (IBDIExternalAccess)result;
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
				};
				ret.addResultListener(lis);
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
