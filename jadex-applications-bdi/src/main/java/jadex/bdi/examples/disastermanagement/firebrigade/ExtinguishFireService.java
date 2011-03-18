package jadex.bdi.examples.disastermanagement.firebrigade;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.ICapability;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.BasicService;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Fire extinguish service.
 */
public class ExtinguishFireService extends BasicService implements IExtinguishFireService
{
	//-------- attributes --------
	
	/** The agent. */
	protected ICapability agent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service.
	 */
	public ExtinguishFireService(ICapability agent)
	{
		super(agent.getServiceProvider().getId(), IExtinguishFireService.class, null);
		this.agent = agent;
	}
	
	//-------- methods --------
	
	/**
	 *  Extinguish a fire.
	 *  @param disaster The disaster.
	 */
	public IFuture extinguishFire(final ISpaceObject disaster)
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
				final IGoal exfire = (IGoal)agent.getGoalbase().createGoal("extinguish_fire");
				exfire.getParameter("disaster").setValue(disaster);
				exfire.addGoalListener(new IGoalListener()
				{
					public void goalFinished(AgentEvent ae)
					{
						if(exfire.isSucceeded())
							ret.setResult(null);
						else
							ret.setException(new RuntimeException("Goal failure."));
					}
					
					public void goalAdded(AgentEvent ae)
					{
					}
				});
				agent.getGoalbase().dispatchTopLevelGoal(exfire);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Abort extinguishing fire.
	 *  @return Future, null when done.
	 */
	public IFuture abort()
	{
		final Future ret = new Future();
		
		IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("extinguish_fire");
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
		return "ExtinguishFireService, "+agent.getComponentIdentifier();
	}
}
