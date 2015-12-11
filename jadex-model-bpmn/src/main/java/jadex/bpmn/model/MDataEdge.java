package jadex.bpmn.model;

import jadex.bridge.modelinfo.UnparsedExpression;

/**
 *  A data edge is a data flow edge between activity parameters.
 */
public class MDataEdge extends MEdge
{
	//-------- attributes --------

	/** The source parameter. */
	protected String sourceparam;
	
	/** The target parameter. */
	protected String targetparam;
	
	/** The parameter mappings. */
	protected UnparsedExpression parametermapping;
	
	//-------- methods --------

	/**
	 *  Helper method connecting two activity parameters using this edge.
	 *  The previous connection is removed.
	 *  
	 *  @param source The new source activity.
	 *  @param sourceparam The new source activity parameter.
	 *  @param target The new target activity.
	 *  @param targetparam The new target activity parameter.
	 */
	public void connect(MActivity source, String sourceparam,
						 MActivity target, String targetparam)
	{
		if (this.source != null)
		{
			this.source.removeOutgoingDataEdge(this);
		}
		
		if (this.target != null)
		{
			this.target.removeIncomingDataEdge(this);
		}
		
		this.source = source;
		this.target = target;
		
		setSourceParameter(sourceparam);
		setTargetParameter(targetparam);
		source.addOutgoingDataEdge(this);
		target.addIncomingDataEdge(this);
	}
	
	/**
	 *  Helper method disconnecting this edge.
	 */
	public void disconnect()
	{
		if (this.source != null)
		{
			this.source.removeOutgoingDataEdge(this);
		}
		
		if (this.target != null)
		{
			this.target.removeIncomingDataEdge(this);
		}
		
		this.source = null;
		this.target = null;
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
	public UnparsedExpression getParameterMapping()
	{
		return parametermapping;
	}

	/**
	 *  Set the parameter mapping.
	 */
	public void setParameterMapping(UnparsedExpression valmap)
	{
		this.parametermapping = valmap;
	}
	
}
