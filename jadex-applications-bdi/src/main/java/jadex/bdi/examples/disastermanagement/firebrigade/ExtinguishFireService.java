package jadex.bdi.examples.disastermanagement.firebrigade;

import jadex.bdi.examples.disastermanagement.IExtinguishFireService;
import jadex.bdiv3.runtime.IGoal;
import jadex.bdiv3x.features.IBDIXAgentFeature;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITerminableFuture;
import jadex.commons.future.TerminableFuture;
import jadex.commons.future.TerminationCommand;
import jadex.extension.envsupport.environment.ISpaceObject;

/**
 *  Fire extinguish service.
 */
@Service
public class ExtinguishFireService implements IExtinguishFireService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;

	//-------- methods --------
	
	/**
	 *  Extinguish a fire.
	 *  @param disaster The disaster.
	 */
	public ITerminableFuture<Void> extinguishFire(final ISpaceObject disaster)
	{
		final IBDIXAgentFeature capa = agent.getComponentFeature(IBDIXAgentFeature.class);

		final TerminableFuture<Void> ret	= new TerminableFuture<Void>(new TerminationCommand()
		{
			public void terminated(Exception reason)
			{
				IGoal[] goals = (IGoal[])capa.getGoalbase().getGoals("extinguish_fire");
				for(int i=0; i<goals.length; i++)
				{
//					System.out.println("Dropping: "+goals[i]);
					goals[i].drop();
				}
			}
		});;
		
		IGoal[] exgoals = (IGoal[])capa.getGoalbase().getGoals("extinguish_fire");
		if(exgoals.length>0)
		{
			ret.setException(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
		}
		else
		{
			IGoal[] goals = (IGoal[])capa.getGoalbase().getGoals("clear_chemicals");
			if(goals.length>0)
			{
				ret.setException(new IllegalStateException("Can only handle one order at a time. Use abort() first."));
			}
			else
			{
				final IGoal exfire = (IGoal)capa.getGoalbase().createGoal("extinguish_fire");
				exfire.getParameter("disaster").setValue(disaster);
//				exfire.addGoalListener(new IGoalListener()
//				{
//					public void goalFinished(AgentEvent ae)
//					{
//						if(exfire.isSucceeded())
//							ret.setResult(null);
//						else
//							ret.setException(new RuntimeException("Goal failure."));
//					}
//					
//					public void goalAdded(AgentEvent ae)
//					{
//					}
//				});
				capa.getGoalbase().dispatchTopLevelGoal(exfire).addResultListener(new IResultListener<Object>()
				{
					public void resultAvailable(Object result)
					{
						if(exfire.isSucceeded())
							ret.setResult(null);
						else
							ret.setException(new RuntimeException("Goal failure."));
					}
					
					public void exceptionOccurred(Exception exception)
					{
						ret.setException(exception);
					}
				});
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
		return "ExtinguishFireService, "+agent.getComponentIdentifier();
	}
}
