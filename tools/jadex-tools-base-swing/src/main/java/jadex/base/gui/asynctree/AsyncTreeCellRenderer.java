package jadex.base.gui.asynctree;

import java.awt.Component;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.tree.DefaultTreeCellRenderer;

import jadex.commons.gui.CombiIcon;


/**
 * Renderer for component tree cells.
 */
public class AsyncTreeCellRenderer extends DefaultTreeCellRenderer
{
	// -------- TreeCellRenderer interface --------

	/**
	 * Get the cell renderer for a node.
	 */
	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row,
			boolean hasFocus)
	{
		assert SwingUtilities.isEventDispatchThread();
		
		// Change icons depending on node type.
		ISwingTreeNode node = (ISwingTreeNode)value;
		Icon icon = node.getSwingIcon();
		String tooltip = node.getTooltipText();
		// Add overlays to icon (if any).
		if(tree.getModel() instanceof AsyncTreeModel)
		{
			List icons = null;
			INodeHandler[] handlers = ((AsyncSwingTreeModel)tree.getModel()).getNodeHandlers();
			for(int i = 0; handlers != null && i < handlers.length; i++)
			{
				Icon overlay = ((ISwingNodeHandler) handlers[i]).getSwingOverlay(node);
				if(overlay != null)
				{
					if(icons == null)
					{
						icons = new ArrayList();
						if(icon != null)
						{
							// Base icon.
							icons.add(icon);
						}
					}
					icons.add(overlay);
				}
			}
			if(icons != null)
			{
				icon = new CombiIcon((Icon[])icons.toArray(new Icon[icons
						.size()]));
			}
		}
		if(icon != null)
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
		if(tooltip!=null)
		{
			setToolTipText(tooltip);
		}
		
		JComponent comp = (JComponent)super.getTreeCellRendererComponent(tree,
			getLabel(node), selected, expanded, leaf, row, hasFocus);

		return comp;
	}

	/**
	 *  Overwritten to clear background behind icon too.
	 *  Required for semi-transparent icons.
	 */
	public void paint(Graphics g)
	{
		if(getBackground()!=null)
		{
			g.setColor(getBackground());
			g.fillRect(0, 0, getWidth(), getHeight());
		}
		super.paint(g);
	}
	
	/**
	 *  Get the label for a node.
	 *  May be overridden by sub classes.
	 */
	protected String	getLabel(ITreeNode node)
	{
		return node.toString();
	}
}
