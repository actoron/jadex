package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Change a belief (set) and test if a plan is triggerd in response.
 */
public class BeliefTriggerPlan extends Plan
{
	/**
	 *  The body method is called on the
	 *  instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		// Hack?! Must wait that belief initialization and plan reaction is done. 
		waitFor(100);
		boolean failed = false;

		TestReport tr = new TestReport("#1", "Changing belief value.");
		int before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 1: changing belief to 0");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(0));
		waitFor(100);
		int after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Setting belief to same value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 2: changing belief to 0");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(0));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was triggered in response to no belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#3", "Setting belief to new value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 3: changing belief to 1");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(1));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#4", "Setting belief to same value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 4: changing belief to 1");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(1));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was triggered in response to no belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#5", "Setting belief to new value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 5: changing belief to 2");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(2));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#6", "Setting belief to same value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 6: changing belief to 2");
		getBeliefbase().getBelief("bel").setFact(Integer.valueOf(2));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was triggered in response to no belief change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// belief set tests

		tr = new TestReport("#7", "Adding value to belief set.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 7: adding 1 to belief set");
		getBeliefbase().getBeliefSet("belset").addFact(Integer.valueOf(1));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief set change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#8", "Adding value to belief set.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 8: adding 2 to belief set");
		getBeliefbase().getBeliefSet("belset").addFact(Integer.valueOf(2));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief set change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#9", "Removing value from belief set.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 9: removing 2 from belief set");
		getBeliefbase().getBeliefSet("belset").removeFact(Integer.valueOf(2));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief set change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#10", "Removing value from belief set.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 10: removing 1 from belief set");
		getBeliefbase().getBeliefSet("belset").removeFact(Integer.valueOf(1));
		waitFor(100);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			failed = true;
			tr.setReason("Plan was not triggered in response to belief set change.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		if(!failed)
			getLogger().info("ALL TESTS SUCCEEDED!");
		else
			getLogger().info("SOME TESTS FAILED!");
	}
}
