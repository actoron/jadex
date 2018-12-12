package jadex.bpmn.model;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;
import jadex.commons.collection.IndexMap;
import jadex.javaparser.IParsedExpression;

/**
 *  A sequence edge is a control flow edge between activities.
 */
public class MSequenceEdge extends MEdge
{
	//-------- attributes --------
	
	/** The type. */
	protected String type;
	
	/** The default flag. */
	protected boolean	def;
	
	//-------- additions --------
	
	/** The condition. */
	protected UnparsedExpression condition; //IParameterExpression
	
	/** The parameter mappings. */
	protected IndexMap<String, Tuple2<UnparsedExpression, UnparsedExpression>> parametermappings;
//	protected Map<String, Tuple2<UnparsedExpression, UnparsedExpression>> parametermappings;
	
	//-------- methods --------
	
	/**
	 *  Helper method connecting two activities using this edge.
	 *  The previous connection is removed.
	 * 
	 *  @param source New source of the edge.
	 *  @param target New target of the edge.
	 */
	public void connect(MActivity source, MActivity target)
	{
		if (this.source != null)
		{
			this.source.removeOutgoingSequenceEdge(this);
		}
		
		if (this.target != null)
		{
			this.target.removeIncomingSequenceEdge(this);
		}
		
		this.source = source;
		this.target = target;
		
		source.addOutgoingSequenceEdge(this);
		target.addIncomingSequenceEdge(this);
	}
	
	/**
	 *  Helper method disconnecting this edge.
	 */
	public void disconnect()
	{
		if (this.source != null)
		{
			this.source.removeOutgoingSequenceEdge(this);
		}
		
		if (this.target != null)
		{
			this.target.removeIncomingSequenceEdge(this);
		}
		
		this.source = null;
		this.target = null;
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
	public void setCondition(UnparsedExpression condition)
	{
		this.condition = condition;
	}

	/**
	 *  Get the condition.
	 *  @return The condition.
	 */
	public UnparsedExpression getCondition()
	{
		return this.condition;
	}
	
	/**
	 *  Get the parsed condition.
	 *  @return The parsed condition.
	 */
	public IParsedExpression getParsedCondition()
	{
		return condition != null? (IParsedExpression)condition.getParsed() : null;
	}
	
	/**
	 *  Add a parameter mapping.
	 *  @param name The parameter name.
	 *  @param exp The expression.
	 *  @param iexp The index expression, when setting a entry of an array parameter.
	 */
	public void addParameterMapping(String name, UnparsedExpression exp, UnparsedExpression iexp)
	{
		if(parametermappings == null)
//			parametermappings = new LinkedHashMap<String, Tuple2<UnparsedExpression,UnparsedExpression>>();
			parametermappings = new IndexMap<String, Tuple2<UnparsedExpression, UnparsedExpression>>();
		
		parametermappings.put(name, new Tuple2<UnparsedExpression, UnparsedExpression>(exp, iexp));
	}
	
	/**
	 *  Get the parameter mappings map.
	 *  @return The parameter mappings.
	 */
	public IndexMap<String, Tuple2<UnparsedExpression, UnparsedExpression>> getParameterMappings()
	{
		return parametermappings;
	}
}
