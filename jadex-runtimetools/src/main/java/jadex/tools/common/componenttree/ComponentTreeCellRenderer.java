package jadex.tools.common.componenttree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 *  Renderer for component tree cells.
 */
public class ComponentTreeCellRenderer	extends DefaultTreeCellRenderer
{
	//-------- constructors --------
	
	/**
	 *  Create a new component tree cell renderer.
	 */
	public ComponentTreeCellRenderer()
	{
	}
	
	//-------- TreeCellRenderer interface --------
	
	/**
	 *  Get the cell renderer for a node.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		// Change icons depending on node type.
		IComponentTreeNode	node	= (IComponentTreeNode)value;
		Icon	icon	= node.getIcon();
		if(icon!=null)
		{
			setOpenIcon(icon);
			setClosedIcon(icon);
			setLeafIcon(icon);
		}
		else
		{
			setOpenIcon(getDefaultOpenIcon());
			setClosedIcon(getDefaultClosedIcon());
			setLeafIcon(getDefaultLeafIcon());
		}

		JComponent	comp	= (JComponent)super.getTreeCellRendererComponent(
			tree, value, selected, expanded, leaf, row, hasFocus);
		
		return comp;
	}

}
