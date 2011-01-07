package jadex.bdi.examples.disastermanagement.firebrigade;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.service.BasicService;

/**
 *   Clear chemicals service.
 */
public class ClearChemicalsService extends BasicService implements IClearChemicalsService
{
	//-------- attributes --------
	
	/** The agent. */
	protected ICapability agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public ClearChemicalsService(ICapability agent)
	{
		super(agent.getServiceProvider().getId(), IClearChemicalsService.class, null);
		this.agent = agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public IFuture clearChemicals(final ISpaceObject disaster)
	{
		final Future ret = new Future();
		
		IGoal[] exgoals = (IGoal[])agent.getGoalbase().getGoals("extinguish_fire");
		if(exgoals.length>0)
		{
			ret.setException(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
		}
		else
		{
			IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("clear_chemicals");
			if(goals.length>0)
			{
				ret.setException(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
			}
			else
			{
				final IGoal clearchem = agent.getGoalbase().createGoal("clear_chemicals");
				clearchem.getParameter("disaster").setValue(disaster);
				clearchem.addGoalListener(new IGoalListener()
				{
					public void goalFinished(AgentEvent ae)
					{
						if(clearchem.isSucceeded())
							ret.setResult(null);
						else
							ret.setException(new RuntimeException("Goal failure."));
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
				agent.getGoalbase().dispatchTopLevelGoal(clearchem);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Abort clearing chemicals.
	 *  @return Future, null when done.
	 */
	public IFuture abort()
	{
		final Future ret = new Future();
		
		IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("clear_chemicals");
		for(int i=0; i<goals.length; i++)
		{
//			System.out.println("Dropping: "+goals[i]);
			goals[i].drop();
		}
		ret.setResult(null);
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ClearChemicalsService, "+agent.getComponentIdentifier();
	}
}
