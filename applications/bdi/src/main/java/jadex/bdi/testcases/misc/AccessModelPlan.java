package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3x.runtime.Plan;

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
			MBelief mbel = (MBelief)getBeliefbase().getBelief("timeout").getModelElement();
	
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
			MPlan mplan = ((IBDIModel)getAgent().getModel()).getCapability().getPlan("accessmodel_plan");
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
