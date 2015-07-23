package jadex.bdiv3x.runtime;

import java.util.Collection;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IPlan;
import jadex.bdiv3.runtime.impl.RElement;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bridge.IInternalAccess;

/**
 *  The planbase.
 */
public class RPlanbase extends RElement implements IPlanbase
{
	/**
	 *  Create a new goalbase.
	 */
	public RPlanbase(IInternalAccess agent)
	{
		super(null, agent);
	}
	
	//-------- methods --------

	/**
	 *  Get all running plans of this planbase.
	 *  @return The plans.
	 */
	public IPlan[] getPlans()
	{
		Collection<RPlan> ret = getCapability().getPlans();
		return ret.toArray(new IPlan[ret.size()]);
	}

	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IPlan[] getPlans(String type)
	{
		// Todo: add capability scope
		type	= type.replace(".", MElement.CAPABILITY_SEPARATOR);
		
		MCapability mcapa = (MCapability)getCapability().getModelElement();
		MPlan mplan = mcapa.getPlan(type);
		Collection<RPlan> ret = getCapability().getPlans(mplan);
		return ret.toArray(new IPlan[ret.size()]);
	}

	/**
	 *  Create a plan instance.
	 */
	public IPlan createPlan(MPlan mplan)
	{
		return RPlan.createRPlan(mplan, null, null, getAgent(), null, null);
	}
	
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
