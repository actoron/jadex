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
	
	//-------- methods --------

	/**
	 *  The plan body.
	 */
	public void body()
	{
		this.result = getParameter("result").getValue();
		
		getLogger().info(""+result);
	}
}
