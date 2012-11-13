package jadex.wfms.simulation.stateset.gui;

import jadex.wfms.guicomponents.ButtonPanel;
import jadex.wfms.parametertypes.Document;
import jadex.wfms.simulation.Scenario;
import jadex.wfms.simulation.gui.SimulationWindow;
import jadex.wfms.simulation.stateset.DocumentStateSet;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

public class DocumentStatePanel extends JPanel implements IStatePanel
{
	private JTable documentTable;
	
	private JButton addButton;
	private JButton randomButton;
	private JButton removeButton;
	
	private String taskName;
	private String parameterName;
	private SimulationWindow simWindow;
	
	public DocumentStatePanel(String tskName, String paramtrName, SimulationWindow simWdw)
	{
		super(new GridBagLayout());
		this.taskName = tskName;
		this.parameterName = paramtrName;
		this.simWindow = simWdw;
		
		GridBagConstraints g = new GridBagConstraints();
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		JPanel mainPanel = new JPanel(new GridBagLayout());
		add(mainPanel, g);
		
		this.documentTable = new JTable();
		documentTable.setModel(new AbstractTableModel()
		{
			public int getColumnCount()
			{
				return 1;
			}
			
			public int getRowCount()
			{
				Scenario scenario = simWindow.getSelectedScenario();
				if (scenario != null)
					return (int) scenario.getTaskParameter(taskName, parameterName).getStateCount();
				return 0;
			}
			
			public Object getValueAt(int rowIndex, int columnIndex)
			{
				Scenario scenario = simWindow.getSelectedScenario();
				if (scenario != null)
					return ((DocumentStateSet) scenario.getTaskParameter(taskName,parameterName)).getState(rowIndex);
				return null;
			}
			
			public boolean isCellEditable(int rowIndex, int columnIndex)
			{
				return false;
			}
			
			public String getColumnName(int column)
			{
				return "Documents";
			}
			
			
		});
		g = new GridBagConstraints();
		g.gridy = 1;
		g.weightx = 1;
		g.weighty = 1;
		g.fill = GridBagConstraints.BOTH;
		JScrollPane tableScrollPane = new JScrollPane(documentTable);
		mainPanel.add(tableScrollPane, g);
		
		g = new GridBagConstraints();
		g.weightx = 1;
		g.gridy = 2;
		g.fill = GridBagConstraints.HORIZONTAL;
		JPanel buttonPanel = new ButtonPanel();
		mainPanel.add(buttonPanel, g);
		
		addButton = new JButton();
		addButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				Scenario scenario = simWindow.getSelectedScenario();
				if (scenario != null)
				{
					JFileChooser fc = new JFileChooser();
					if (fc.showOpenDialog(DocumentStatePanel.this) == JFileChooser.APPROVE_OPTION)
					{
						File docFile = fc.getSelectedFile();
						try
						{
							DocumentStateSet stateSet = (DocumentStateSet) scenario.getTaskParameter(taskName, parameterName);
							stateSet.addDocument(new Document(docFile));
							((AbstractTableModel) documentTable.getModel()).fireTableRowsInserted((int)stateSet.getStateCount() - 1, (int)stateSet.getStateCount() - 1);
						}
						catch (IOException e1)
						{
							JOptionPane.showMessageDialog(DocumentStatePanel.this, "Document read failed.");
						}
					}
				}
			}
		});
		addButton.setText("Add Document...");
		buttonPanel.add(addButton);
		
		randomButton = new JButton();
		randomButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				Scenario scenario = simWindow.getSelectedScenario();
				if (scenario != null)
					((DocumentStateSet) scenario.getTaskParameter(taskName, parameterName)).addRandom();
			}
		});
		randomButton.setText("Add Random");
		buttonPanel.add(randomButton);
		
		removeButton = new JButton();
		removeButton.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				Scenario scenario = simWindow.getSelectedScenario();
				int[] rows = documentTable.getSelectedRows();
				if (scenario != null && rows.length > 0)
				{
					DocumentStateSet stateSet = (DocumentStateSet) scenario.getTaskParameter(taskName, parameterName);
					Arrays.sort(rows);
					for (int i = 0; i < rows.length; ++i)
					{
						stateSet.removeDocument((Document) documentTable.getValueAt(rows[i] - i, 0));
						((AbstractTableModel) documentTable.getModel()).fireTableRowsDeleted(rows[i] - i, rows[i] - i);
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
		((AbstractTableModel) documentTable.getModel()).fireTableStructureChanged();
		boolean eb = simWindow.getSelectedScenario() != null;
		addButton.setEnabled(eb);
		randomButton.setEnabled(eb);
		removeButton.setEnabled(eb);
	}
}
