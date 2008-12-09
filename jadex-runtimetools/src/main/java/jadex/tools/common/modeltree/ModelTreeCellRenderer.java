package jadex.tools.common.modeltree;


import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * ModelTreeCellRenderer
 */
public class ModelTreeCellRenderer extends DefaultTreeCellRenderer
{
	//-------- attributes --------
	
	/** The node functionality. */
	protected DefaultNodeFunctionality	nof;
	
	//-------- constructors --------
	
	/**
	 *  Create a model tree cell renderer.
	 */
	public ModelTreeCellRenderer(DefaultNodeFunctionality nof)
	{
		this.nof	= nof;
	}
	
	//-------- methods --------

	/**
	 * @param tree
	 * @param value
	 * @param selected
	 * @param expanded
	 * @param leaf
	 * @param row
	 * @param hasFocus
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded,
			boolean leaf, int row, boolean hasFocus)
	{
		Icon icon	= null;
		if(value instanceof IExplorerTreeNode) 
		{
			IExplorerTreeNode	node	= (IExplorerTreeNode)value;
			icon = nof.getIcon(node);
			setToolTipText(node.getToolTipText());
		}
		else
		{
			setToolTipText(value.toString());
		}
		
		if(icon!=null)
		{
			setOpenIcon(icon);			
			setClosedIcon(icon);			
			setLeafIcon(icon);		
		}
		else
		{ // Fallback for unknown elements, should never be called.
			setOpenIcon(UIManager.getIcon("Tree.openIcon"));			
			setClosedIcon(UIManager.getIcon("Tree.closedIcon"));			
			setLeafIcon(UIManager.getIcon("Tree.leafIcon"));			
		}
		
		return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
	}

}
