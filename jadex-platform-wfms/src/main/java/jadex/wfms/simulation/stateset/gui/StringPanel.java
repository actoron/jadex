package jadex.wfms.simulation.stateset.gui;

import jadex.wfms.guicomponents.StringTable;
import jadex.wfms.guicomponents.StringTable.AbstractStringTableModel;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.StringStateSet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.table.AbstractTableModel;

public class StringPanel extends JPanel implements IStatePanel
{
	private StringTable table;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public StringPanel(String tskName, String paramtrName, SimulationWindow simWdw)
	{
		super(new GridBagLayout());
		this.taskName = tskName;
		this.parameterName = paramtrName;
		this.simWindow = simWdw;
		
		table = new StringTable(new AbstractStringTableModel()
		{
			public void addString(String string)
			{
				if (simWindow.getSelectedScenario() != null)
					if (((StringStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addString(string));
						fireTableRowsInserted(getRowCount() - 1, getRowCount() - 1);
			}
			
			public void removeString(int index)
			{
				if (simWindow.getSelectedScenario() != null)
				{
					((StringStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeString(index);
					fireTableRowsDeleted(getRowCount(), getRowCount());
				}
			}
			
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (simWindow.getSelectedScenario() != null)
					return ((StringStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).getState(rowIndex);
				return null;
			}
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{
				if (simWindow.getSelectedScenario() != null)
					((StringStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).setString(rowIndex, (String) aValue);
				fireTableRowsUpdated(rowIndex, rowIndex);
			}
			
			public int getRowCount()
			{
				if (simWindow.getSelectedScenario() != null)
					return (int) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName).getStateCount();
				return 0;
			}
			
			public int getColumnCount()
			{
				return 1;
			}
			
			public String getColumnName(int column)
			{
				return "Strings";
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return simWindow.getSelectedScenario() != null && simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName).getStateCount() > rowIndex;
			}
			
			public boolean isEditable()
			{
				return simWindow.getSelectedScenario() != null;
			}
			
		}, StringTable.TEXT_BUTTONS);
		
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		add(table, g);
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		((AbstractTableModel) table.getModel()).fireTableStructureChanged();
	}
}
