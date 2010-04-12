package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IBeliefSet;
import jadex.bdi.runtime.IBeliefSetListener;
import jadex.bdi.runtime.Plan;


/**
 * Test if proper events are generated when calling modified().
 */
public class ManualModifiedPlan extends Plan
{
	/**
	 * Plan body.
	 */
	public void body()
	{
		IBeliefSet testbel = getBeliefbase().getBeliefSet("test");
		final IBeliefSet reports = getBeliefbase().getBeliefSet("testcap.reports");

		testbel.addBeliefSetListener(new IBeliefSetListener()
		{
			public void factAdded(AgentEvent ae)
			{
				if(ae.getValue().equals(new Integer(1)))
				{
					reports.addFact(new TestReport("#1", "Adding first fact.", true, null));
				}
				else if(ae.getValue().equals(new Integer(2)))
				{
					reports.addFact(new TestReport("#2", "Adding second fact.", true, null));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Adding ??? fact.", false, "Unexpected event for: "+ae.getValue()));
				}
			}

			public void factRemoved(AgentEvent ae)
			{
				if(ae.getValue().equals(new Integer(1)))
				{
					reports.addFact(new TestReport("#3", "Removing first fact.", true, null));
				}
				else if(ae.getValue().equals(new Integer(2)))
				{
					reports.addFact(new TestReport("#4", "Modifying second fact.", false, "Removed event instead of modified."));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Removing ??? fact.", false, "Unexpected event for: "+ae.getValue()));
				}
			}

			public void factChanged(AgentEvent ae)
			{
				if(ae.getValue().equals(new Integer(1)))
				{
					reports.addFact(new TestReport("#3", "Removing first fact.", false, "Modified event instead of removed."));
				}
				else if(ae.getValue().equals(new Integer(2)))
				{
					reports.addFact(new TestReport("#4", "Modifying second fact.", true, null));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Modifying ??? fact.", false, "Unexpected event for: "+ae.getValue()));
				}
			}
		});


		Object obj1 = new Integer(1);
		Object obj2 = new Integer(2);
		testbel.addFact(obj1);
		testbel.addFact(obj2);
		testbel.removeFact(obj1);
		testbel.modified(obj2);
	}
}
