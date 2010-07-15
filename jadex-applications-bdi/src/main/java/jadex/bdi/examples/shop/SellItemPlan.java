package jadex.bdi.examples.shop;

import jadex.bdi.runtime.Plan;

/**
 * 
 */
public class SellItemPlan extends Plan
{
	/**
	 *  The plan body.
	 */
	public void body()
	{
		String name = (String)getParameter("name").getValue();
		System.out.println(getComponentName()+" sell item: "+name);
		
		getParameter("result").setValue("Sold item: "+name);
		
		// kill shop for cache test purposes.
		killAgent();
	}
}
