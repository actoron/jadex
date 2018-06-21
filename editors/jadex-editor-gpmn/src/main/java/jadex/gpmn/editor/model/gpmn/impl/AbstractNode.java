package jadex.gpmn.editor.model.gpmn.impl;

import jadex.gpmn.editor.model.gpmn.IGpmnModel;

/**
 *  A node in the GPMN model.
 *
 */
public abstract class AbstractNode extends AbstractElement
{
	
	
	protected AbstractNode(IGpmnModel model)
	{
		super(model);
	}
	
	/**
	 *  Prints the name of the node.
	 */
	public String toString()
	{
		return name;
	}
}
