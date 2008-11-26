package jadex.rules.rulesystem.rete.viewer;

import jadex.commons.SGUI;
import jadex.rules.rulesystem.IRule;
import jadex.rules.rulesystem.IRulebase;
import jadex.rules.rulesystem.IRulebaseListener;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

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
 *  A panel for viewing the content of the rulebase.
 */
public class RulebasePanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"breakpoint", SGUI.makeIcon(RulebasePanel.class,	"/jadex/rules/rulesystem/rete/viewer/images/important.png"),
//		"breakpoint", SGUI.makeIcon(RulebasePanel.class,	"/jadex/rules/rulesystem/rete/viewer/images/lockoverlay.png"),
	});

	//-------- attributes --------
	
	/** The rulebase. */
//	protected IRulebase rulebase;
	protected List rules;
	
	/** The list. */
	protected JTable list;
	
	/** The listeners (if any). */
	protected List	listeners;
	
	/** The breakpoints. */
	protected Set	breakpoints;
	
	//-------- constructors --------
	
	/**
	 *  Create a new rulebase panel.
	 */
	public RulebasePanel(final IRulebase rulebase)
	{
//		this.rulebase = rulebase;
		this.rules = new ArrayList();
		this.breakpoints	= new HashSet();
		for(Iterator it=rulebase.getRules().iterator(); it.hasNext(); )
			rules.add(it.next());
		
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
				return column==1 ? ((IRule)rules.get(row)).getName() : null;
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
		
		rulebase.addRulebaseListener(new IRulebaseListener()
		{
			public void ruleAdded(IRule rule)
			{
				rules.add(rule);
			}
			
			public void ruleRemoved(IRule rule)
			{
				rules.remove(rule);
			}
		});
		
		this.list = new JTable(lm);
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
	 *  Clear the selection in the gui.
	 */
	public void clearSelectedRules()
	{
		list.clearSelection();
	}

	/**
	 *  Get the currently selected rules.
	 */
	public IRule[] getSelectedRules()
	{
		 List	selected	= new ArrayList();
		 for(int i=0; i<list.getRowCount(); i++)
		 {
			 if(list.isRowSelected(i))
				 selected.add(rules.get(i));
		 }
		return (IRule[]) selected.toArray(new IRule[selected.size()]);
	}

	/**
	 *  Select a rule.
	 */
	public void selectRule(IRule rule)
	{
		int	index	= rules.indexOf(rule);
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

//	rp.getRulebasePanel().getList().setCellRenderer(new DefaultListCellRenderer()
//	{
//		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus)
//		{
//			Component	ret	= super.getListCellRendererComponent(list, ((IRule)value).getName(), index, isSelected, cellHasFocus);
//			if(steppable.isBreakpoint((IRule)value))
//				setBackground(Color.red);
//			return ret;
//		}
//	});
//	rp.getRulebasePanel().getList().addMouseListener(new MouseAdapter()
//	{
//		public void mousePressed(MouseEvent e)
//		{
//			if(e.isPopupTrigger())
//				doPopup(e);
//		}
//		public void mouseReleased(MouseEvent e)
//		{
//			if(e.isPopupTrigger())
//				doPopup(e);
//		}
//		public void	doPopup(MouseEvent e)
//		{
//			JList	list	= (JList)e.getSource();
//			int index	= list.locationToIndex(e.getPoint());
//			if(index!=-1)
//			{
//				Iterator	it	= rulesystem.getRulebase().getRules().iterator();
//				for(int i=0; i<index && it.hasNext(); i++)
//				{
//					it.next();
//				}
//				if(it.hasNext())
//				{
//					IRule	rule	= (IRule)it.next();
//					if(steppable.isBreakpoint(rule))
//						steppable.removeBreakpoint(rule);
//					else
//						steppable.addBreakpoint(rule);
//					list.repaint();
//				}
//			}
//		}
//	});

	/**
	 *  Table cell renderer / editor using add delete buttons.
	 */
	public class ButtonCellManager	extends AbstractCellEditor	implements TableCellRenderer, TableCellEditor
	{
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int rowIndex, int column)
		{
			boolean	selected	= breakpoints.contains(rules.get(rowIndex));
			JPanel	ret	= new JPanel(new BorderLayout());
			JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint on this rule.");
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
			boolean	selected	= breakpoints.contains(rules.get(rowIndex));
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
						breakpoints.add(rules.get(rowIndex));
					}
					else
					{
						breakpoints.remove(rules.get(rowIndex));
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
