package jadex.bdiv3.examples.moneypainter;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.annotation.Publish;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.PlanFailureException;
import jadex.bridge.IInternalAccess;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;

/**
 *  The money painter.
 */
@Agent(type=BDIAgentFactory.TYPE)
//@ProvidedServices(@ProvidedService(name="paintser", type=IPaintMoneyService.class, 
//	implementation=@Implementation(BDIAgent.class)))
public class PainterAgent
{
	@Agent 
	protected IInternalAccess agent;
	
	@Belief
	protected Object painter;
	
	@Goal(publish=@Publish(type=IPaintMoneyService.class))
	public class GetOneEuro
	{
		public GetOneEuro(String from)
		{
//			System.out.println("created goal: "+this);
//			Thread.dumpStack();
//			System.out.println("created painter goal: "+from);
		}
	} 
	
//	@Plan(trigger=@Trigger(service=@ServiceTrigger(name="paintser")))
	@Plan(trigger=@Trigger(goals=GetOneEuro.class))
	public class PaintPlan
	{
		@PlanBody
		public String paint(IPlan plan, GetOneEuro goal)
		{
			if(painter!=null)
				throw new PlanFailureException();
			
			painter = this;

			System.out.println("painting start: "+agent.getId()+" "+goal);//this.getRPlan());
			
			plan.waitFor(2000).get();
	//		if(Math.random()>0.7)
	//		{
	//			getBeliefbase().getBelief("painting").setFact(Boolean.FALSE);
	//			throw new RuntimeException("end painting: painted euro not good enough");
	//		}
	//		System.out.println("end painting: ok, "+this);
			System.out.println("painting end: "+agent.getId());
			
			painter = null;
			
			return agent.getId().getName();
		}
		
		@PlanAborted
		@PlanFailed
		public void fail(Exception ex)
		{
			System.out.println("failed: "+ex);
		}
		
		@PlanPrecondition
//		@PlanContextCondition(beliefs="painter")
		public boolean checkPrecond()
		{
			return painter==null;
		}
	}
}
