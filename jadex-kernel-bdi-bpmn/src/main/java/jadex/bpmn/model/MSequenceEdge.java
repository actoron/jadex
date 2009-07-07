package jadex.bpmn.model;

import jadex.javaparser.IParsedExpression;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  A sequence edge is a control flow edge between activities.
 */
public class MSequenceEdge extends MAssociationTarget
{
	//-------- attributes --------
	
	/** The outgoing edges. */
	protected MActivity source;
	
	/** The incoming edges. */
	protected MActivity target;
	
	/** The type. */
	protected String type;
	
	/** The associations. */
	protected List associations;
	
	//-------- additions --------
	
	/** The condition. */
	protected IParsedExpression condition;
	
	/** The parameter mappings. */
	protected Map parametermappings;
	
	//-------- methods --------

	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public MActivity getSource()
	{
		return source;
	}
	
	/**
	 *  Set the source.
	 *  @return The source.
	 */
	public void setSource(MActivity source)
	{
		this.source = source;
	}
	
	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public MActivity getTarget()
	{
		return target;
	}
	
	/**
	 *  Set the target.
	 *  @param target The target.
	 */
	public void setTarget(MActivity target)
	{
		this.target = target;
	}

	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	//-------- additions --------
	
	/**
	 *  Set the condition.
	 *  @param condition The condition.
	 */
	public void setCondition(IParsedExpression condition)
	{
		this.condition = condition;
	}

	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public IParsedExpression getCondition()
	{
		return this.condition;
	}
	
	/**
	 *  Add a parameter mapping.
	 *  @param name The parameter name.
	 *  @param exp The expression.
	 */
	public void addParameterMapping(String name, IParsedExpression exp)
	{
		if(parametermappings == null)
			parametermappings = new HashMap();
		
		parametermappings.put(name, exp);
	}
	
	/**
	 *  Get the parameter mappings map.
	 *  @return The parameter mappings.
	 */
	public Map getParameterMappings()
	{
		return parametermappings;
	}
}
