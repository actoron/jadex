package jadex.wfms.client.standard;

import jadex.wfms.service.ProcessResourceInfo;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Set;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

public class ProcessModelComponent extends JPanel
{
	private static final String PROCESS_MODEL_COLUMN_NAME = "Process Models";
	
	private static final String START_BUTTON_LABEL = "Start";
	
	private static final String ADD_PROCESS_BUTTON_LABEL = "Add...";
	
	private static final String REMOVE_PROCESS_BUTTON_LABEL = "Remove";
	
	/** Table listing the process model names */
	private JTable processTable;
	
	/** Current process table mouse listener */
	private MouseListener processMouseListener;
	
	/** Model for the table listing the process model names */
	private ProcessModelTableModel processTableModel;
	
	/** Start process button */
	private JButton startButton;
	
	/** Add process button */
	private JButton addProcessButton;
	
	/** Remove process button */
	private JButton removeProcessButton;
	
	/** Add Menu item for the full menu */
	private JMenuItem addMenu;
	
	/** Remove Menu item for the full menu */
	private JMenuItem removeMenu;
	
	/** Start Menu item for the full menu */
	private JMenuItem startMenu;
	
	/** Add Menu item for the reduced menu */
	private JMenuItem reducedAddMenu;
	
	public ProcessModelComponent()
	{
		super(new GridBagLayout());
		processTableModel = new ProcessModelTableModel();
		//processTableModel.setColumnIdentifiers(new Object[] {PROCESS_MODEL_COLUMN_NAME});
		
		processTable = new JTable(processTableModel)
		{
			public boolean isCellEditable(int row, int column)
			{
				return false;
			}
		};
		processTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		((DefaultTableColumnModel) processTable.getColumnModel()).getColumn(0);
		JScrollPane processScrollPane = new JScrollPane(processTable);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.BOTH;
		gbc.gridy = 0;
		gbc.gridwidth = 3;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		add(processScrollPane, gbc);
		
		createMenus();
		
		JPanel buttonPanel = new JPanel(new GridBagLayout());
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridy = 1;
		gbc.weightx = 1;
		gbc.anchor = GridBagConstraints.SOUTH;
		add(buttonPanel, gbc);
		
		JPanel buttonFiller = new JPanel();
		gbc = new GridBagConstraints();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1;
		buttonPanel.add(buttonFiller, gbc);
		
		addProcessButton = new JButton(ADD_PROCESS_BUTTON_LABEL);
		addProcessButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.EAST;
		buttonPanel.add(addProcessButton, gbc);
		
		removeProcessButton = new JButton(REMOVE_PROCESS_BUTTON_LABEL);
		removeProcessButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.gridx = 2;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.EAST;
		buttonPanel.add(removeProcessButton, gbc);
		
		startButton = new JButton(START_BUTTON_LABEL);
		startButton.setMargin(new Insets(1, 1, 1, 1));
		gbc = new GridBagConstraints();
		gbc.gridx = 3;
		gbc.insets = new Insets(5, 5, 5, 5);
		gbc.anchor = GridBagConstraints.EAST;
		buttonPanel.add(startButton, gbc);
	}
	
	/**
	 * Sets the listed process model names, deleting the previous list.
	 * @param processModelNames new set of process model names
	 */
	public void setProcessModels(Set<ProcessResourceInfo> processmodels)
	{
		processTableModel.clear();
		
		for (Iterator<ProcessResourceInfo> it = processmodels.iterator(); it.hasNext(); )
		{
			processTableModel.addProcessModel(it.next());
		}
	}
	
	public void addProcessModel(ProcessResourceInfo info)
	{
		processTableModel.addProcessModel(info);
	}
	
	public void removeProcessModel(ProcessResourceInfo info)
	{
		processTableModel.removeProcessModel(info);
	}
	
	/**
	 * Returns the current selected process model name.
	 * @return currently selected process model name
	 */
	public ProcessResourceInfo getSelectedModel()
	{
		int row = processTable.getSelectedRow();
		return processTableModel.getModelAt(row);
	}
	
	/**
	 * Sets the action for the start button.
	 * @param action action for the start button
	 */
	public void setStartAction(final Action action)
	{
		action.putValue(Action.NAME, START_BUTTON_LABEL);
		startButton.setAction(action);
		
		if (processMouseListener != null)
			processTable.removeMouseListener(processMouseListener);
		
		processMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
				{
					action.actionPerformed(new ActionEvent(e, e.getID(), null));
				}
			}
		};
		
		processTable.addMouseListener(processMouseListener);
		
		startMenu.setAction(action);
	}
	
	/**
	 * Sets the action for the add process button.
	 * @param action action for the add process button
	 */
	public void setAddProcessAction(final Action action)
	{
		action.putValue(Action.NAME, ADD_PROCESS_BUTTON_LABEL);
		addProcessButton.setAction(action);
		addMenu.setAction(action);
		reducedAddMenu.setAction(action);
	}
	
	/**
	 * Sets the action for the remove process button.
	 * @param action action for the remove process button
	 */
	public void setRemoveProcessAction(final Action action)
	{
		action.putValue(Action.NAME, REMOVE_PROCESS_BUTTON_LABEL);
		removeProcessButton.setAction(action);
		removeMenu.setAction(action);
	}
	
	/**
	 * Clears the model list
	 */
	public void clear()
	{
		processTableModel.clear();
	}
	
	private void createMenus()
	{
		final JPopupMenu reducedMenu = new JPopupMenu();
		reducedAddMenu = new JMenuItem(ADD_PROCESS_BUTTON_LABEL);
		reducedMenu.add(reducedAddMenu);
		
		final JPopupMenu fullMenu = new JPopupMenu();
		startMenu = new JMenuItem(START_BUTTON_LABEL);
		addMenu = new JMenuItem(ADD_PROCESS_BUTTON_LABEL);
		removeMenu = new JMenuItem(REMOVE_PROCESS_BUTTON_LABEL);
		fullMenu.add(startMenu);
		fullMenu.add(addMenu);
		fullMenu.add(removeMenu);
		
		processTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getButton() == MouseEvent.BUTTON3) &&
					(e.getClickCount() == 1))
				{
					int row = processTable.rowAtPoint(new Point(e.getX(), e.getY()));
					
					if (row < 0)
						reducedMenu.show(processTable, e.getX(), e.getY());
					else
					{
						processTable.changeSelection(row, 0, false, false);
						fullMenu.show(processTable, e.getX(), e.getY());
					}
				}
			}
		});
	}
}
