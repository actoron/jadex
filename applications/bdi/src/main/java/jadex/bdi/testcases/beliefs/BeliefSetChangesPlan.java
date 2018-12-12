package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test different kinds of belief set changes.
 */
public class BeliefSetChangesPlan extends Plan
{
	/**
	 *  The body method.
	 */
	public void body()
	{
		// Hack!!! wait for initial triggers.
		waitFor(300);

		// Test addFact() and removeFact() plan triggers

		TestReport tr = new TestReport("#1", "Test if factadded on belief set causes plan trigger.");
		int before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("Test 1: One should be created in response to addValue of belset_a.");
		getLogger().info("belset_a.addFact(\"a4\")");
		getBeliefbase().getBeliefSet("belset_a").addFact("a4");
		waitFor(300);
		int after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			tr.setReason("One should be created in response to addValue of belset_a.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Test if factremoved on belief set causes plan trigger.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("\n\nTest 2: Plan a should be created in response to removeValue of belset_a.");
		getLogger().info("belset_a.removeFact(\"a4\")");
		getBeliefbase().getBeliefSet("belset_a").removeFact("a1");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after+1)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			tr.setReason("Plan a should be created in response to removeValue of belset_a.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// test <facts> tag changes
		getLogger().info("-----------------------------------------------");

		// Case 3: a new value is stored in the belief "bel". As it is equal
		// no change events will be propagated.
		tr = new TestReport("#3", "Test if setting same value does not provoke invocations.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("\n\nTest 3: A new value is stored in the belief bel."+
			"As it is equal no change events will be propagated");
		getBeliefbase().getBelief("bel").setFact(new BeanChangesArrayList());
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			tr.setReason("Setting same value on bel invoked a plan.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Case 4: the fact of "bel" is provoked to throw a bean property change
		// by calling the modified() method. As the "belset" does not have changed
		// considering its content no plan is invoked.
		tr = new TestReport("#4", "Test if dependent belset recognizes same value.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		getLogger().info("\n\nTest 4: The fact of bel is provoked to throw a bean property change "+
			"by calling the modified() method. As the belset does not have changed "+
			"considering its content no plan is invoked");
		BeanChangesArrayList a = (BeanChangesArrayList)getBeliefbase().getBelief("bel").getFact();
		a.modified();
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			tr.setReason("Setting same value on belset invoked a plan");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Case 5: a new value is added causing a bean property change event that
		// leads to plan invocation.
		tr = new TestReport("#5", "Add indirectly a value to belset.");
		getLogger().info("\n\nTest 5: a new value is added causing a bean property "+
			"change event that leads to plan invocation.");
		before = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		a.add("a");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("result").getFact()).intValue();
		if(before+1==after)
		{
			getLogger().info("TEST SUCCEEDED.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: "+before+" - "+after);
			tr.setReason("No plan was invoked.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		// Test if added / removed facts can be accessed via the reserved variables
		// "$addedfact" and "$removedfact"
		tr = new TestReport("#6", "Test if added fact can be accessed.");
		String newfact = "a new fact";
		getBeliefbase().getBeliefSet("belset_b").addFact(newfact);
		waitFor(200);
		if(getBeliefbase().getBelief("result_b").getFact()!=null)
			tr.setSucceeded(true);
		else
			tr.setReason("Fact could not be accessed via '$addedfact'.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
		
		tr = new TestReport("#7", "Test if removed fact can be accessed.");
		getBeliefbase().getBelief("result_b").setFact(null);
		getBeliefbase().getBeliefSet("belset_b").removeFact(newfact);		
		waitFor(200);
		if(getBeliefbase().getBelief("result_b").getFact()!=null)
			tr.setSucceeded(true);
		else
			tr.setReason("Fact could not be accessed via '$removedfact'.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
