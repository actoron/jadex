package jadex.bdi.model.editable;

import jadex.bdi.model.IMPlanbase;

/**
 * 
 */
public interface IMEPlanbase extends IMPlanbase
{
	/**
	 *  Create a plan with a name.
	 *  @param name	The plan name.
	 */
	public IMEPlan createPlan(String name);
}
