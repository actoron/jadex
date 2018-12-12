package jadex.bdi.testcases.beliefs;

import jadex.base.test.TestReport;
import jadex.bdiv3.runtime.impl.BeliefAdapter;
import jadex.bdiv3x.runtime.IBeliefSet;
import jadex.bdiv3x.runtime.Plan;
import jadex.rules.eca.ChangeInfo;


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

		testbel.addBeliefSetListener(new BeliefAdapter<Object>()
		{
			public void factAdded(ChangeInfo<Object> info)
			{
				if(info.getValue().equals(Integer.valueOf(1)))
				{
					reports.addFact(new TestReport("#1", "Adding first fact.", true, null));
				}
				else if(info.getValue().equals(Integer.valueOf(2)))
				{
					reports.addFact(new TestReport("#2", "Adding second fact.", true, null));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Adding ??? fact.", false, "Unexpected event for: "+info.getValue()));
				}
			}
			
			public void factRemoved(ChangeInfo<Object> info)
			{
				if(info.getValue().equals(Integer.valueOf(1)))
				{
					reports.addFact(new TestReport("#3", "Removing first fact.", true, null));
				}
				else if(info.getValue().equals(Integer.valueOf(2)))
				{
					reports.addFact(new TestReport("#4", "Modifying second fact.", false, "Removed event instead of modified."));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Removing ??? fact.", false, "Unexpected event for: "+info.getValue()));
				}
			}

			public void factChanged(ChangeInfo<Object> info)
			{
				if(info.getValue().equals(Integer.valueOf(1)))
				{
					reports.addFact(new TestReport("#3", "Removing first fact.", false, "Modified event instead of removed."));
				}
				else if(info.getValue().equals(Integer.valueOf(2)))
				{
					reports.addFact(new TestReport("#4", "Modifying second fact.", true, null));
				}
				else 
				{
					reports.addFact(new TestReport("??", "Modifying ??? fact.", false, "Unexpected event for: "+info.getValue()));
				}
			}
		});
			
		Object obj1 = Integer.valueOf(1);
		Object obj2 = Integer.valueOf(2);
		testbel.addFact(obj1);
		testbel.addFact(obj2);
		testbel.removeFact(obj1);
		testbel.modified(obj2);
	}
}
