package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.model.IMBelief;
import jadex.bdi.runtime.Plan;

/**
 *  Test accessing model information.
 */
public class AccessModelPlan extends Plan
{
	
	/**
	 *  The body method is called on the
	 *  instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test accessing a belief model.");
		try
		{
			IMBelief mbel = (IMBelief)getBeliefbase().getBelief("timeout").getModelElement();
			if(mbel!=null)
				tr.setSucceeded(true);
			else
				tr.setReason("Could not get belief timeout");
		}
		catch(Exception e)
		{
			tr.setReason("Could not get belief timeout");
			getLogger().severe("Exception while creating the worker agent: "+ e);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
