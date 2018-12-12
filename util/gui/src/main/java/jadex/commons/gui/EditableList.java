package jadex.commons.gui;

import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.UIDefaults;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

import jadex.commons.SUtil;

/**
 *  A editable list with x / + symbols at each row.
 */
public class EditableList extends JTable
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"add", SGUI.makeIcon(EditableList.class,	"/jadex/commons/gui/images/add_small.png"),
		"delete", SGUI.makeIcon(EditableList.class,	"/jadex/commons/gui/images/delete_small.png"),
	});

	//-------- attributes --------

	/** The editable flag. */
	protected boolean editable;

	/** The entries. */
	protected java.util.List entries;
	
	/** Allow duplicate entries. */
	protected boolean allowduplicates;
	
	/** The list title. */
	protected String title;
	
	/** Show the numbers of entries in title. */
	protected boolean showcnt;

	//-------- constructors --------

	/**
	 *  Create a new editable list.
	 */
	public EditableList(final String title)
	{
		this(title, false);
	}
	
	/**
	 *  Create a new editable list.
	 */
	public EditableList(final String title, boolean showcnt)
	{
		this.title = title;
		this.entries = new ArrayList();
		this.editable	= true;
		this.allowduplicates = false;
		this.showcnt = showcnt;

		setModel(new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return editable ? 2 : 1;
			}

			public String getColumnName(int column)
			{
				return editable && column==0 ? " " : getTitle();
			}

			public Class getColumnClass(int columnIndex)
			{
				return editable && columnIndex==0 ? JButton.class : String.class;
			}

			public int getRowCount()
			{
				return getEntries().length + (editable ? 1 : 0);
			}

			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if((!editable || columnIndex!=0) && rowIndex<getEntries().length)
				{
					return getEntries()[rowIndex];
				}
				else
				{
					return "";
				}
			}

			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return editable && (columnIndex==0 || rowIndex!=getEntries().length);
			}

			public void setValueAt(Object newadr, int rowIndex, int columnIndex)
			{
				if(columnIndex==1)
				{
					while(rowIndex>=entries.size())
						entries.add("");
					entries.set(rowIndex, (String)newadr);
					fireTableCellUpdated(rowIndex, columnIndex);
				}
			}
		});

		// Hack!!! Set header preferred size and afterwards set title text to "" (bug in JDK1.5).
		getTableHeader().setPreferredSize(getTableHeader().getPreferredSize());
		getColumnModel().getColumn(0).setHeaderValue("");

		// Hack!!! Stupid JTable behaviour, see bug #4709394.
		putClientProperty("terminateEditOnFocusLost", Boolean.TRUE);

		setDefaultRenderer(JButton.class, new ButtonCellManager());
		setDefaultEditor(JButton.class, new ButtonCellManager());
		JButton	but	= new JButton(icons.getIcon("delete"));
		but.setMargin(new Insets(0,0,0,0));
		getColumnModel().getColumn(0).setMaxWidth(but.getPreferredSize().width);
		//teststable.setPreferredScrollableViewportSize(new Dimension(tfname.getPreferredSize().width, tfname.getPreferredSize().height*3));

		// Add resizable header.
		/*ResizeableTableHeader header = new ResizeableTableHeader();
		header.setColumnModel(getColumnModel());
		header.setAutoResizingEnabled(true); //default
		header.setIncludeHeaderWidth(false); //default
		setTableHeader(header);
		setAutoResizeMode(JTable.AUTO_RESIZE_OFF);*/
	}

	/**
	 *  Test if duplicates are allowed. 
	 *  @return the allowduplicates.
	 */
	public boolean isAllowDuplicates()
	{
		return allowduplicates;
	}

	/**
	 *  Set if duplicates are allowed.
	 *  @param allowduplicates the allowduplicates to set.
	 */
	public void setAllowDuplicates(boolean allowduplicates)
	{
		this.allowduplicates = allowduplicates;
		if(!allowduplicates)
			removeDuplicates();
	}
	
	/**
	 *  Remove all duplicates from the list.
	 */
	protected void removeDuplicates()
	{
		for(int i=0; i<entries.size(); i++)
		{
			Object tmp = entries.get(i);
			
			int last = entries.lastIndexOf(tmp);
			while(i<last)
			{
				entries.remove(last);
				last = entries.lastIndexOf(tmp);
			}
		}
		refresh();
	}

	/**
	 *  Get the entries.
	 *  @return The entries.
	 */
	public String[] getEntries()
	{
		return (String[])entries.toArray(new String[entries.size()]);
	}
	
	/**
	 *  Get the size.
	 *  @return The size.
	 */
	public int getEntryCount()
	{
		return entries.size();
	}

	/**
	 *  Set the entries.
	 *  @param entries The entries.
	 */
	public void setEntries(String[] entries)
	{
		this.entries = entries!=null ? SUtil.arrayToList(entries) : new ArrayList();
		if(!allowduplicates)
			removeDuplicates();
		refresh();
	}

	/**
	 *  Add a new entry.
	 *  @param entry The new entry.
	 */
	public void addEntry(String entry)
	{
		if(allowduplicates || !entries.contains(entry))
			this.entries.add(entry);
		refresh();
	}

	/**
	 *  Add a new entry.
	 *  @param entry The new entry.
	 */
	public void removeEntry(String entry)
	{
		this.entries.remove(entry);
		refresh();
	}
	
	/**
	 *  Remove all entries.
	 */
	public void removeEntries()
	{
		this.entries.clear();
		refresh();
	}
	
	/**
	 *  Test if an entry is contained.
	 *  @param entry The entry.
	 *  @return True, if contained.
	 */
	public boolean containsEntry(String entry)
	{
		return this.entries.contains(entry);
	}

	/**
	 *  Table cell renderer / editor using add delete buttons.
	 */
	public class ButtonCellManager	extends AbstractCellEditor	implements TableCellRenderer, TableCellEditor
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column)
		{
			JButton	ret;
			if(rowIndex!=getEntries().length)
			{
				ret	= new JButton(icons.getIcon("delete"));
				ret.setToolTipText("Remove this entry.");
			}
			else
			{
				ret	= new JButton(icons.getIcon("add"));
				ret.setToolTipText("Add a new entry.");
			}
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
			if(rowIndex!=getEntries().length)
			{
				JButton	del	= new JButton(icons.getIcon("delete"));
				del.addActionListener(new ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
//						System.out.println("remove");
//						((AbstractTableModel)getModel()).fireTableRowsDeleted(rowIndex, rowIndex);
						Object entry = entries.remove(rowIndex);
						((AbstractTableModel)getModel()).fireTableChanged(new EditableListEvent(getModel(), rowIndex, rowIndex, 
							TableModelEvent.ALL_COLUMNS, TableModelEvent.DELETE, new Object[]{entry}));
						fireEditingStopped();
						refresh();
					}
				});
				return	del;
			}
			else
			{
				JButton	add	= new JButton(icons.getIcon("add"));
				add.addActionListener(new ActionListener()
				{
					public void actionPerformed(java.awt.event.ActionEvent e)
					{
//						System.out.println("add");
						entries.add("");
						AbstractTableModel model = (AbstractTableModel)getModel();
						model.fireTableRowsInserted(model.getRowCount(), model.getRowCount());
						fireEditingStopped();
						refresh();
					}
				});
				return	add;
			}
		}
		public Object getCellEditorValue()
		{
			return "";
		}
	}

	/**
	 *  Update the ui, when the aid has changed.
	 */
	public void refresh()
	{
		// Force table repaint (hack???).
		tableChanged(new TableModelEvent(getModel(), TableModelEvent.HEADER_ROW));
		if(editable)
		{
			// Hack!!! Have to (re)set column width for buttons.
			JButton	but	= new JButton(icons.getIcon("delete"));
			but.setMargin(new Insets(0,0,0,0));
			getColumnModel().getColumn(0).setMaxWidth(but.getPreferredSize().width);
		}
		// Hack!!! For the table to be relayouted, we have to revalidate the scroll pane.
		this.invalidate();
		this.validate();
		this.repaint();
	}
	
	/**
	 *  Set the list title.
	 *  @param title The title.
	 */
	public void setTitle(String title)
	{
		this.title = title;
		refresh();
	}
	
	/**
	 *  Get the list title.
	 *  @return The title.
	 */
	public String getTitle()
	{
		return showcnt? this.title+" ["+entries.size()+"]": this.title;
	}

	/**
	 *  Test if number of entries is shown.
	 *  @return The showcnt.
	 */
	public boolean isShowEntriesCount()
	{
		return showcnt;
	}

	/**
	 *  Show the number of entries in title.
	 *  @param showcnt True for showing the number.
	 */
	public void setShowEntriesCount(boolean showcnt)
	{
		this.showcnt = showcnt;
	}
}


