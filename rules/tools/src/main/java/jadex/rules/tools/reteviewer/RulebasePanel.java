package jadex.rules.tools.reteviewer;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
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
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.commons.ISteppable;
import jadex.commons.gui.SGUI;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.IRulebaseListener;
import jadex.rules.tools.common.TableSorter;

/**
 *  A panel for viewing the content of the rulebase.
 */
public class RulebasePanel extends JPanel	implements IBreakpointPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"breakpoint", SGUI.makeIcon(RulebasePanel.class,	"/jadex/rules/tools/reteviewer/images/lockoverlay.png"),
	});

	//-------- attributes --------
	
	/** The rulebase. */
	protected IRulebase rulebase;

	/** The rules. */
	protected List rules;
	
	/** The list. */
	protected JTable list;
	
	/** The listeners (if any). */
	protected List	listeners;
	
	/** The steppable (to set/remove breakpoints). */
	protected ISteppable	steppable;
	
	/** The rulebase listener. */
	protected IRulebaseListener	listener;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rulebase panel.
	 */
	public RulebasePanel(IRulebase rulebase, ISteppable steppable)
	{
		this.steppable	= steppable;
		this.rulebase	= rulebase;
		this.rules = new ArrayList();
		for(Iterator it=rulebase.getRules().iterator(); it.hasNext(); )
			rules.add(((IRule)it.next()).getName());
		
		TableModel lm = new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return 2;
			}
			public int getRowCount()
			{
				return rules.size();
			}
			public Object getValueAt(int row, int column)
			{
				return column==1 ? rules.get(row) : null;
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
		
		listener	= new IRulebaseListener()
		{
			public void ruleAdded(IRule rule)
			{
				rules.add(rule.getName());
			}
			
			public void ruleRemoved(IRule rule)
			{
				rules.remove(rule.getName());
			}
		};
		rulebase.addRulebaseListener(listener);
		
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
		list.getColumnModel().getColumn(1).setHeaderValue("Rulebase");

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
					 ChangeEvent	ce	= new ChangeEvent(this, EVENT_TYPE_SELECTED);
					 for(int i=0; i<listeners.size(); i++)
					 {
						 ((IChangeListener)listeners.get(i)).changeOccurred(ce);
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
		rulebase.removeRulebaseListener(listener);
	}
	
	/**
	 *  Get the currently selected breakpoints.
	 */
	public String[] getSelectedBreakpoints()
	{
		 List	selected	= new ArrayList();
		 TableSorter sorter = (TableSorter)list.getModel();
		 for(int i=0; i<list.getRowCount(); i++)
		 {
			 if(list.isRowSelected(i))
				 selected.add(rules.get(sorter.modelIndex(i)));
		}
		return (String[]) selected.toArray(new String[selected.size()]);
	}

	/**
	 *  Set the currently selected breakpoints.
	 */
	public void setSelectedBreakpoints(String[] breakpoints)
	{
		list.clearSelection();
		for(int i=0; i<breakpoints.length; i++)
		{
			int	index	= rules.indexOf(breakpoints[i]);
			if(index!=-1)
				list.getSelectionModel().addSelectionInterval(index, index);
		}
	}

	/**
	 *  Add a change listener to be notified of rule selection changes.
	 */
	public void addBreakpointListener(IChangeListener listener)
	{
		if(listeners==null)
			listeners	= new ArrayList();
		
		listeners.add(listener);
	}

	/**
	 *  Remove a change listener.
	 */
	public void removeBreakpointListener(IChangeListener listener)
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
			TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= steppable!=null && steppable.isBreakpoint(rules.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint on this rule.");
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
			final TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= steppable!=null && steppable.isBreakpoint(rules.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			final JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint on this rule.");
			but.addActionListener(new ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if(but.isSelected() && steppable!=null)
					{
						steppable.addBreakpoint(rules.get(sorter.modelIndex(rowIndex)));
					}
					else if(steppable!=null)
					{
						steppable.removeBreakpoint(rules.get(sorter.modelIndex(rowIndex)));
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
