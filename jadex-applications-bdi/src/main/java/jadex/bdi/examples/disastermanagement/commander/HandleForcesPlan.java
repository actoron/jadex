package jadex.bdi.examples.disastermanagement.commander;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.Plan;
import jadex.commons.service.IService;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

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
		final ISpaceObject disaster = (ISpaceObject)getParameter("disaster").getValue();
		int number = ((Integer)getParameter("number").getValue()).intValue();
		Collection forces = (Collection)getParameter("forces").getValue();
		
		if(forces.size()>0)
		{
			final IBeliefSet busy = getBeliefbase().getBeliefSet("busy_entities");	
			Iterator it = forces.iterator();
			
			List goals = new ArrayList();
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
					goals.add(sendforce);
				}
			}
			
			for(int i=0; i<goals.size(); i++)
			{
				IGoal goal = (IGoal)goals.get(i);
				try
				{
					waitForGoal(goal);
				}
				catch(Exception e)
				{
				}
				IService force = (IService)goal.getParameter("rescueforce").getValue();
				busy.removeFact(force.getServiceIdentifier().getProviderId());
				getParameterSet("units").removeValue(force);
			}
		}
	}
}
