package jadex.wfms.simulation.stateset.gui;

import jadex.bpmn.model.MParameter;
import jadex.javaparser.SimpleValueFetcher;
import jadex.wfms.parametertypes.ListChoice;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.ResolvableListChoiceStateSet;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class ResolvableListChoiceStatePanel extends JPanel implements IStatePanel
{
	private static final String[] COLUMN_NAMES = { "Choice", "Include" };
	
	private JTable selectionTable;
	private Object[] choices;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public ResolvableListChoiceStatePanel(String tskName, MParameter parameter, SimulationWindow simWdw)
	{
		super(new GridBagLayout());
		this.taskName = tskName;
		this.parameterName = parameter.getName();
		this.simWindow = simWdw;
		choices = ((ListChoice) parameter.getInitialValue().getValue(new SimpleValueFetcher())).getChoices();
		
		this.selectionTable = new JTable(new AbstractTableModel()
		{
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				if (columnIndex == 0)
					return choices[rowIndex];
				else if (simWindow.getSelectedScenario() != null)
					return new Boolean(((ResolvableListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).hasSelection(choices[rowIndex]));
				else
					return null;
			}
			
			public void setValueAt(Object aValue, int rowIndex, int columnIndex)
			{
				if (columnIndex == 1 && simWindow.getSelectedScenario() != null)
					if (((Boolean) aValue).booleanValue())
						((ResolvableListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).addSelection(choices[rowIndex]);
					else
						((ResolvableListChoiceStateSet) simWindow.getSelectedScenario().getTaskParameter(taskName, parameterName)).removeSelection(choices[rowIndex]);				
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return (columnIndex == 1) && simWindow.getSelectedScenario() != null;
			}
			
			public int getRowCount()
			{
				return choices.length;
			}
			
			public int getColumnCount()
			{
				return 2;
			}
			
			public String getColumnName(int column)
			{
				return COLUMN_NAMES[column];
			}
			
			public Class getColumnClass(int columnIndex)
			{
				if (columnIndex == 1)
					return Boolean.class;
				return super.getColumnClass(columnIndex);
			}
		});
		
		JScrollPane tableScrollPane = new JScrollPane(selectionTable);
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		add(tableScrollPane, g);
		
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				refreshPanel();
			}
		});
	}
	
	/**
	 * Refreshes the contents of the state panel.
	 */
	public void refreshPanel()
	{
		((AbstractTableModel) selectionTable.getModel()).fireTableStructureChanged();
		int width = selectionTable.getTableHeader().getHeaderRect(1).getSize().width;
		selectionTable.getColumnModel().getColumn(1).setMinWidth(width);
		selectionTable.getColumnModel().getColumn(1).setMaxWidth(width);
		selectionTable.getColumnModel().getColumn(1).setResizable(false);
	}
}
