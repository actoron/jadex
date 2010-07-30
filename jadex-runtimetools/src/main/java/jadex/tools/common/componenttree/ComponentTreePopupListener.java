package jadex.tools.common.componenttree;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Action;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTree;
import javax.swing.tree.TreePath;

/**
 *  A mouse listener to add popup-menus to a component tree.
 *  Uses the actions given by the selected nodes.
 */
public class ComponentTreePopupListener	extends MouseAdapter
{
	// Is only mouseReleased a popup trigger???
	public void	mousePressed(MouseEvent e)	{doPopup(e);}
	public void	mouseReleased(MouseEvent e)	{doPopup(e);}
	public void	mouseClicked(MouseEvent e)	{doPopup(e);}

	/** Open a popup menu. */
	protected void doPopup(final MouseEvent e)
	{
		if(e.isPopupTrigger())
		{
			JTree	tree	= (JTree)e.getSource();
			ComponentTreeModel	model	= (ComponentTreeModel)tree.getModel();
			int row	= tree.getRowForLocation(e.getX(), e.getY());
			if(row!=-1)
			{
				// Check if popup is on selected items or a new item.
				int[]	selrows	= tree.getSelectionRows();
				boolean newsel=true;
				for(int i=0; newsel && selrows!=null && i<selrows.length; i++)
				{
					if(selrows[i]==row)
						newsel = false;
				}
				if(newsel)
				{
					tree.clearSelection();
					tree.addSelectionRow(row);
				}

				TreePath[]	paths	= tree.getSelectionPaths();
				IComponentTreeNode[]	nodes	= new IComponentTreeNode[paths.length];
				for(int i=0; i<nodes.length; i++)
					nodes[i]	= (IComponentTreeNode)paths[i].getLastPathComponent();
				
				INodeHandler[]	handlers	= model.getNodeHandlers();
				List	actions	= null;
				for(int i=0; i<handlers.length; i++)
				{
					Action[]	acts	= handlers[i].getPopupActions(nodes);
					if(acts!=null && acts.length>0)
					{
						if(actions==null)
						{
							actions	= new ArrayList();
						}
						actions.addAll(Arrays.asList(acts));
					}
				}
				
				if(actions!=null)
				{					
					// Show menu.
					JPopupMenu	menu	= new JPopupMenu("Actions");
					for(int i=0; i<actions.size(); i++)
					{
						JMenuItem	item	= new JMenuItem((Action)actions.get(i));
						menu.add(item);
					}
					Point	loc	= tree.getPopupLocation(e);
					if(loc==null)
						loc	= e.getPoint();
					menu.show(tree, loc.x, loc.y);
				}
			}
		}
	}
}
