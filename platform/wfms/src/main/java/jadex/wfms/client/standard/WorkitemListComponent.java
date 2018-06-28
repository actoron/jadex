package jadex.wfms.client.standard;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.ExceptionResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingResultListener;
import jadex.wfms.client.IClientActivity;
import jadex.wfms.client.IWorkitem;
import jadex.wfms.client.standard.parametergui.ActivityComponent;
import jadex.wfms.guicomponents.SGuiHelper;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class WorkitemListComponent extends JPanel
{
	private static final Object[] WORKITEM_LIST_COLUMN_NAMES = {"Workitem", "Role"};
	
	private static final String BEGIN_ACTIVITY_BUTTON_LABEL = "Begin Activity";
	
	/** The client. */
	protected IWfmsClient client;
	
	/** Table listing available workitems */
	private JTable workitemTable;
	
	/** Current workitem table mouse listener */
	private MouseListener workitemMouseListener;
	
	/** Model of table listing available workitems */
	private DefaultTableModel workitemTableModel;
	
	/** Begin activity button */
	private JButton beginActivityButton;
	
	/** The activity panel */
	protected JTabbedPane activitypane;
	
	public WorkitemListComponent(IWfmsClient client)
	{
		super(new GridBagLayout());
		
		this.client = client;
		
		final JSplitPane splitpane = new JSplitPane();
		splitpane.setOneTouchExpandable(true);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1.0;
		g.weighty = 1.0;
		g.fill = GridBagConstraints.BOTH;
		g.anchor = GridBagConstraints.CENTER;
		add(splitpane, g);
		
		
		JPanel wipanel = new JPanel(new GridBagLayout());
		splitpane.setLeftComponent(wipanel);
		
		workitemTableModel = new DefaultTableModel();
		workitemTableModel.setColumnIdentifiers(WORKITEM_LIST_COLUMN_NAMES);
		
		workitemTable = new JTable(workitemTableModel)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		
		workitemTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
		{
			
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{
				if (value instanceof IWorkitem)
					return super.getTableCellRendererComponent(table, SGuiHelper.beautifyName(((IWorkitem) value).getName()), isSelected, hasFocus, row, column);
				return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			}
		});
		
		JScrollPane workitemScrollPane = new JScrollPane(workitemTable);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		wipanel.add(workitemScrollPane, gbc);
		
		beginActivityButton = new JButton(BEGIN_ACTIVITY_BUTTON_LABEL);
		beginActivityButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.NONE;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.SOUTHWEST;
		wipanel.add(beginActivityButton, gbc);
		
		activitypane = new JTabbedPane();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		splitpane.setRightComponent(activitypane);
		
		// setDividerLocation() only works when the component is visible, grr....
		addComponentListener(new ComponentListener()
		{
			public void componentShown(ComponentEvent e)
			{
				splitpane.setDividerLocation(0.4);
				WorkitemListComponent.this.removeComponentListener(this);
			}
			
			public void componentResized(ComponentEvent e)
			{
			}
			
			public void componentMoved(ComponentEvent e)
			{
			}
			
			public void componentHidden(ComponentEvent e)
			{
			}
		});
	}
	
	public void setBeginActivityAction(final Action action)
	{
		beginActivityButton.setAction(action);
		beginActivityButton.setText(BEGIN_ACTIVITY_BUTTON_LABEL);
		
		if (workitemMouseListener != null)
			workitemTable.removeMouseListener(workitemMouseListener);
		
		workitemMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					action.actionPerformed(new ActionEvent(e, e.getID(), null));
				}
			}
		};
		
		workitemTable.addMouseListener(workitemMouseListener);
	}
	
	public IWorkitem getSelectedWorkitem()
	{
		int row = workitemTable.getSelectedRow();
		if (row >= 0)
			return (IWorkitem) workitemTableModel.getValueAt(row, 0);
		return null;
	}
	
	/**
	 * Adds a workitem to the list.
	 * @param wi the workitem
	 */
	public void addWorkitem(IWorkitem wi)
	{
		for (int i = 0; i < workitemTableModel.getRowCount(); ++i)
		{
			if (wi.equals(workitemTableModel.getValueAt(i, 0)))
				return;
			
		}
		workitemTableModel.addRow(new Object[] {wi, wi.getRole()});
	}
	
	/**
	 * Removess a workitem from the list.
	 * @param wi the workitem
	 */
	public void removeWorkitem(IWorkitem wi)
	{
		for (int i = 0; i < workitemTableModel.getRowCount(); ++i)
		{
			if (wi.equals(workitemTableModel.getValueAt(i, 0)))
			{
				workitemTableModel.removeRow(i);
			}
		}
	}
	
	/**
	 * Sets the listed workitems, deleting the previous list.
	 * @param workitems new set of workitems
	 */
	public void setWorkitems(Set workitems)
	{
		while (workitemTableModel.getRowCount() != 0)
			workitemTableModel.removeRow(0);
		
		for (Iterator it = workitems.iterator(); it.hasNext(); )
		{
			IWorkitem wi = (IWorkitem) it.next();
			workitemTableModel.addRow(new Object[] {wi, wi.getRole()});
		}
	}
	
	/** 
	 *  Adds an activity.
	 *  @param activity The activity.
	 */
	public void addActivity(IClientActivity activity)
	{
		ActivityComponent ac = createActivityComponent(activity);
		
		activitypane.add(ac, SGuiHelper.beautifyName(ac.getActivity().getName()));
		activitypane.setSelectedComponent(ac);
	}
	
	/** 
	 *  Removes an activity.
	 *  @param activity The activity.
	 */
	public void removeActivity(IClientActivity activity)
	{
		for (int i = 0; i < activitypane.getTabCount(); ++i)
		{
			ActivityComponent ac = (ActivityComponent) activitypane.getComponent(i);
			if (ac.getActivity().equals(activity))
				activitypane.remove(i);
		}
	}
	
	/**
	 * Clears the workitem list
	 */
	public void clear()
	{
		while (workitemTableModel.getRowCount() > 0)
			workitemTableModel.removeRow(0);
	}
	
	protected ActivityComponent createActivityComponent(final IClientActivity activity)
	{
		final ActivityComponent ac = new ActivityComponent(activity);
		ac.setCancelAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				cancelActivity(activity);
			}
		});
		
		ac.setSuspendAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				IClientActivity activity = ac.getActivity();
				Map parameterValues = ac.getParameterValues();
				activity.setMultipleParameterValues(parameterValues);
				
				cancelActivity(activity);
			}
		});
		
		ac.setFinishAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (!ac.isReadyForFinish())
					return;
				
				final IClientActivity activity = ac.getActivity();
				activity.setMultipleParameterValues(ac.getParameterValues());
				
				client.getExternalAccess().scheduleStep(new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						client.getWfms().finishActivity(activity).addResultListener(new SwingResultListener(new ExceptionResultListener<Void>()
						{
							public void exceptionOccurred(Exception exception)
							{
								JOptionPane.showMessageDialog(WorkitemListComponent.this, "Failed finishing activity.");
							}
						}));
						return IFuture.DONE;
					}
				});
			}
		});
		
		return ac;
	}
	
	protected void cancelActivity(final IClientActivity activity)
	{
		client.getExternalAccess().scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				client.getWfms().cancelActivity(activity).addResultListener(new SwingResultListener(new ExceptionResultListener<Void>()
				{
					
					public void exceptionOccurred(Exception exception)
					{
						JOptionPane.showMessageDialog(WorkitemListComponent.this, "Activity cancelation failed.");
					}
				}));
				return IFuture.DONE;
			}
		});
		
	}
}
