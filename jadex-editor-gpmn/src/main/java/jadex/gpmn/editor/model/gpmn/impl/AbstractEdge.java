package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IEdge;
import jadex.gpmn.editor.model.gpmn.IElement;
import jadex.gpmn.editor.model.gpmn.IGpmnModel;


/**
 *  An edge in the model.
 *
 */
public abstract class AbstractEdge extends AbstractElement implements IEdge
{
	/**
	 *  The edge sources.
	 */
	protected AbstractElement source;
	
	/**
	 *  The edge sources.
	 */
	protected AbstractElement target;
	
	/**
	 *  Creates a new edge.
	 */
	protected AbstractEdge(IGpmnModel model)
	{
		super(model);
	}

	/**
	 *  Gets the source.
	 *
	 *  @return The source
	 */
	public IElement getSource()
	{
		return source;
	}

	/**
	 *  Sets the source.
	 *
	 *  @param source The source to set
	 */
	public void setSource(IElement source)
	{
		if (this.source != null)
		{
			this.source.removeSourceEdge(this);
		}
		((AbstractElement) source).addSourceEdge(this);
		
		this.source = (AbstractElement) source;
	}

	/**
	 *  Gets the target.
	 *
	 *  @return The target
	 */
	public IElement getTarget()
	{
		return target;
	}

	/**
	 *  Sets the target.
	 *
	 *  @param target The target to set
	 */
	public void setTarget(IElement target)
	{
		if (this.target != null)
		{
			this.target.removeTargetEdge(this);
		}
		((AbstractElement) target).addTargetEdge(this);
		
		this.target = (AbstractElement) target;
	}
}
