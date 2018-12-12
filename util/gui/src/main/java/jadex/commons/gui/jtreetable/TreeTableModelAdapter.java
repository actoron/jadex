package jadex.commons.gui.jtreetable;

/*
 * Copyright 1997-1999 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or
 * without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer. 
 *   
 * - Redistribution in binary form must reproduce the above
 *   copyright notice, this list of conditions and the following
 *   disclaimer in the documentation and/or other materials
 *   provided with the distribution. 
 *   
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.  
 * 
 * This software is provided "AS IS," without a warranty of any
 * kind. ALL EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND
 * WARRANTIES, INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY
 * EXCLUDED. SUN AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY
 * DAMAGES OR LIABILITIES SUFFERED BY LICENSEE AS A RESULT OF OR
 * RELATING TO USE, MODIFICATION OR DISTRIBUTION OF THIS SOFTWARE OR
 * ITS DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE 
 * FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT,   
 * SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER  
 * CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF 
 * THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF SUN HAS 
 * BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed, licensed or
 * intended for use in the design, construction, operation or
 * maintenance of any nuclear facility.
 */

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements 
 * the table model interface. The implementation is trivial, with 
 * all of the event dispatching support provided by the superclass: 
 * the AbstractTableModel. 
 *
 * @version 1.2 10/27/98
 *
 * @author Philip Milne
 * @author Scott Violet
 */
public class TreeTableModelAdapter extends AbstractTableModel
{
    JTree tree;
    TreeTableModel treeTableModel;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;

		tree.addTreeExpansionListener(new TreeExpansionListener()
		{
			public void treeExpanded(TreeExpansionEvent event)
			{
	   			int row	= TreeTableModelAdapter.this.tree.getRowForPath(event.getPath());
	   			if(row!=-1)
	   			{
	   				fireTableRowsUpdated(row, row);
	   				if(event.getPath().getLastPathComponent() instanceof TreeTableNode)
	   				{
	   					int	childcount	= ((TreeTableNode)event.getPath().getLastPathComponent()).getChildCount();
	   					if(childcount>0)
	   					{
	   						fireTableRowsInserted(row+1, row+1+childcount);
	   					}
	   				}
	   				else
	   				{
	   					// No way to figure out what happened!?
	   					fireTableDataChanged();
	   				}
	   			}
			}
	
			public void treeCollapsed(TreeExpansionEvent event)
			{
	   			int row	= TreeTableModelAdapter.this.tree.getRowForPath(event.getPath());
	   			if(row!=-1)
	   			{
	   				fireTableRowsUpdated(row, row);
	   				if(event.getPath().getLastPathComponent() instanceof TreeTableNode)
	   				{
	   					int	childcount	= ((TreeTableNode)event.getPath().getLastPathComponent()).getChildCount();
	   					if(childcount>0)
	   					{
	   						fireTableRowsDeleted(row+1, row+1+childcount);
	   					}
	   				}
	   				else
	   				{
	   					// No way to figure out what happened!?
	   					fireTableDataChanged();
	   				}
	   			}
			}
		});
	
		// Installs a TreeModelListener that can update the table when
		// the tree changes. We use delayedFireTableDataChanged as we can
		// not be guaranteed the tree will have finished processing
		// the event before us.
		treeTableModel.addTreeModelListener(new TreeModelListener()
		{
		    public void treeNodesChanged(final TreeModelEvent e)
		    {
		    	SwingUtilities.invokeLater(new Runnable()
		    	{
		    		public void	run()
		    		{
		    			int row	= TreeTableModelAdapter.this.tree.getRowForPath(new TreePath(e.getPath()));
		    			if(row!=-1 && TreeTableModelAdapter.this.tree.isExpanded(row))
		    			{
			    			int[] rows	= e.getChildIndices();
			    			fireTableRowsUpdated(row+1+rows[0], row+1+rows[rows.length-1]);
		    			}
		    		}
		    	});
		    }
	
		    public void treeNodesInserted(final TreeModelEvent e)
		    {
		    	SwingUtilities.invokeLater(new Runnable()
		    	{
		    		public void	run()
		    		{
		    			int row	= TreeTableModelAdapter.this.tree.getRowForPath(new TreePath(e.getPath()));
		    			if(row!=-1 && TreeTableModelAdapter.this.tree.isExpanded(row))
		    			{
			    			fireTableRowsUpdated(row, row);
		    				int[] rows	= e.getChildIndices();
		    				fireTableRowsInserted(row+1+rows[0], row+1+rows[rows.length-1]);
		    			}
		    		}
		    	});
		    }
	
		    public void treeNodesRemoved(final TreeModelEvent e)
		    {
		    	SwingUtilities.invokeLater(new Runnable()
		    	{
		    		public void	run()
		    		{
		    			int row	= TreeTableModelAdapter.this.tree.getRowForPath(new TreePath(e.getPath()));
		    			if(row!=-1 && TreeTableModelAdapter.this.tree.isExpanded(row))
		    			{
			    			fireTableRowsUpdated(row, row);
		    				int[] rows	= e.getChildIndices();
		    				fireTableRowsDeleted(row+1+rows[0], row+1+rows[rows.length-1]);
		    			}
		    		}
		    	});
		    }
	
		    public void treeStructureChanged(final TreeModelEvent e)
		    {
		    	SwingUtilities.invokeLater(new Runnable()
		    	{
		    		public void	run()
		    		{
		    			fireTableStructureChanged();
		    		}
		    	});
		    }
		});
    }

    // Wrappers, implementing TableModel interface. 

    public int getColumnCount() {
	return treeTableModel.getColumnCount();
    }

    public String getColumnName(int column) {
	return treeTableModel.getColumnName(column);
    }

    public Class getColumnClass(int column) {
	return treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
	return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
	TreePath treePath = tree.getPathForRow(row);
	return treePath.getLastPathComponent();         
    }

    public Object getValueAt(int row, int column) {
	return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column) {
         return treeTableModel.isCellEditable(nodeForRow(row), column); 
    }

    public void setValueAt(Object value, int row, int column) {
	treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
}


