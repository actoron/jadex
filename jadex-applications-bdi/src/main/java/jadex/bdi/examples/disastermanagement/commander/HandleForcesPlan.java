package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.Plan;
import jadex.commons.service.IService;

import java.util.Collection;
import java.util.Iterator;

/**
 *  Handle forces by sending an appropriate number to the disaster site..
 */
public class HandleForcesPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void	body()
	{		
		while(true)
		{
			final ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
			String servicename = (String)getParameter("servicename").getValue();
			String typename = (String)getParameter("typename").getValue();
			Collection forces = (Collection)getScope().getRequiredServices(servicename).get(this);
			int number = ((Integer)disaster.getProperty(typename)).intValue();
			
			if(forces.size()>0)
			{
				final IBeliefSet busy = getBeliefbase().getBeliefSet("busy_entities");	
				Iterator it = forces.iterator();
				
//				List goals = new ArrayList();
				while(number>getParameterSet("units").size() && it.hasNext())
				{
					IService force = (IService)it.next();
					final Object provid = force.getServiceIdentifier().getProviderId();
					if(!busy.containsFact(provid))
					{
						busy.addFact(provid);
						getParameterSet("units").addValue(force);
					
						IGoal sendforce = createGoal("send_rescueforce");
						sendforce.getParameter("disaster").setValue(disaster);
						sendforce.getParameter("rescueforce").setValue(force);
						dispatchSubgoal(sendforce);
						sendforce.addGoalListener(new IGoalListener()
						{
							public void goalFinished(AgentEvent ae)
							{
								busy.removeFact(provid);
							}
							
							public void goalAdded(AgentEvent ae)
							{
							}
						});
//						goals.add(sendforce);
					}
				}
			}
			
			waitFor(1000);
		}
	}
	
//	for(int i=0; i<goals.size(); i++)
//	{
//		IGoal goal = (IGoal)goals.get(i);
//		try
//		{
//			waitForGoal(goal);
//		}
//		catch(Exception e)
//		{
//		}
//		IService force = (IService)goal.getParameter("rescueforce").getValue();
//		busy.removeFact(force.getServiceIdentifier().getProviderId());
//		getParameterSet("units").removeValue(force);
//	}
}
