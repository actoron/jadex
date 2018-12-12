package jadex.bdi.examples.moneypainter;

import jadex.bdiv3x.runtime.Plan;

/**
 * 
 */
public class PaintOneEuroPlan extends Plan
{
	/**
	 * 
	 */
	public void body()
	{
//		getBeliefbase().getBelief("painting").setFact(Boolean.TRUE);
//		System.out.println("start painting, "+((GoalFlyweight)getReason()).getHandle()+" "+this.getRPlan());
		
		// Hack! Should not be necessary due to context condition.
		// Problem is current implementation is that executePlanStep() and abortPlan() are not ordered.
		if(getBeliefbase().getBelief("painter").getFact()!=null)
		{
			System.out.println("dreck: "+getBeliefbase().getBelief("painter").getFact());
			fail();
		}
		
		getBeliefbase().getBelief("painter").setFact(this.getRPlan());
		System.out.println("painting start: "+getComponentIdentifier());//this.getRPlan());
		
		waitFor(2000);
//		if(Math.random()>0.7)
//		{
//			getBeliefbase().getBelief("painting").setFact(Boolean.FALSE);
//			throw new RuntimeException("end painting: painted euro not good enough");
//		}
//		System.out.println("end painting: ok, "+this);
		System.out.println("painting end: "+getComponentIdentifier());
		
		getParameter("result").setValue(getComponentIdentifier().getName());
		
		getBeliefbase().getBelief("painter").setFact(null);
//		getBeliefbase().getBelief("painting").setFact(Boolean.FALSE);
	}
	
	public void failed()
	{
		System.out.println("failed: "+this.getRPlan());
	}

	public void aborted()
	{
		System.out.println("aborted: "+this.getRPlan());
	}
	
}
