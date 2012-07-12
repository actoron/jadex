package jadex.bpmn.model;

import jadex.javaparser.IParsedExpression;

import java.util.HashMap;
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
	
	/** The default flag. */
	protected boolean	def;
	
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

	/**
	 *  Get the default flag. 
	 *  @return The default flag.
	 */
	public boolean	isDefault()
	{
		return this.def;
	}

	/**
	 *  Set the default flag.
	 *  @param def The default flag.
	 */
	public void setDefault(boolean def)
	{
		this.def = def;
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
	 *  @param iexp The index expression, when setting a entry of an array parameter.
	 */
	public void addParameterMapping(String name, IParsedExpression exp, IParsedExpression iexp)
	{
		if(parametermappings == null)
			parametermappings = new HashMap();
		
		parametermappings.put(name, new Object[]{exp, iexp});
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
