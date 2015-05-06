package jadex.bdi.testcases;

import jadex.bdiv3x.runtime.Plan;

/**
 *  Print results to some output
 */
public class PrintResultPlan extends Plan
{
	//-------- attributes --------
	
	/** The result. */
	protected Object result;
	
	//-------- constructors --------

	/**
	 *  Create a new plan.
	 */
	public PrintResultPlan()
	{
//		System.out.println("create: "+getRPlan());
		this.result = getParameter("result").getValue();
	}
	
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
//		System.out.println("body: "+getRPlan());
		getLogger().info(""+result);
	}
}
