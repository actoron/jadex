package jadex.bdi.examples.disastermanagement.firebrigade;

import jadex.bdi.examples.disastermanagement.IClearChemicalsService;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *   Clear chemicals service.
 */
@Service
public class ClearChemicalsService implements IClearChemicalsService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IBDIInternalAccess agent;
	
	//-------- methods --------
	
	/**
	 *  Clear chemicals.
	 *  @param disaster The disaster.
	 *  @return Future, null when done.
	 */
	public ITerminableFuture<Void> clearChemicals(final ISpaceObject disaster)
	{
		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				IGoal[] goals = (IGoal[])agent.getGoalbase().getGoals("clear_chemicals");
				for(int i=0; i<goals.length; i++)
				{
//					System.out.println("Dropping: "+goals[i]);
					goals[i].drop();
				}
			}
		});
		
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
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ClearChemicalsService, "+agent.getComponentIdentifier();
	}
}
