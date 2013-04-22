package jadex.bpmn.model;

import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.Tuple2;

/**
 *  A data edge is a data flow edge between activity parameters.
 */
public class MDataEdge extends MNamedIdElement
{
	//-------- attributes --------
	
	/** The source activity. */
	protected MActivity source;

	/** The source parameter. */
	protected String sourceparam;
	
	/** The target activity. */
	protected MActivity target;
	
	/** The target parameter. */
	protected String targetparam;
	
	/** The parameter mappings. */
	protected Tuple2<UnparsedExpression, UnparsedExpression> parametermapping;
	
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
	 *  Get the sourceparam.
	 *  @return The sourceparam.
	 */
	public String getSourceParameter()
	{
		return sourceparam;
	}

	/**
	 *  Set the sourceparam.
	 *  @param sourceparam The sourceparam to set.
	 */
	public void setSourceParameter(String sourceparam)
	{
		this.sourceparam = sourceparam;
	}

	/**
	 *  Get the targetparam.
	 *  @return The targetparam.
	 */
	public String getTargetParameter()
	{
		return targetparam;
	}

	/**
	 *  Set the targetparam.
	 *  @param targetparam The targetparam to set.
	 */
	public void setTargetParameter(String targetparam)
	{
		this.targetparam = targetparam;
	}

	/**
	 *  Get the parametermapping.
	 *  @return The parametermapping.
	 */
	public Tuple2<UnparsedExpression, UnparsedExpression> getParameterMapping()
	{
		return parametermapping;
	}

	/**
	 *  Set the parameter mapping.
	 */
	public void setParameterMapping(UnparsedExpression valmap, UnparsedExpression indexmap)
	{
		this.parametermapping = new Tuple2<UnparsedExpression, UnparsedExpression>(valmap, indexmap);
	}
	
}
