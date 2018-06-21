package jadex.wfms.simulation.stateset.gui;

import jadex.bpmn.model.MParameter;
import jadex.commons.gui.SGUI;
import jadex.javaparser.SimpleValueFetcher;
import jadex.wfms.guicomponents.ButtonPanel;
import jadex.wfms.guicomponents.ChoiceTable;
import jadex.wfms.parametertypes.MultiListChoice;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.ResolvableMultiListChoiceStateSet;

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
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableColumnModel;

public class ResolvableMultiListChoiceStatePanel extends JPanel implements IStatePanel
{
	private JTable selectionTable;
	private Object[] choices;
	
	private JButton addButton;
	private JButton removeButton;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public ResolvableMultiListChoiceStatePanel(String tskName, MParameter parameter, SimulationWindow simWdw)
	{
		super(new GridBagLayout());
		this.taskName = tskName;
		this.parameterName = parameter.getName();
		this.simWindow = simWdw;
		choices = ((MultiListChoice) parameter.getInitialValue().getValue(new SimpleValueFetcher())).getChoices();
		
		this.selectionTable = new JTable();
		selectionTable.setColumnModel(new DefaultTableColumnModel());
		selectionTable.setModel(new AbstractTableModel()
		{
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (simWindow.getSelectedScenario() != null)
					return ((ResolvableMultiListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).getState(rowIndex);
				return null;
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return false;
			}
			
			public int getRowCount()
			{
				if (simWindow.getSelectedScenario() != null)
					return (int) ((ResolvableMultiListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameters(taskName).get(parameterName)).getStateCount();
				return 0;
			}
			
			public int getColumnCount()
			{
				return 1;
			}
			
			public String getColumnName(int column)
			{
				return "Selections";
			}
			
			public Class getColumnClass(int columnIndex)
			{
				return Object[].class;
			}
		});
		selectionTable.setDefaultRenderer(Object[].class, new DefaultTableCellRenderer()
		{
			public Component getTableCellRendererComponent(JTable table,
					Object value, boolean isSelected, boolean hasFocus,
					int row, int column)
			{
				return super.getTableCellRendererComponent(table, Arrays.toString((Object[]) value), isSelected, hasFocus, row, column);
			}
		});
		
		JScrollPane tableScrollPane = new JScrollPane(selectionTable);
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
					SelectionDialog dialog = new SelectionDialog(simWindow, choices);
					dialog.setVisible(true);
					Object[] selections = dialog.getSelections();
					if (selections != null)
					{
						((ResolvableMultiListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addSelectionSet(selections);
						((AbstractTableModel) selectionTable.getModel()).fireTableRowsInserted(selectionTable.getRowCount() - 1, selectionTable.getRowCount() - 1);
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
					int[] rows = selectionTable.getSelectedRows();
					for (int i = 0; i < rows.length; ++i)
					{
						((ResolvableMultiListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeSelectionSet((Object[]) selectionTable.getValueAt(rows[i] - i, 0));
						((AbstractTableModel) selectionTable.getModel()).fireTableRowsDeleted(rows[i] - i, rows[i] - i);
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
		((AbstractTableModel) selectionTable.getModel()).fireTableStructureChanged();
		boolean eb = simWindow.getSelectedScenario() != null;
		addButton.setEnabled(eb);
		removeButton.setEnabled(eb);
	}
	
	private static class SelectionDialog extends JDialog
	{
		private ChoiceTable choiceTable;
		
		private boolean canceled;
		
		public SelectionDialog(Frame owner, Object[] choices)
		{
			super(owner, "Choose Selection", true);
			getContentPane().setLayout(new GridBagLayout());
			
			this.canceled = true;
			
			this.choiceTable = new ChoiceTable(choices);
			GridBagConstraints g = new GridBagConstraints();
			g.weightx = 1;
			g.weighty = 1;
			g.fill = GridBagConstraints.BOTH;
			getContentPane().add(choiceTable, g);
			
			JPanel buttonPanel = new ButtonPanel();
			g = new GridBagConstraints();
			g.gridy = 1;
			g.weightx = 1;
			g.fill = GridBagConstraints.HORIZONTAL;
			getContentPane().add(buttonPanel, g);
			
			JButton addButton = new JButton();
			addButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					canceled = false;
					SelectionDialog.this.setVisible(false);
				}
			});
			addButton.setText("Add");
			buttonPanel.add(addButton);
			
			JButton cancelButton = new JButton();
			cancelButton.setAction(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					SelectionDialog.this.setVisible(false);
				}
			});
			cancelButton.setText("Cancel");
			buttonPanel.add(cancelButton);
			
			pack();
			setLocation(SGUI.calculateMiddlePosition(this));
		}
		
		public Object[] getSelections()
		{
			if (canceled)
				return null;
			return choiceTable.getSelections();
		}
	}
}
