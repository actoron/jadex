package jadex.bdi.examples.moneypainter;

import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBDIInternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IGoalListener;
import jadex.bdi.runtime.impl.flyweights.GoalFlyweight;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 * 
 */
@Service
public class PaintMoneyService implements IPaintMoneyService
{
	@ServiceComponent
	protected IBDIInternalAccess agent;
	
	/**
	 *  Paint one euro.
	 */
	public IFuture<String> paintOneEuro(String name)
	{
		final Future<String> ret = new Future<String>();
		
		if(agent.getBeliefbase().getBelief("painter").getFact()==null)
		{
			final IGoal goal = agent.getGoalbase().createGoal("getoneeuro");
			goal.getParameter("name").setValue(name);
			
//			final Object handle = ((GoalFlyweight)goal).getHandle();
			goal.addGoalListener(new IGoalListener()
			{
				public void goalFinished(AgentEvent ae)
				{
					if(goal.isSucceeded())
					{
//						System.out.println("painter success: "+handle);
						ret.setResult((String)goal.getParameter("result").getValue());
					}
					else
					{
//						System.out.println("painter failure: "+handle);
						ret.setException(goal.getException()!=null? goal.getException(): new RuntimeException());
					}
				}
				
				public void goalAdded(AgentEvent ae)
				{
				}
			});
			agent.getGoalbase().dispatchTopLevelGoal(goal);
		}
		else
		{
//			System.out.println("painter failure (busy)");
			ret.setException(new RuntimeException("Painter busy: "+agent.getConfigurationName()));
		}
	
		return ret;
	}
}
