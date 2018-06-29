package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.IInternalEvent;
import jadex.bdiv3x.runtime.Plan;

/**
 * 
 */
public class APLTesterPlan extends Plan
{
	/**
	 * 
	 */
	public void body()
	{
		getWaitqueue().addInternalEvent("someevent");
		
		TestReport tr = new TestReport("#1 Instance plan wait", "Tests if two internal events can trigger one plan twice.");
		IInternalEvent ev1 = createInternalEvent("someevent");
		IInternalEvent ev2 = createInternalEvent("someevent");
		startAtomic();
		{
			dispatchInternalEvent(ev1);
			dispatchInternalEvent(ev2);
		}
		endAtomic();
		
		waitForInternalEvent("someevent");
		waitForInternalEvent("someevent", 500);
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#2", "Tests if waitqueue works.");
		
		getWaitqueue().addInternalEvent("someevent");
		
		IInternalEvent ev3 = createInternalEvent("someevent");
		IInternalEvent ev4 = createInternalEvent("someevent");
		dispatchInternalEvent(ev3);
		dispatchInternalEvent(ev4);
//		System.out.println("all disptached");
		
		try
		{
			waitForInternalEvent("someevent",500);
			waitForInternalEvent("someevent",500);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setReason("Plan should receive both internal events.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
