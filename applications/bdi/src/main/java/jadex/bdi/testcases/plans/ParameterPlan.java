package jadex.bdi.testcases.plans;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Plan that tests access to plan parameters.
 */
public class ParameterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instantiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Test values of parameters.");

		Object in1 = getParameter("in1").getValue();
		Object in2 = getParameter("in2").getValue();
		Object inout1 = getParameter("inout1").getValue();
		Object inout2 = getParameter("inout2").getValue();

		tr.setSucceeded(true);
		if(!in1.equals("initial"))
		{
			getLogger().info("In1 has wrong value: "+in1);
			tr.setSucceeded(false);
		}
		if(!in2.equals("default"))
		{
			getLogger().info("In2 has wrong value: "+in2);
			tr.setSucceeded(false);
		}
		if(!inout1.equals("initial"))
		{
			getLogger().info("Inout1 has wrong value: "+inout1);
			tr.setSucceeded(false);
		}
		if(!inout2.equals("default"))
		{
			getLogger().info("Inout2 has wrong value: "+inout2);
			tr.setSucceeded(false);
		}
		if(!tr.isSucceeded())
			tr.setReason("Parameter has wrong value.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		/*tr = new TestReport("#2", "Test parameter directions.");
		tr.setSucceeded(true);
		try
		{
			getParameter("in1").setValue("in");
			tr.setSucceeded(false);
		}
		catch(Exception e)
		{
		}
		try
		{
			getParameter("inout1").getValue();
			getParameter("inout1").setValue("in");
		}
		catch(Exception e)
		{
			tr.setSucceeded(false);
		}
		try
		{
			getParameter("out1").getValue();
			tr.setSucceeded(false);
		}
		catch(Exception e)
		{
		}

		if(!tr.isSucceeded())
			tr.setReason("Parameter access was wrong.");
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);*/
	}
}
