package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.IPlanListener;


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
	public IPlan createPlan(MPlan mplan);
		
	//-------- listeners --------
	
//	/**
//	 *  Add a plan listener.
//	 *  @param type	The goal type.
//	 *  @param listener The plan listener.
//	 */
//	public <T> void addPlanListener(String type, IPlanListener<T> listener);	
//	
//	/**
//	 *  Remove a goal listener.
//	 *  @param type	The goal type.
//	 *  @param listener The goal listener.
//	 */
//	public <T> void removePlanListener(String type, IPlanListener<T> listener);
}
