package jadex.bdiv3x.runtime;

import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.IPlan;

import java.util.ArrayList;
import java.util.List;

/**
 *  Prepend capability prefix to plan names.
 */
public class PlanbaseWrapper implements IPlanbase
{
	//-------- attributes --------
	
	/** The flat plan base. */
	protected IPlanbase	planbase;
	
	/** The full capability prefix. */
	protected String	prefix;
		
	//-------- constructors --------
	
	/**
	 *  Create a plan base wrapper.
	 */
	public PlanbaseWrapper(IPlanbase planbase, String prefix)
	{
		this.planbase	= planbase;
		this.prefix	= prefix;
	}
	
	//-------- element methods ---------

	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public MElement getModelElement()
	{
		return planbase.getModelElement();
	}
	
	//-------- IPlanbase methods --------
	
	/**
	 *  Get all running plans of this planbase.
	 *  @return The plans.
	 */
	public IPlan[] getPlans()
	{
		List<IPlan>	ret	= new ArrayList<IPlan>();
		for(IPlan plan: planbase.getPlans())
		{
			if(plan.getModelElement().getName().startsWith(prefix))
			{
				ret.add(plan);
			}
		}
		return ret.toArray(new IPlan[ret.size()]);
	}
	
	/**
	 *  Get all plans of a specified type (=model element name).
	 *  @param type The plan type.
	 *  @return All plans of the specified type.
	 */
	public IPlan[] getPlans(String type)
	{
		return planbase.getPlans(prefix + type);
	}
	
	/**
	 *  Create a plan instance.
	 */
	public IPlan createPlan(MPlan mplan)
	{
		return planbase.createPlan(mplan);
	}
}