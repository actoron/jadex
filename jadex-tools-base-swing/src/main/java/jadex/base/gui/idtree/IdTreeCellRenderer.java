package jadex.base.gui.idtree;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

/**
 * 
 */
public class IdTreeCellRenderer extends DefaultTreeCellRenderer
{
	/**
	 * 
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
		boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		// Change icons depending on node type.
		IdTreeNode<?> node = (IdTreeNode<?>)value;
		Icon icon = node.getIcon();
		String tooltip = node.getTooltipText();
		
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
		
//		System.out.println("tooltip: "+tooltip);
		if(tooltip!=null)
		{
			setToolTipText(tooltip);
		}
		
		JComponent comp = (JComponent)super.getTreeCellRendererComponent(tree,
			node.toString(), selected, expanded, leaf, row, hasFocus);

		return comp;
	}
}