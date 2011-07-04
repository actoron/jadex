package jadex.wfms.client.standard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

public class ProcessModelTreeComponent extends JPanel
{
	private static final String START_BUTTON_LABEL = "Start";
	
	private static final String ADD_PROCESS_BUTTON_LABEL = "Add...";
	
	private static final String REMOVE_PROCESS_BUTTON_LABEL = "Remove";
	
	/** Tree listing the process model names */
	private JTree processTree;
	
	/** Current process table mouse listener */
	private MouseListener processMouseListener;
	
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
	
	public ProcessModelTreeComponent()
	{
		super(new GridBagLayout());
		
		processTree = new JTree(new DefaultMutableTreeNode("Root"));
		processTree.setRootVisible(false);
		JScrollPane processScrollPane = new JScrollPane(processTree);
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
	public void setProcessModelNames(Set processModelNames)
	{
		clear();
		
		for (Iterator it = processModelNames.iterator(); it.hasNext(); )
			addProcessModelName((String) it.next());
	}
	
	public void addProcessModelName(String name)
	{
		StringTokenizer tok = new StringTokenizer(name, "/");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) processTree.getModel().getRoot();
		do
		{
			String pathComponent = tok.nextToken();
			int childIndex = getPathChildNode(node, pathComponent);
			if (childIndex >= 0)
				node = (DefaultMutableTreeNode) node.getChildAt(childIndex);
			else
			{
				if (tok.hasMoreTokens())
				{
					DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(pathComponent);
					node.add(tmpNode);
					((DefaultTreeModel) processTree.getModel()).nodesWereInserted(node, new int[] {node.getIndex(tmpNode)});
					node = tmpNode;
				}
				else
				{
					DefaultMutableTreeNode tmpNode = new DefaultMutableTreeNode(new ProcessPath(pathComponent, name));
					node.add(tmpNode);
					((DefaultTreeModel) processTree.getModel()).nodesWereInserted(node, new int[] {node.getIndex(tmpNode)});
				}
				
				//if (node.equals(((DefaultMutableTreeNode) processTree.getModel().getRoot())))
				processTree.expandPath(new TreePath(((DefaultMutableTreeNode) processTree.getModel().getRoot()).getPath()));
			}
		}
		while (tok.hasMoreTokens());
	}
	
	public void removeProcessModelName(String name)
	{
		StringTokenizer tok = new StringTokenizer(name, "/");
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) processTree.getModel().getRoot();
		do
		{
			String pathComponent = tok.nextToken();
			int childIndex = getPathChildNode(node, pathComponent);
			if (childIndex >= 0)
				node = (DefaultMutableTreeNode) node.getChildAt(childIndex);
			else
				return;
		}
		while (tok.hasMoreTokens());
		int index = 0;
		DefaultMutableTreeNode child = null;
		while ((!processTree.getModel().getRoot().equals(node)) && node.getChildCount() == 0)
		{
			child = node;
			node = (DefaultMutableTreeNode) node.getParent();
			index = node.getIndex(child);
			node.remove(child);
		}
		((DefaultTreeModel) processTree.getModel()).nodesWereRemoved(node, new int[] {index}, new Object[] {child});//((DefaultMutableTreeNode) processTree.getModel().getRoot());
	}
	
	/**
	 * Returns the current selected process model name.
	 * @return currently selected process model name
	 */
	public String getSelectedModelName()
	{
		if (processTree.getSelectionPath() == null)
			return null;
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) processTree.getSelectionPath().getLastPathComponent();
		if (node.getUserObject() instanceof ProcessPath)
			return ((ProcessPath) node.getUserObject()).getPath();
		return null;
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
			processTree.removeMouseListener(processMouseListener);
		
		processMouseListener = new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 2)
					action.actionPerformed(new ActionEvent(e, e.getID(), null));
			}
		};
		
		processTree.addMouseListener(processMouseListener);
		
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
		((DefaultTreeModel) processTree.getModel()).setRoot(new DefaultMutableTreeNode("Root"));
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
		
		AbstractAction expandaction = new AbstractAction("Expand All")
		{
			public void actionPerformed(ActionEvent e)
			{
				for (int i = 0; i < processTree.getRowCount(); ++i)
					processTree.expandRow(i);
			}
		};
		JMenuItem expandmenu = new JMenuItem("Expand All");
		expandmenu.setAction(expandaction);
		fullMenu.add(expandmenu);
		expandmenu = new JMenuItem("Expand All");
		expandmenu.setAction(expandaction);
		reducedMenu.add(expandmenu);
		
		processTree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if ((e.getButton() == MouseEvent.BUTTON3) &&
					(e.getClickCount() == 1))
				{
					TreePath path = processTree.getPathForLocation(e.getX(), e.getY());
					processTree.setSelectionPath(path);
					if (getSelectedModelName() == null)
						reducedMenu.show(processTree, e.getX(), e.getY());
					else
						fullMenu.show(processTree, e.getX(), e.getY());
				}
			}
		});
	}
	
	private static int getPathChildNode(DefaultMutableTreeNode parent, String pathComponent)
	{
		for (int i = 0; i < parent.getChildCount(); ++i)
			if (parent.getChildAt(i).toString().equals(pathComponent))
				return i;
		return -1;
	}
	
	private static class ProcessPath
	{
		private String path;
		private String lastPathComponent;
		
		public ProcessPath(String pathComponent, String path)
		{
			this.path = path;
			this.lastPathComponent = pathComponent;
		}
		
		public String getPath()
		{
			return path;
		}
		
		public String toString()
		{
			return lastPathComponent;
		}
	}
}
