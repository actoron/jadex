package jadex.bdi.runtime;

import jadex.bdi.model.IMPlan;


/**
 *  The programmers interface for a plan base.
 */
public interface IPlanbase extends IElement
{
	//-------- methods --------

	/**
	 *  Get all running plans of this planbase.
	 *  @return The plans.
	 */
	public IPlan[] getPlans();

	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IPlan[] getPlans(String type);

	/**
	 *  Create a plan instance.
	 */
	public IPlan createPlan(IMPlan mplan);
	
	/**
	 *  Get a plan by name.
	 *  @param name	The plan name.
	 *  @return The plan with that name (if any).
	 */
//	public IPlan	getPlan(String name);

	/**
	 *  Register a new plan.
	 *  @param mplan The new plan model.
	 */
//	public void registerPlan(IMPlan mplan);

	/**
	 *  Deregister a plan.
	 *  @param mplan The plan model.
	 */
//	public void deregisterPlan(IMPlan mplan);
	
	//-------- listeners --------
	
	/**
	 *  Add a plan listener.
	 *  @param type	The goal type.
	 *  @param listener The plan listener.
	 */
	public void addPlanListener(String type, IPlanListener listener);	
	
	/**
	 *  Remove a goal listener.
	 *  @param type	The goal type.
	 *  @param listener The goal listener.
	 */
	public void removePlanListener(String type, IPlanListener listener);
}
