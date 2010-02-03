package jadex.gpmn.model;

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
	
	/** The sequential order */
	protected Integer sequentialorder;
	
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

	/**
	 *  Get the sequentialorder.
	 *  @return The sequentialorder.
	 */
	public Integer getSequentialOrder()
	{
		return sequentialorder;
	}

	/**
	 *  Set the sequentialorder.
	 *  @param sequentialorder The sequentialorder to set.
	 */
	public void setSequentialOrder(Integer sequentialorder)
	{
		this.sequentialorder = sequentialorder;
	}
	
	
}
