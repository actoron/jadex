package jadex.tools.debugger;

import jadex.commons.SGUI;
import jadex.rules.tools.common.TableSorter;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

/**
 *  A panel for viewing the breakpoints.
 */
public class BreakpointPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"breakpoint", SGUI.makeIcon(BreakpointPanel.class,	"/jadex/rules/tools/reteviewer/images/lockoverlay.png"),
	});

	//-------- attributes --------
	
	/** The breakpoints. */
	protected List	breakpoints;
	
	/** The list. */
	protected JTable list;
	
	/** The listeners (if any). */
	protected List	listeners;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rulebase panel.
	 */
	public BreakpointPanel(Collection breakpoints)
	{
		this.breakpoints = new ArrayList(breakpoints);
		
		TableModel lm = new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return 2;
			}
			public int getRowCount()
			{
				return BreakpointPanel.this.breakpoints.size();
			}
			public Object getValueAt(int row, int column)
			{
				return column==1 ? BreakpointPanel.this.breakpoints.get(row) : null;
			}
			public boolean isCellEditable(int row, int column)
			{
				return column==0;
			}
			public Class getColumnClass(int column)
			{
				return column==0 ? JToggleButton.class : String.class;
			}
		};
				
		this.list = new JTable(new TableSorter(lm));
		TableSorter sorter = (TableSorter)list.getModel();
		sorter.setTableHeader(list.getTableHeader());

		this.setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(list);
		this.add(sp, BorderLayout.CENTER);
//		this.setBorder(BorderFactory.createTitledBorder("Rulebase"));

		// Hack!!! Set header preferred size and afterwards set title text to "" (bug in JDK1.5).
		list.getTableHeader().setPreferredSize(list.getTableHeader().getPreferredSize());
		list.getColumnModel().getColumn(0).setHeaderRenderer(new DefaultTableCellRenderer()
		{
	        public Component getTableCellRendererComponent(JTable table, 
	        	Object obj, boolean selected, boolean focus, int row, int column)
	        {
	        	setIcon(icons.getIcon("breakpoint"));
	            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	            setHorizontalAlignment(JLabel.CENTER);
				setToolTipText("Use checkbox to enable/disable breakpoint on a rule.");
	            return this;
	        }
	    });
		list.getColumnModel().getColumn(1).setHeaderValue("Breakpoints");

		list.setDefaultRenderer(JToggleButton.class, new ButtonCellManager());
		list.setDefaultEditor(JToggleButton.class, new ButtonCellManager());
		JCheckBox	but	= new JCheckBox();
		but.setMargin(new Insets(0,0,0,0));
		list.getColumnModel().getColumn(0).setMaxWidth(but.getPreferredSize().width+4);
		
		list.getSelectionModel().addListSelectionListener(new ListSelectionListener()
		{
			public void valueChanged(ListSelectionEvent e)
			{
				 if(!e.getValueIsAdjusting() && listeners!=null)
				 {
					 ChangeEvent	ce	= new ChangeEvent(this);
					 for(int i=0; i<listeners.size(); i++)
					 {
						 ((ChangeListener)listeners.get(i)).stateChanged(ce);
					 }
				 }
			}
		});
	}
	
	
	/**
	 *  Dispose the panel
	 *  and remove all listeners.
	 */
	public void	dispose()
	{
	}
	
	/**
	 *  Clear the selection in the gui.
	 */
	public void clearSelectedBreakpoints()
	{
		list.clearSelection();
	}

	/**
	 *  Get the currently selected breakpoints.
	 */
	public Object[] getSelectedBreakpoints()
	{
		 List	selected	= new ArrayList();
		 TableSorter sorter = (TableSorter)list.getModel();
		 for(int i=0; i<list.getRowCount(); i++)
		 {
			 if(list.isRowSelected(i))
				 selected.add(breakpoints.get(sorter.modelIndex(i)));
		}
		return selected.toArray();
	}

	/**
	 *  Select a breakpoint.
	 */
	public void selectBreakpoints(Object breakpoint)
	{
		int	index	= breakpoints.indexOf(breakpoints);
		if(index!=-1)
			list.getSelectionModel().addSelectionInterval(index, index);
	}

	/**
	 *  Add a change listener to be notified of rule selection changes.
	 */
	public void addRuleSelectionListener(ChangeListener listener)
	{
		if(listeners==null)
			listeners	= new ArrayList();
		
		listeners.add(listener);
	}

	/**
	 *  Remove a change listener.
	 */
	public void removeRuleSelectionListener(ChangeListener listener)
	{
		listeners.remove(listener);

		if(listeners.isEmpty())
			listeners	= null;
	}

	/**
	 *  Table cell renderer / editor using add delete buttons.
	 */
	public class ButtonCellManager	extends AbstractCellEditor	implements TableCellRenderer, TableCellEditor
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column)
		{
//			TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= false; //steppable.isBreakpoint((IRule)breakpoints.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint.");
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
//			final TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= false; //steppable.isBreakpoint((IRule)breakpoints.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			final JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint.");
			but.addActionListener(new ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if(but.isSelected())
					{
//						steppable.addBreakpoint((IRule) breakpoints.get(sorter.modelIndex(rowIndex)));
					}
					else
					{
//						steppable.removeBreakpoint((IRule) breakpoints.get(sorter.modelIndex(rowIndex)));
					}
				}
			});
			return	ret;
		}

		public Object getCellEditorValue()
		{
			return "";
		}
	}
}
