package jadex.tools.debugger;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableModel;

import jadex.base.gui.CMSUpdateHandler;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.ChangeEvent;
import jadex.commons.IBreakpointPanel;
import jadex.commons.IChangeListener;
import jadex.commons.SUtil;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.gui.SGUI;
import jadex.commons.gui.jtable.TableSorter;

/**
 *  A panel for viewing the breakpoints.
 */
public class BreakpointPanel extends JPanel	implements IBreakpointPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]
	{
		"breakpoint", SGUI.makeIcon(BreakpointPanel.class,	"/jadex/tools/common/images/lockoverlay.png"),
	});

	//-------- attributes --------
	
	/** The breakpoints. */
	protected List<String>	breakpoints;
	
	/** The component description. */
	protected IComponentDescription	description;
	
	/** The external access. */
	protected IExternalAccess access;
	
	/** The list. */
	protected JTable list;
	
	/** The listeners (if any). */
	protected List	listeners;
	
	/** The cms subscription. */
	protected ISubscriptionIntermediateFuture<CMSStatusEvent>	sub;
		
	//-------- constructors --------
	
	/**
	 *  Create a new rulebase panel.
	 */
	public BreakpointPanel(String[] breakpointsa, final IComponentDescription description, IExternalAccess access, CMSUpdateHandler cmshandler)
	{
		this.breakpoints = SUtil.arrayToList(breakpointsa);
		this.description	= description;
		this.access = access;
	
		sub	= cmshandler.addCMSListener(access.getId());
		sub.addResultListener(new IIntermediateResultListener<CMSStatusEvent>()
		{
			@Override
			public void exceptionOccurred(Exception exception)
			{
			}

			@Override
			public void resultAvailable(Collection<CMSStatusEvent> result)
			{
			}

			@Override
			public void intermediateResultAvailable(CMSStatusEvent result)
			{
				if(result.getComponentIdentifier().equals(description.getName()))
				{
					BreakpointPanel.this.description = result.getComponentDescription();
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							((AbstractTableModel)list.getModel()).fireTableDataChanged();
						}
					});
				}
			}

			@Override
			public void finished()
			{
			}
		});
		
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
		
		list = new JTable(new TableSorter(lm));
		TableSorter sorter = (TableSorter)list.getModel();
		sorter.setTableHeader(list.getTableHeader());

		setLayout(new BorderLayout());
		JScrollPane sp = new JScrollPane(list);
		add(sp, BorderLayout.CENTER);

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
		sub.terminate();
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
				 selected.add(breakpoints.get(sorter.modelIndex(i)));
		}
		return (String[])selected.toArray(new String[selected.size()]);
	}

	/**
	 *  Set the currently selected breakpoints.
	 */
	public void setSelectedBreakpoints(String[] breakpoints)
	{
		list.clearSelection();
		for(int i=0; i<breakpoints.length; i++)
		{
			int	index	= this.breakpoints.indexOf(breakpoints[i]);
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
			boolean	selected	= SUtil.arrayContains(description.getBreakpoints(), breakpoints.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint.");
			return ret;
		}

		public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, final int rowIndex, int column)
		{
			final TableSorter sorter = (TableSorter)list.getModel();
			boolean	selected	= SUtil.arrayContains(description.getBreakpoints(), breakpoints.get(sorter.modelIndex(rowIndex)));
			JPanel	ret	= new JPanel(new BorderLayout());
			final JCheckBox	but	= new JCheckBox((String)null, selected);
			ret.add(but, BorderLayout.CENTER);
			ret.setToolTipText("Enable/disable breakpoint.");
			but.addActionListener(new ActionListener()
			{
				public void actionPerformed(java.awt.event.ActionEvent e)
				{
					final List	bps	= new ArrayList(Arrays.asList(description.getBreakpoints()));
					if(but.isSelected())
					{
						bps.add(breakpoints.get(sorter.modelIndex(rowIndex)));
					}
					else
					{
						bps.remove(breakpoints.get(sorter.modelIndex(rowIndex)));
					}
					access.setComponentBreakpoints(
						description.getName(), (String[])bps.toArray(new String[bps.size()]));
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
