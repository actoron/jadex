package jadex.base.gui.componenttree;

import java.util.List;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIDefaults;

import jadex.base.gui.asynctree.AbstractSwingTreeNode;
import jadex.base.gui.asynctree.AbstractTreeNode;
import jadex.base.gui.asynctree.AsyncSwingTreeModel;
import jadex.base.gui.asynctree.ISwingTreeNode;
import jadex.base.gui.asynctree.ITreeNode;
import jadex.commons.gui.SGUI;

/**
 *  The view tree node is used by the platform node to display 'proxy', 'application' and 'system' subfolders. 
 */
public class ViewTreeNode	extends AbstractSwingTreeNode
{
	//-------- constants --------
	
	/** The service container icon. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"System", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/system.png"),
		"Applications", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/bean2.png"),
		"Platforms", SGUI.makeIcon(ServiceContainerNode.class, "/jadex/base/gui/images/cloud3.png"),
	});
	
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	//-------- constructors --------
	
	/**
	 *  Create a new service container node.
	 */
	public ViewTreeNode(String name, ISwingTreeNode parent, AsyncSwingTreeModel model, JTree tree, List<? extends ITreeNode> children)
	{
		super(parent, model, tree);
//		System.out.println("create view node: "+name+" "+parent);
		this.name = name;
		setChildren(children);
		model.registerNode(this);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the id used for lookup.
	 */
	public Object	getId()
	{
		return getParent().getId()+name;
	}
	
	/**
	 *  Get the icon as byte[] for a node.
	 */
	public byte[] getIcon()
	{
		return null;
	}

	/**
	 *  Get the icon for a node.
	 */
	public Icon	getSwingIcon()
	{
		Icon	ret	= icons.getIcon(name);
		return ret;
	}

	/**
	 *  Asynchronously search for children.
	 *  Called once for each node.
	 *  Should call setChildren() once children are found.
	 */
	protected void	searchChildren()
	{
		// Done by parent node.
	}
	
	/**
	 *  A string representation.
	 */
	public String toString()
	{
		return name;
	}
	
	/**
	 *  Get tooltip text.
	 */
	public String getTooltipText()
	{
		return null;
	}
	
	/**
	 *  Made public.
	 */
	public void setChildren(List<? extends ITreeNode> newchildren)
	{
		super.setChildren(newchildren);
	}
	
	/**
	 *  Overridden for alphabetical insert.
	 */
	public void addChild(ITreeNode node)
	{
		boolean ins = false;
		for(int i=0; i<getChildCount() && !ins; i++)
		{
			ISwingTreeNode child = getChild(i);
			if(child.toString().toLowerCase().compareTo(node.toString().toLowerCase())>=0)
			{
				super.addChild(i, node);
				ins = true;
			}
		}
		if(!ins)
		{
			super.addChild(node);
		}
//		System.out.println("cc: "+getCachedChildren().size());
	}
	
	public void removeChild(ITreeNode node)
	{
		super.removeChild(node);
//		System.out.println("cc: "+getCachedChildren().size());
		if(getCachedChildren().size()==0)
		{
			((AbstractTreeNode)getParent()).removeChild(this);
		}
	}
}
