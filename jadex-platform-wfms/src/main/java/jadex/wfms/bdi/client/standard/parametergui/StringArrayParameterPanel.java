package jadex.wfms.bdi.client.standard.parametergui;

import jadex.wfms.bdi.client.standard.SGuiHelper;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;

public class StringArrayParameterPanel extends AbstractParameterPanel
{
	private JTable parameterTable;
	
	public StringArrayParameterPanel(String parameterName, String[] initialValue, Map metaProperties, final boolean readOnly)
	{
		super(parameterName, readOnly);
		
		JPanel mainPanel = new JPanel(new GridBagLayout());
		String borderTitle = (String) metaProperties.get("short_description");
		if (borderTitle == null)
			borderTitle = SGuiHelper.beautifyName(parameterName);
		TitledBorder border = new TitledBorder(new EtchedBorder(EtchedBorder.LOWERED));
		border.setTitle(borderTitle);
		mainPanel.setBorder(border);
		
		GridBagConstraints g = new GridBagConstraints();
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		add(mainPanel, g);
		
		DefaultTableModel tableModel = new DefaultTableModel(0, 1)
		{
			public boolean isCellEditable(int row, int column)
			{
				return !readOnly;
			}
		};
		if (initialValue != null)
			for (int i = 0; i < initialValue.length; ++i)
				tableModel.addRow(new Object[] {initialValue[i]});
		parameterTable = new JTable(tableModel);
		
		g = new GridBagConstraints();
		g.gridx = 0;
		g.gridy = 0;
		g.gridwidth = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		g.weightx = 1;
		//JScrollPane tablePane = new JScrollPane(parameterTable);
		//add(tablePane, g);
		mainPanel.add(parameterTable, g);
		
		if (!readOnly)
		{
			g = new GridBagConstraints();
			g.gridx = 0;
			g.gridy = 1;
			g.anchor = GridBagConstraints.WEST;
			JButton addButton = new JButton(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					((DefaultTableModel) parameterTable.getModel()).addRow(new Object[] {""});
				}
			});
			addButton.setText("Add");
			addButton.setMargin(new Insets(1, 1, 1, 1));
			mainPanel.add(addButton, g);
			
			g = new GridBagConstraints();
			g.gridx = 1;
			g.gridy = 1;
			g.anchor = GridBagConstraints.WEST;
			JButton removeButton = new JButton(new AbstractAction()
			{
				public void actionPerformed(ActionEvent e)
				{
					int[] rows = parameterTable.getSelectedRows();
					Arrays.sort(rows);
					for (int i = 0; i < rows.length; ++i)
						((DefaultTableModel) parameterTable.getModel()).removeRow(rows[i] - i);
				}
			});
			removeButton.setText("Remove");
			removeButton.setMargin(new Insets(1, 1, 1, 1));
			mainPanel.add(removeButton, g);
		}
	}
	
	public boolean isParameterValueValid()
	{
		/*if (parameterField.getText().isEmpty())
		{
			parameterField.setBackground(Color.RED);
			return false;
		}*/
		return true;
	}
	
	public boolean requiresLabel()
	{
		return false;
	}
	
	public Object getParameterValue()
	{
		String[] ret = new String[parameterTable.getRowCount()];
		for (int i = 0; i < ret.length; ++i)
			ret[i] = (String) parameterTable.getValueAt(i, 0);
		return ret;
	}
}
