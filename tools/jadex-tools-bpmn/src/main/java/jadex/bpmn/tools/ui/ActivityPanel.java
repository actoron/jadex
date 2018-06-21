package jadex.bpmn.tools.ui;

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

import jadex.bpmn.model.MActivity;
import jadex.bpmn.model.MBpmnModel;
import jadex.commons.ISteppable;
import jadex.commons.gui.jtable.TableSorter;

/**
 *  A panel for viewing the content of a bpmn process model.
 */
public class ActivityPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
//	protected static UIDefaults	icons	= new UIDefaults(new Object[]
//	{
//		"breakpoint", SGUI.makeIcon(RulebasePanel.class,	"/jadex/rules/tools/reteviewer/images/lockoverlay.png"),
//	});

	//-------- attributes --------
	
	/** The rulebase. */
	protected MBpmnModel model;

	/** The activities. */
	protected List activities;
	
	/** The list. */
	protected JTable list;
	
	/** The listeners (if any). */
	protected List	listeners;
	
	/** The steppable (to set/remove breakpoints). */
	protected ISteppable	steppable;
	
	//-------- constructors --------
	
	/**
	 *  Create a new activity panel.
	 */
	public ActivityPanel(MBpmnModel model, ISteppable steppable)
	{
		this.steppable	= steppable;
		this.model	= model;
		this.activities = new ArrayList();
		
		for(Iterator it=model.getAllActivities().values().iterator(); it.hasNext(); )
			activities.add(it.next());
		
		TableModel lm = new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return 2;
			}
			public int getRowCount()
			{
				return activities.size();
			}
			public Object getValueAt(int row, int column)
			{
				Object ret = null; 
				if(column==1)
				{
					MActivity act = (MActivity)activities.get(row);
					ret = act.getName();
					if(ret!=null)
					{
//						ret = ret + " (type = " + act.getActivityType() + ", id = " + act.getId() + ")"; 
						ret = ret + " (type = " + act.getActivityType() + ")"; 
					}
					else
					{
						ret = act.getActivityType() + " (id = " + act.getId() + ")"; 
					}
					
				}
				return ret;
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
		
//		listener = new IRulebaseListener()
//		{
//			public void ruleAdded(IRule rule)
//			{
//				activities.add(rule);
//			}
//			
//			public void ruleRemoved(IRule rule)
//			{
//				activities.remove(rule);
//			}
//		};
//		rulebase.addRulebaseListener(listener);
		
		this.list = new JTable(new TableSorter(lm));
		TableSorter sorter = (TableSorter)list.getModel();
		sorter.setTableHeader(list.getTableHeader());

		this.setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(list);
		this.add(sp, BorderLayout.CENTER);
//		this.setBorder(BorderFactory.createTitledBorder("Activities"));

		// Hack!!! Set header preferred size and afterwards set title text to "" (bug in JDK1.5).
		list.getTableHeader().setPreferredSize(list.getTableHeader().getPreferredSize());
		list.getColumnModel().getColumn(0).setHeaderRenderer(new DefaultTableCellRenderer()
		{
	        public Component getTableCellRendererComponent(JTable table, 
	        	Object obj, boolean selected, boolean focus, int row, int column)
	        {
//	        	setIcon(icons.getIcon("breakpoint"));
	            setBorder(UIManager.getBorder("TableHeader.cellBorder"));
	            setHorizontalAlignment(JLabel.CENTER);
				setToolTipText("Use checkbox to enable/disable breakpoint on an activity.");
	            return this;
	        }
	    });
		list.getColumnModel().getColumn(1).setHeaderValue("Activities");

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
	 * /
	public void	dispose()
	{
		rulebase.removeRulebaseListener(listener);
	}*/
	
	/**
	 *  Clear the selection in the gui.
	 */
	public void clearSelectedRules()
	{
		list.clearSelection();
	}

	/**
	 *  Get the currently selected activities.
	 */
	public MActivity[] getSelectedActivities()
	{
		 List	selected	= new ArrayList();
		 TableSorter sorter = (TableSorter)list.getModel();
		 for(int i=0; i<list.getRowCount(); i++)
		 {
			 if(list.isRowSelected(i))
				 selected.add(activities.get(sorter.modelIndex(i)));
		}
		return (MActivity[])selected.toArray(new MActivity[selected.size()]);
	}

	/**
	 *  Select an activity.
	 */
	public void selectActivity(MActivity activity)
	{
		int	index	= activities.indexOf(activity);
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
			TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= steppable.isBreakpoint((MActivity)activities.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint on this rule.");
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
			final TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= steppable.isBreakpoint((MActivity)activities.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			final JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint on this rule.");
			but.addActionListener(new ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					if(but.isSelected())
					{
						steppable.addBreakpoint((MActivity)activities.get(sorter.modelIndex(rowIndex)));
					}
					else
					{
						steppable.removeBreakpoint((MActivity)activities.get(sorter.modelIndex(rowIndex)));
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
