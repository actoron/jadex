package jadex.wfms.simulation.gui;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;
import java.util.Set;

import jadex.commons.SGUI;
import jadex.commons.collection.TreeNode;
import jadex.wfms.simulation.Simulator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;

public class SimulationWindow extends JFrame
{
	private static final String PROCESS_MENU_NAME = "Process";
	private static final String SIMULATION_MENU_NAME = "Simulation";
	
	private static final String OPEN_MENU_ITEM_NAME = "Open...";
	private static final String CLOSE_MENU_ITEM_NAME = "Close";
	private static final String EXIT_MENU_ITEM_NAME = "Exit";
	
	private static final String START_MENU_ITEM_NAME = "Start";
	private static final String STOP_MENU_ITEM_NAME = "Stop";
	
	private static final JPanel EMPTY_PANEL = new JPanel();
	
	private static final TreeModel EMPTY_MODEL = new DefaultTreeModel(new DefaultMutableTreeNode("No Process"));
	
	private Simulator simulator;
	
	private JTree processModelTree;
	
	private JMenuBar menuBar;
	
	private JMenuItem openMenuItem;
	
	public SimulationWindow(Simulator simulator)
	{
		super("Process Simulator");
		this.simulator = simulator;
		
		JSplitPane mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		add(mainPane);
		
		processModelTree = new JTree(EMPTY_MODEL);
		JScrollPane treePane = new JScrollPane(processModelTree, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		mainPane.setLeftComponent(treePane);
		//mainPane.setLeftComponent(processModelTree);
		
		mainPane.setRightComponent(EMPTY_PANEL);
		
		buildMenuBar();
		
		pack();
		setSize(600,300);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		mainPane.setDividerLocation(0.4);
	}
	
	private void buildMenuBar()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu processMenu = new JMenu(PROCESS_MENU_NAME);
		processMenu.setMnemonic(KeyEvent.VK_P);
		menuBar.add(processMenu);
		
		openMenuItem = new JMenuItem();
		openMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Open active!");
			}
		});
		openMenuItem.setText(OPEN_MENU_ITEM_NAME);
		openMenuItem.setMnemonic(KeyEvent.VK_O);
		openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.CTRL_MASK));
		processMenu.add(openMenuItem);
		
		JMenuItem closeMenuItem = new JMenuItem();
		closeMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Close active!");
			}
		});
		closeMenuItem.setText(CLOSE_MENU_ITEM_NAME);
		closeMenuItem.setMnemonic(KeyEvent.VK_C);
		closeMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
		processMenu.add(closeMenuItem);
		
		processMenu.addSeparator();
		
		JMenuItem exitMenuItem = new JMenuItem();
		exitMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				SimulationWindow.this.dispose();
			}
		});
		exitMenuItem.setText(EXIT_MENU_ITEM_NAME);
		exitMenuItem.setMnemonic(KeyEvent.VK_X);
		exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
		processMenu.add(exitMenuItem);
		
		
		JMenu simMenu = new JMenu(SIMULATION_MENU_NAME);
		simMenu.setMnemonic(KeyEvent.VK_S);
		menuBar.add(simMenu);
		
		JMenuItem startMenuItem = new JMenuItem();
		startMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Start active!");
			}
		});
		startMenuItem.setText(START_MENU_ITEM_NAME);
		startMenuItem.setMnemonic(KeyEvent.VK_S);
		startMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
		simMenu.add(startMenuItem);
		
		JMenuItem stopMenuItem = new JMenuItem();
		stopMenuItem.setAction(new AbstractAction()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.out.println("Stop active!");
			}
		});
		stopMenuItem.setText(STOP_MENU_ITEM_NAME);
		stopMenuItem.setMnemonic(KeyEvent.VK_T);
		stopMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.CTRL_MASK));
		simMenu.add(stopMenuItem);
	}
	
	public String showProcessPickerDialog(Set modelNames)
	{
		return (String)JOptionPane.showInputDialog(this,
                "Pick a process",
                "Processes",
                JOptionPane.PLAIN_MESSAGE,
                null,
                modelNames.toArray(),
                "ham");

	}
	
	public void showMessage(int type, String title, String text)
	{
		JOptionPane.showMessageDialog(this, text, title, type);
	}
	
	public void setOpenAction(Action action)
	{
		setMenuAction(openMenuItem, action);
	}
	
	public void setProcessTreeModel(TreeModel model)
	{
		if (model == null)
			model = EMPTY_MODEL;
		processModelTree.setModel(model);
		int row = 0;
		while (row < processModelTree.getRowCount())
			processModelTree.collapseRow(row++);
		processModelTree.repaint();
	}
	
	private void setMenuAction(JMenuItem menuItem, Action action)
	{
		int mnemonic = menuItem.getMnemonic();
		KeyStroke accelerator = menuItem.getAccelerator();
		String text = menuItem.getText();
		
		menuItem.setAction(action);
		
		menuItem.setMnemonic(mnemonic);
		menuItem.setAccelerator(accelerator);
		menuItem.setText(text);
	}
}
