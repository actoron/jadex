package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdi.model.IMBelief;
import jadex.bdi.model.IMPlan;
import jadex.bdi.model.IMPlanbase;
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
		
		tr = new TestReport("#2", "Test accessing a plan model.");
		try
		{
			IMPlan mplan = ((IMPlanbase)getPlanbase().getModelElement()).getPlan("accessmodel_plan");
			if(mplan!=null)
				tr.setSucceeded(true);
			else
				tr.setReason("Could not get plan accessmodel_plan");
		}
		catch(Exception e)
		{
			tr.setReason("Could not get belief timeout");
			getLogger().severe("Exception while creating the worker agent: "+ e);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
