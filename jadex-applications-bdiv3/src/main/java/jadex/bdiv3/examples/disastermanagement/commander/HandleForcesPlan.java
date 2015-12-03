package jadex.bdiv3.examples.disastermanagement.commander;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.examples.disastermanagement.commander.CommanderBDI.SendRescueForce;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.service.IService;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Handle forces by sending an appropriate number to the disaster site..
 */
@Plan
public abstract class HandleForcesPlan 
{
	//-------- attributes --------

	@PlanCapability
	protected CommanderBDI capa;
	
	@PlanAPI
	protected IPlan rplan;
	
	@PlanReason
	protected IForcesGoal goal;
		
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	allocateForces(String servicename, String typename)
	{		
		while(true)
		{
			final ISpaceObject disaster = goal.getDisaster();
			IIntermediateFuture<IService> fut = capa.getAgent().getComponentFeature(IRequiredServicesFeature.class).getRequiredServices(servicename);
			Collection<IService> forces = (Collection<IService>)fut.get();
			int number = ((Integer)disaster.getProperty(typename)).intValue();
			
//			int as = 0;
			if(forces.size()>0)
			{
				List<IService> fs = new ArrayList<IService>(forces);
				Iterator<IService> it = fs.iterator();
				
//				List goals = new ArrayList();
				while(number>goal.getUnits().size() && it.hasNext())
				{
					final IService force = (IService)it.next();
					final Object provid = force.getServiceIdentifier().getProviderId();
					if(!capa.getBusyEntities().contains(provid))
					{
//						as++;
						capa.getBusyEntities().add(provid);
						goal.getUnits().add(force);
					
						SendRescueForce sendforce = capa.new SendRescueForce(disaster, force);
						rplan.dispatchSubgoal(sendforce).addResultListener(new IResultListener<Object>()
						{
							public void resultAvailable(Object result)
							{
								goal.getUnits().remove(force);
								capa.getBusyEntities().remove(provid);
							}
							
							public void exceptionOccurred(Exception exception)
							{
								goal.getUnits().remove(force);
								capa.getBusyEntities().remove(provid);
							}
						});
					}
				}
			}
			
//			System.out.println("hf: "+disaster.getId()+" "+number+" "+getParameterSet("units").getValues().length+" "+as+" "+busy.size()+" "+SUtil.arrayToString(busy.getFacts()));
			
//			waitForFactRemoved("busy_entities");
			rplan.waitFor(1000).get();
		}
	}
	
	@PlanAborted
	@PlanFailed
	public void failure(Exception e)
	{
//		System.out.println("aborted: "+this+" "+goal.getDisaster());
//		e.printStackTrace();
	}
}
