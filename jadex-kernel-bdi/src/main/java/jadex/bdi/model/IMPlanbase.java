package jadex.bdi.model;

/**
 *  Interface for planbase model.
 */
public interface IMPlanbase
{
	/**
	 *  Get a belief for a name.
	 *  @param name	The belief name.
	 */
	public IMPlan getPlan(String name);

	/**
	 *  Returns all beliefs.
	 *  @return All beliefs.
	 */
	public IMPlan[] getPlans();

}
