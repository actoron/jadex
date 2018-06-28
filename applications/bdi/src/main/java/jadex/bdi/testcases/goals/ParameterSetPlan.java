package jadex.bdi.testcases.goals;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.commons.SUtil;

/**
 *  Test if parameter sets work.
 */
public class ParameterSetPlan extends Plan
{
	public void body()
	{
		TestReport	report	= new TestReport("check_values", "Checks if the goal contains the correct parameter set <values>.");
		Object[]	vals	= getParameterSet("names").getValues();
		if("[a, b, c]".equals(SUtil.arrayToString(vals)))
			report.setSucceeded(true);
		else
			report.setReason("Values should be [a, b, c], but were: "+SUtil.arrayToString(vals));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
		
		report	= new TestReport("check_value", "Checks if the goal contains the correct parameter set <value>s.");
		vals	= getParameterSet("morenames").getValues();
		if("[d, e, f]".equals(SUtil.arrayToString(vals)))
			report.setSucceeded(true);
		else
			report.setReason("Values should be [d, e, f], but were: "+SUtil.arrayToString(vals));
		getBeliefbase().getBeliefSet("testcap.reports").addFact(report);
	}
}