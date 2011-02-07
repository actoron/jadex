package jadex.wfms.simulation.stateset.gui;

import jadex.bpmn.model.MParameter;
import jadex.commons.gui.SGUI;
import jadex.wfms.guicomponents.ButtonPanel;
import jadex.wfms.guicomponents.StringTable;
import jadex.wfms.guicomponents.StringTable.DefaultStringTableModel;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.StringArrayStateSet;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EtchedBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;

public class StringArrayStatePanel extends JPanel implements IStatePanel
{
	private JTable arrayTable;
	
	private JButton addButton;
	private JButton removeButton;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public StringArrayStatePanel(String tskName, MParameter parameter, SimulationWindow simWdw)
	{
		super(new GridBagLayout());
		this.taskName = tskName;
		this.parameterName = parameter.getName();
		this.simWindow = simWdw;
		
		this.arrayTable = new JTable();
		arrayTable.setColumnModel(new DefaultTableColumnModel());
		arrayTable.setModel(new AbstractTableModel()
		{
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (simWindow.getSelectedScenario() != null)
					return ((StringArrayStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).getState(rowIndex);
				return null;
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return false;
			}
			
			public int getRowCount()
			{
				if (simWindow.getSelectedScenario() != null)
					return (int) ((StringArrayStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).getStateCount();
				return 0;
			}
			
			public int getColumnCount()
			{
				return 1;
			}
			
			public String getColumnName(int column)
			{
				return "Values";
			}
			
			public Class getColumnClass(int columnIndex)
			{
				return Object[].class;
			}
		});
		arrayTable.setDefaultRenderer(Object[].class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{
				return super.getTableCellRendererComponent(table, Arrays.toString((Object[]) value), isSelected, hasFocus, row, column);
			}
		});
		
		JScrollPane tableScrollPane = new JScrollPane(arrayTable);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		add(tableScrollPane, g);
		
		JPanel buttonPanel = new ButtonPanel();
		g = new GridBagConstraints();
		g.gridy = 1;
		g.weightx = 1;
		g.fill = GridBagConstraints.HORIZONTAL;
		add(buttonPanel, g);
		
		addButton = new JButton();
		addButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					StringArrayDialog dialog = new StringArrayDialog(simWindow);
					dialog.setVisible(true);
					String[] stringarray = dialog.getStringArray();
					if (stringarray != null)
					{
						((StringArrayStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addString(stringarray);
						((AbstractTableModel) arrayTable.getModel()).fireTableRowsInserted(arrayTable.getRowCount() - 1, arrayTable.getRowCount() - 1);
					}
				}
			}
		});
		addButton.setText("Add...");
		buttonPanel.add(addButton);
		
		removeButton = new JButton();
		removeButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					int[] rows = arrayTable.getSelectedRows();
					for (int i = 0; i < rows.length; ++i)
					{
						((StringArrayStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeString((String[]) arrayTable.getValueAt(rows[i] - i, 0));
						((AbstractTableModel) arrayTable.getModel()).fireTableRowsDeleted(rows[i] - i, rows[i] - i);
					}
				}
			}
		});
		removeButton.setText("Remove");
		buttonPanel.add(removeButton);
		
		refreshPanel();
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		((AbstractTableModel) arrayTable.getModel()).fireTableStructureChanged();
		boolean eb = simWindow.getSelectedScenario() != null;
		addButton.setEnabled(eb);
		removeButton.setEnabled(eb);
	}
	
	private static class StringArrayDialog extends JDialog
	{
		private StringTable stringTable;
		
		private boolean canceled;
		
		public StringArrayDialog(Frame owner)
		{
			super(owner, "Create String Array", true);
			getContentPane().setLayout(new GridBagLayout());
			
			JPanel mainPanel = new JPanel(new GridBagLayout());
			mainPanel.setBorder(new EtchedBorder(EtchedBorder.LOWERED));
			GridBagConstraints g = new GridBagConstraints();
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			getContentPane().add(mainPanel, g);
			
			this.canceled = true;
			
			this.stringTable = new StringTable(new StringTable.DefaultStringTableModel("Strings", true), StringTable.ICON_BUTTONS);
			g = new GridBagConstraints();
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			mainPanel.add(stringTable, g);
			
			JPanel buttonPanel = new ButtonPanel();
			g = new GridBagConstraints();
			g.gridy = 1;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			mainPanel.add(buttonPanel, g);
			
			JButton addButton = new JButton();
			addButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					canceled = false;
					StringArrayDialog.this.setVisible(false);
				}
			});
			addButton.setText("Add String Array");
			buttonPanel.add(addButton);
			
			JButton cancelButton = new JButton();
			cancelButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					StringArrayDialog.this.setVisible(false);
				}
			});
			cancelButton.setText("Cancel");
			buttonPanel.add(cancelButton);
			
			pack();
			setLocation(SGUI.calculateMiddlePosition(this));
		}
		
		public String[] getStringArray()
		{
			if (canceled)
				return null;
			return ((DefaultStringTableModel) stringTable.getModel()).getStringsAsArray();
		}
	}
}
