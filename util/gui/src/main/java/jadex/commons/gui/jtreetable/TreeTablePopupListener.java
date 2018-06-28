package jadex.commons.gui.jtreetable;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.tree.TreePath;

/**
 *  A mouse listener to add popup-menus to tree tables.
 *  Uses the actions given by the type of the selected tree table node.
 */
public class TreeTablePopupListener	extends MouseAdapter
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
			JTreeTable	table	= (JTreeTable)e.getSource();
			TreePath	path	= table.getTree().getPathForLocation(e.getX(), e.getY());
			// Should also support clicks on other table columns???
			if(path!=null)
			{
				final DefaultTreeTableNode	node
					= (DefaultTreeTableNode)path.getLastPathComponent();
				
				// Select rows.
				int row	= table.rowAtPoint(e.getPoint());
				int[] selrows = table.getSelectedRows();
				boolean newsel=true;
				for(int i=0; newsel && i<selrows.length; i++)
				{
					if(selrows[i]==row)
						newsel = false;
				}
				if(newsel)
				{
					table.clearSelection();
					table.addRowSelectionInterval(row, row);
				}

				if(node.getType().getPopupActions().length>0)
				{					
					// Show menu.
					Action[]	actions	= node.getType().getPopupActions();
					JPopupMenu	menu	= new JPopupMenu("Actions");
					for(int i=0; i<actions.length; i++)
					{
						// Hack!!! how to get node when action should be performed?
						actions[i].putValue("node", node);
						if(actions[i] instanceof TreeTableAction)
						{
							JCheckBoxMenuItem	item	= new JCheckBoxMenuItem(actions[i]);
							item.setSelected(((TreeTableAction)actions[i]).isSelected());
							menu.add(item);
						}
						else
						{
							JMenuItem	item	= new JMenuItem(actions[i]);
							menu.add(item);
						}
					}
					if(actions.length>0)
						menu.show(table, e.getX(), e.getY());
				}
			}
		}
	}
}
