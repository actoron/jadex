package jadex.wfms.client.standard.parametergui;

import jadex.wfms.parametertypes.MultiListChoice;

import java.awt.GridBagConstraints;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

public class MultiListChoiceParameterPanel extends AbstractParameterPanel
{
	private JTable parameterTable;
	
	private MultiListChoice parameterValue;
	
	public MultiListChoiceParameterPanel(String parameterName, MultiListChoice initialValue, boolean readOnly)
	{
		super(parameterName, readOnly);
		if (initialValue == null)
			throw new RuntimeException("Uninitialized MultiListChoice: " + parameterName);
		
		parameterValue = initialValue;
		DefaultTableModel model = new DefaultTableModel();
		model.setColumnIdentifiers(new Object[] {"Item", "Select"});
		parameterTable = new JTable(model)
		{
			public Class getColumnClass(int column)
			{
				if (column == 1)
					return Boolean.class;
				return String.class;
			}
			
			public boolean isCellEditable(int row, int column)
			{
				if (column == 1)
					return true;
				return false;
			}
		};
		Object[] choices = parameterValue.getChoices();
		Object[] selArr = parameterValue.getSelections() != null? parameterValue.getSelections(): new Object[0];
		HashSet selections = new HashSet(Arrays.asList(selArr));
		for (int i = 0; i < choices.length; ++i)
			model.addRow(new Object[] {choices[i], new Boolean(selections.contains(choices[i]))});
		
		GridBagConstraints g = new GridBagConstraints();
		g.gridx = 0;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(parameterTable, g);
	}
	
	public boolean isParameterValueValid()
	{
		return true;
	}
	
	public boolean requiresLabel()
	{
		return true;
	}
	
	public Object getParameterValue()
	{
		ArrayList selectionList = new ArrayList();
		DefaultTableModel model = (DefaultTableModel) parameterTable.getModel();
		for (int i = 0; i < model.getRowCount(); ++i)
		{
			if (((Boolean) model.getValueAt(i, 1)).booleanValue())
				selectionList.add(model.getValueAt(i, 0));
		}
		String[] selections = (String[]) selectionList.toArray(new String[0]);
		
		parameterValue.setSelections(selections);
		return parameterValue;
	}
}
