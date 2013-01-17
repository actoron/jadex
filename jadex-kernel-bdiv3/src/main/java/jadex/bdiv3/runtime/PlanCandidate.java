package jadex.bdiv3.runtime;

import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MPlan;

import java.util.Map;

/**
 *  An object allowing users to specify custom plan candidates
 *  during event or processing.
 */
public class PlanCandidate
{
	//-------- attributes --------
	
	/** The plan name. */
	protected String	name;
	
	/** The plan parameters. */
	protected Map<String, Object> params;
	
	/** The mplan found in init(). */
	protected MPlan	mplan;
	
	//-------- constructors --------
	
	/**
	 *  Create a plan candidate for a newly instantiated plan
	 *  with given parameters.
	 *  @param name	The plan (method) name.
	 *  @param params	The parameters (if any).
	 */
	public PlanCandidate(String name, Map<String, Object> params)
	{
		this.name	= name;
		this.params	= params;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the parameters.
	 */
	public Map<String, Object>	getParameters()
	{
		return params;
	}

	//-------- internal methods --------
	
	/**
	 *  Initialize the plan candidate object.
	 */
	protected void init(RProcessableElement element, RCapability capa)
	{
		this.mplan	= ((MCapability)capa.getModelElement()).getPlan(name);
	}
	
	/**
	 *  Get the mplan.
	 */
	public MPlan	getMPlan()
	{
		return mplan;
	}
}
