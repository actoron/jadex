package jadex.tools.common.treecombo;

import jadex.tools.common.jtreetable.JTreeTable;

import java.awt.Component;

import javax.swing.ComboBoxModel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class JTreeTableComboModelRenderer	implements ComboBoxModel, ListCellRenderer
{
	JTreeTable	tree;
	Object	selected	= null;
	
	public JTreeTableComboModelRenderer(JTreeTable tree)
	{
		this.tree	= tree;
	}
	
	public void removeListDataListener(final ListDataListener l)
	{
		tree.getModel().addTableModelListener(new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				l.contentsChanged(new ListDataEvent(null, ListDataEvent.CONTENTS_CHANGED, 0, getSize()));
			}
		});
	}
	
	public int getSize()
	{
		return tree.getRowCount();
	}
	
	public Object getElementAt(int index)
	{
		return tree.getValueAt(index, 0);
	}
	
	public void addListDataListener(ListDataListener l)
	{
	}
	
	public void setSelectedItem(Object anItem)
	{
		selected	= anItem;
	}
	
	public Object getSelectedItem()
	{
		return selected;
	}

	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus)
	{
		if(index==-1)
			return tree.getCellRenderer(0, 0).getTableCellRendererComponent(tree, selected, isSelected, cellHasFocus, index, 0);
		else
			return tree.getCellRenderer(index, 0).getTableCellRendererComponent(tree, value, isSelected, cellHasFocus, index, 0);
			
	}
}
