package jadex.wfms.simulation;

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MParameter;
import jadex.commons.ICacheableModel;
import jadex.commons.collection.TreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ModelTreeNode extends TreeNode
{
	private ModelTreeNode parent;
	
	public ModelTreeNode()
	{
		this(null);
	}
	
	public ModelTreeNode(Object data)
	{
		super(data);
		parent = null;
	}
	
	public void addChild(TreeNode child)
	{
		if (child instanceof ModelTreeNode)
			((ModelTreeNode) child).setParent(this);
		super.addChild(child);
	}
	
	public void setChildren(List children)
	{
		if (children == null)
			children = new ArrayList();
		for (Iterator it = children.iterator(); it.hasNext(); )
			((ModelTreeNode) it.next()).setParent(this);
		super.setChildren(children);
	}
	
	public void setParent(ModelTreeNode parent)
	{
		this.parent = parent;
	}
	
	public ModelTreeNode getParent()
	{
		return parent;
	}
	
	public String toString()
	{
		if (data instanceof ICacheableModel)
		{
			ICacheableModel model = ((ICacheableModel) data);
			return ClientMetaProcessModel.resolveProcessName(model);
		}
		else if (data instanceof MActivity)
			return ((MActivity) data).getName();
		else if (data instanceof MParameter)
			return ((MParameter) data).getName() + " (" + ((MParameter) data).getClazz().getSimpleName() + ")";
		else
			return String.valueOf(data);
	}
}
