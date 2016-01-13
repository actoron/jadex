package jadex.commons.gui.jtreetable;


import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;



/**
 *  A custom tree cell renderer for changing icons in the tree.
 */
public class DefaultTreeTableCellRenderer	extends DefaultTreeCellRenderer
{
	//-------- overridings --------

	/**
	 *  Configures the renderer based on the passed in components.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		// Change icons depending on node type.
		DefaultTreeTableNode	node	= (DefaultTreeTableNode)value;
		if(node.getType()!=null)
		{
			// Should support different icons for open/closed/leaf???
			Icon	icon	= node.getType().getIcon(value);
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
		}

		JComponent	comp	= (JComponent)super.getTreeCellRendererComponent(
			tree, value, sel, expanded, leaf, row, hasFocus);

		return comp;
	}
}