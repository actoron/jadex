package jadex.wfms.guicomponents;


import jadex.commons.SUtil;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class StringTable extends JPanel
{
	//public static final int NO_BUTTONS = 0;
	public static final int TEXT_BUTTONS = 1;
	public static final int ICON_BUTTONS = 2;
	
	private JTable stringTable;
	
	private JPanel buttonPanel;
	
	private TableModelListener modelListener;
	
	public StringTable(AbstractStringTableModel model, int buttons)
	{
		super(new GridBagLayout());
		
		stringTable = new JTable(model);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		JScrollPane tablePane = new JScrollPane(stringTable);
		add(tablePane, g);
		
		if (buttons == ICON_BUTTONS)
		{
			buttonPanel = new ButtonPanel(BoxLayout.PAGE_AXIS);
		}
		else
			buttonPanel = new ButtonPanel();
		
		JButton addButton = new JButton(new AbstractAction("Add")
		{
			public void actionPerformed(ActionEvent e)
			{
				((AbstractStringTableModel) stringTable.getModel()).addString("");
			}
		});
		buttonPanel.add(addButton);
		
		JButton removeButton = new JButton(new AbstractAction("Remove")
		{
			public void actionPerformed(ActionEvent e)
			{
				int[] rows = stringTable.getSelectedRows();
				Arrays.sort(rows);
				for (int i = 0; i < rows.length; ++i)
					((AbstractStringTableModel) stringTable.getModel()).removeString(rows[i] - i);
			}
		});
		
		g = new GridBagConstraints();
		if (buttons == ICON_BUTTONS)
		{
			addButton.setIcon(new ImageIcon(SUtil.getResourceInfo0("jadex/tools/common/images/add_small.png", getClass().getClassLoader()).getFilename()));
			removeButton.setIcon(new ImageIcon(SUtil.getResourceInfo0("jadex/tools/common/images/delete_small.png", getClass().getClassLoader()).getFilename()));
			addButton.setText("");
			removeButton.setText("");
			
			g.gridx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.VERTICAL;
			add(buttonPanel, g);
		}
		else
		{
			g.gridy = 1;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			add(buttonPanel, g);
		}
		
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		
		if (!model.isEditable())
			buttonPanel.setVisible(false);
		
		modelListener = new TableModelListener()
		{
			public void tableChanged(TableModelEvent e)
			{
				if (((AbstractStringTableModel) stringTable.getModel()).isEditable())
					buttonPanel.setVisible(true);
				else
					buttonPanel.setVisible(false);
			}
		};
		model.addTableModelListener(modelListener);
	}
	
	public TableModel getModel()
	{
		return stringTable.getModel();
	}
	
	public void setModel(TableModel model)
	{
		stringTable.setModel(model);
	}
	
	public abstract static class AbstractStringTableModel extends AbstractTableModel
	{
		/**
		 *  Adds a string.
		 *  @param string the string
		 */
		public abstract void addString(String string);
		
		/**
		 *  Removes a string.
		 *  @param index index of the string
		 */
		public abstract void removeString(int index);
		
		/**
		 * Returns whether the model can be edited.
		 * @return true if it can be edited
		 */
		public abstract boolean isEditable();
	}
	
	public static class DefaultStringTableModel extends AbstractStringTableModel
	{
		private String title;
		
		private ArrayList strings;
		
		private boolean editable;
		
		public DefaultStringTableModel(String title, boolean editable)
		{
			this.title = title;
			strings = new ArrayList();
			this.editable = editable;			
		}
		
		public void addString(String string)
		{
			strings.add(string);
			fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
		}
		
		public void removeString(int index)
		{
			strings.remove(index);
			fireTableRowsDeleted(getRowCount(), getRowCount());
		}
		
		public Object getValueAt(int rowIndex, int columnIndex)
		{
			return strings.get(rowIndex);
		}
		
		public void setValueAt(Object aValue, int rowIndex, int columnIndex)
		{
			strings.set(rowIndex, aValue);
		}
		
		public int getRowCount()
		{
			return strings.size();
		}
		
		public int getColumnCount()
		{
			return 1;
		}
		
		public boolean isCellEditable(int row, int column)
		{
			return editable;
		}
		
		public String getColumnName(int column)
		{
			return title;
		}
		
		public boolean isEditable()
		{
			return editable;
		}
		
		public void setEditable(boolean editable)
		{
			this.editable = editable;
		}
		
		public List getStrings()
		{
			return strings;
		}
		
		public String[] getStringsAsArray()
		{
			return (String[]) strings.toArray(new String[strings.size()]);
		}
		
		public void setStrings(List strings)
		{
			this.strings = new ArrayList(strings);
			fireTableStructureChanged();
		}
	}
	
	protected void finalize() throws Throwable
	{
		super.finalize();
		stringTable.getModel().removeTableModelListener(modelListener);
	}
}
