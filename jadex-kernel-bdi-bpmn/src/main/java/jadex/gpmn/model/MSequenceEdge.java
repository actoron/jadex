package jadex.gpmn.model;

import jadex.bpmn.model.MNamedIdElement;

import java.util.List;

/**
 *  A sequence edge is a control flow edge between activities.
 */
public class MSequenceEdge extends MNamedIdElement//MAssociationTarget
{
	//-------- attributes --------
	
	/** The outgoing edges. */
	protected MProcessElement source;
	
	/** The incoming edges. */
	protected MProcessElement target;
	
	/** The type. */
	protected String type;
	
	/** The associations. */
	protected List associations;
	
	//-------- methods --------

	/**
	 *  Get the source.
	 *  @return The source.
	 */
	public MProcessElement getSource()
	{
		return source;
	}
	
	/**
	 *  Set the source.
	 *  @return The source.
	 */
	public void setSource(MProcessElement source)
	{
		this.source = source;
	}
	
	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public MProcessElement getTarget()
	{
		return target;
	}
	
	/**
	 *  Set the target.
	 *  @param target The target.
	 */
	public void setTarget(MProcessElement target)
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

}
