package jadex.base.gui.asynctree;

import java.awt.Point;
import java.awt.event.ActionEvent;
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
public class TreePopupListener	extends MouseAdapter
{
	// Is only mouseReleased a popup trigger???
	public void	mousePressed(MouseEvent e)	{doPopup(e);}
	public void	mouseReleased(MouseEvent e)	{doPopup(e);}
	public void	mouseClicked(MouseEvent e)
	{
		if(!doPopup(e) && e.getClickCount()==2)
		{
			JTree	tree	= (JTree)e.getSource();
			TreePath[]	paths	= tree.getSelectionPaths();
			if(paths!=null)
			{
				AsyncSwingTreeModel	model	= (AsyncSwingTreeModel)tree.getModel();
				INodeHandler[]	handlers	= model.getNodeHandlers();
				if(handlers!=null)
				{
					for(int i=0; paths!=null && i<paths.length; i++)
					{
						Action	a	= null;
						for(int j=handlers.length-1; a==null && j>=0; j--)
						{
							a	= ((ISwingNodeHandler) handlers[j]).getDefaultAction((ISwingTreeNode)paths[i].getLastPathComponent());
						}
						if(a!=null)
						{
							a.actionPerformed(new ActionEvent(tree, 0, null));
						}
					}
				}
			}
		}
	}

	/**
	 *  Open a popup menu.
	 */
	protected boolean	doPopup(final MouseEvent e)
	{
		boolean	ret	= false;
		if(e.isPopupTrigger())
		{
			JTree	tree	= (JTree)e.getSource();
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
				if(paths!=null)
				{
					ISwingTreeNode[]	nodes	= new ISwingTreeNode[paths.length];
					for(int i=0; i<nodes.length; i++)
						nodes[i]	= (ISwingTreeNode)paths[i].getLastPathComponent();
					
					AsyncSwingTreeModel	model	= (AsyncSwingTreeModel)tree.getModel();
					INodeHandler[]	handlers	= model.getNodeHandlers();
					if(handlers!=null)
					{
						List	actions	= null;
						for(int i=handlers.length-1; i>=0; i--)
						{
							Action[]	acts	= ((ISwingNodeHandler) handlers[i]).getPopupActions(nodes);
							if(acts!=null && acts.length>0)
							{
								if(actions==null)
								{
									actions	= new ArrayList();
								}
								if(!actions.isEmpty() && !(actions.get(actions.size()-1) instanceof JPopupMenu.Separator))
								{
									actions.add(new JPopupMenu.Separator());
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
								if(actions.get(i) instanceof JPopupMenu.Separator)
								{
									menu.add((JPopupMenu.Separator)actions.get(i));
								}
								else
								{
									JMenuItem	item	= new JMenuItem((Action)actions.get(i));
									menu.add(item);
								}
							}
							Point	loc	= tree.getPopupLocation(e);
							if(loc==null)
								loc	= e.getPoint();
							menu.show(tree, loc.x, loc.y);
							ret	= true;
						}
					}
				}
			}
		}
		return	ret;
	}
}
