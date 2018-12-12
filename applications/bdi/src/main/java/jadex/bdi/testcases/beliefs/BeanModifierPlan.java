package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  The print plan simply prints out the object
 *  it gets as parameter in the construtor.
 */
public class BeanModifierPlan extends Plan
{
//	//-------- constructors --------
//
//	/**
//	 *  Create a new plan.
//	 */
//	public BeanModifierPlan()
//	{
//		getLogger().fine("Created: "+this);
//		//System.out.println("Created: "+this);
//	}

	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		// Wait until increment plan has executed once (hack???).
		waitFor(300);

		boolean	success	= true;
		// Perform some belief actions to test the
		// event propagation
		TestReport tr = new TestReport("#1", "Changes name of fact of belief one.");
		int before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nChanging attribute of belief one:");
		TestBean alois = (TestBean)getBeliefbase().getBelief("one").getFact();
		alois.setName("Alois_changed");
		waitFor(300);
		int after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to bean modification.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to bean modification.");
			success	= false;
			tr.setReason("Plan should have been executed in response to bean modification.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Changes fact of belief one.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nSetting fact of belief one:");
		TestBean anna = new TestBean("Anna");
		getBeliefbase().getBelief("one").setFact(anna);
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to setting a new fact.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to setting a new fact.");
			success	= false;
			tr.setReason("Plan should have been executed in response to setting a new fact.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#3", "Test modified() of belief one.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\n Modified() belief one:");
		getBeliefbase().getBelief("one").modified();
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to modified() of belief.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to modified() of belief.");
			success	= false;
			tr.setReason("Plan should have been executed in response to modified() of belief.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#4", "Setting attribute of removed fact.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nSetting attribute of removed fact:");
		alois.setName("Alois_reloaded");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED: No plan was executed in response to modification of removed bean.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan was executed in response to modification of removed bean.");
			success	= false;
			tr.setReason("Plan was executed in response to modification of removed bean.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		getLogger().info("\n\n----------------------------------------");

		tr = new TestReport("#5", "Changing attribute of 2nd fact of belief some.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nChanging attribute of 2nd fact of belief some:");
		TestBean[] ps = (TestBean[])getBeliefbase().getBeliefSet("some").getFacts();
		ps[1].setName(ps[1].getName()+"_changed");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to bean modification.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to bean modification.");
			success	= false;
			tr.setReason("Plan should have been executed in response to bean modification.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#6", "Now adding a fact to beliefset some");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nNow adding a fact to beliefset some: ");
		TestBean charlie = new TestBean("Charlie");
		getBeliefbase().getBeliefSet("some").addFact(charlie);
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to fact addition.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to fact addition.");
			success	= false;
			tr.setReason("Plan should have been executed in response to fact addition.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#7", "Changing attribute of added fact.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nChanging attribute of added fact: ");
		charlie.setName("Charlie_changed");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to bean modification.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to bean modification.");
			success	= false;
			tr.setReason("Plan should have been executed in response to bean modification.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#8", "Test modified() of added fact.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nTest modified() of added fact: ");
		getBeliefbase().getBeliefSet("some").modified(charlie);
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to modified() fact.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to modified() fact.");
			success	= false;
			tr.setReason("Plan should have been executed in response to modified() fact.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#9", "Now removing the fact from beliefset some.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nNow removing the fact from beliefset some: ");
		getBeliefbase().getBeliefSet("some").removeFact(charlie);
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after-1)
		{
			getLogger().info("TEST SUCCEEDED: Plan was executed in response to fact removal.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should have been executed in response to fact removal.");
			success	= false;
			tr.setReason("Plan should have been executed in response to fact removal.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#10", "Now changing attribute of removed fact.");
		before = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		getLogger().info("\n\nNow changing attribute of removed fact: ");
		charlie.setName("Charlie_reloaded");
		waitFor(300);
		after = ((Integer)getBeliefbase().getBelief("invocations").getFact()).intValue();
		if(before==after)
		{
			getLogger().info("TEST SUCCEEDED: No Plan was executed in response to removed bean modification.");
			tr.setSucceeded(true);
		}
		else
		{
			getLogger().info("TEST FAILED: Plan should not have been executed in response to removed bean modification.");
			success	= false;
			tr.setReason("Plan should not have been executed in response to removed bean modification.");
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		if(success)
		{
			getLogger().info("All tests succeeded!");
		}
		else
		{
			getLogger().info("Some tests failed!");
		}

		//killAgent();
	}
}

