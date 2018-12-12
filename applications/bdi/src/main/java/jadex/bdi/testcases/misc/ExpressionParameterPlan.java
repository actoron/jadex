package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;

/**
 *  Test some expressions.
 */
public class ExpressionParameterPlan extends Plan
{
	/**
	 * The body method is called on the
	 * instatiated plan instance from the scheduler.
	 */
	public void body()
	{
		TestReport tr = new TestReport("#1", "Using an expression with parameter.");
		try
		{
			getLogger().info("Test 1: Using an expression with parameter:");
			Object result = getExpression("exp").execute("$num", Integer.valueOf(0));
			getLogger().info("0: "+result);
			result = getExpression("exp").execute("$num", Integer.valueOf(1));
			getLogger().info("1: "+result);
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setReason("Expression could not be evaluated: "+e);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);

		tr = new TestReport("#2", "Using an expression with a join.");
		try
		{
			getLogger().info("\n\nTest 2: Using an expression with a join:");
			getLogger().info(""+getExpression("join").execute());
			tr.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr.setReason("Expression could not be evaluated: "+e);
		}
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
